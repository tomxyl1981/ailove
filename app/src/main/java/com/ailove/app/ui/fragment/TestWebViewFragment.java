package com.ailove.app.ui.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.ailove.app.R;
import com.ailove.app.ui.fragment.AnalysisResultFragment;
import com.ailove.app.storage.TestResultStorage;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import okhttp3.*;
import java.io.IOException;

public class TestWebViewFragment extends Fragment {
    private static final String TAG = "TestWebViewFragment";
    private static final String BASE_URL = "https://jiehun.mynatapp.cc";
    private static final String ANALYSIS_URL = BASE_URL + "/user/test-analysis";
    
    private WebView webView;
    private Button btnAnalysis;
    private String testType;
    private String htmlFileName;
    private String resultJson;
    private String userToken;
    private OkHttpClient httpClient = new OkHttpClient();
    
    public static TestWebViewFragment newInstance(String testType, String htmlFileName) {
        return newInstance(testType, htmlFileName, null);
    }
    
    public static TestWebViewFragment newInstance(String testType, String htmlFileName, String resultJson) {
        TestWebViewFragment fragment = new TestWebViewFragment();
        Bundle args = new Bundle();
        args.putString("testType", testType);
        args.putString("htmlFileName", htmlFileName);
        args.putString("resultJson", resultJson);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            testType = getArguments().getString("testType");
            htmlFileName = getArguments().getString("htmlFileName");
            resultJson = getArguments().getString("resultJson");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test_webview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        webView = view.findViewById(R.id.web_view);
        btnAnalysis = view.findViewById(R.id.btn_analysis);
        
        userToken = requireContext().getSharedPreferences("ailove_prefs", android.content.Context.MODE_PRIVATE)
                .getString("user_token", "");
        
        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        
        btnAnalysis.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.plaza_container, AnalysisResultFragment.newInstance(testType))
                .addToBackStack(null)
                .commit();
        });
        
        setupWebView();
        loadHtml();
    }
    
    private void loadAnalysis() {
        if (userToken.isEmpty()) {
            Toast.makeText(requireContext(), "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        
        btnAnalysis.setEnabled(false);
        btnAnalysis.setText("加载中...");
        
        Request request = new Request.Builder()
                .url(ANALYSIS_URL)
                .addHeader("X-Session-Token", userToken)
                .get()
                .build();
        
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() -> {
                    btnAnalysis.setEnabled(true);
                    btnAnalysis.setText("查看分析");
                    Toast.makeText(requireContext(), "加载失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
            
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String respBody = response.body() != null ? response.body().string() : "";
                Log.d(TAG, "分析响应: " + response.code() + " " + respBody);
                
                requireActivity().runOnUiThread(() -> {
                    btnAnalysis.setEnabled(true);
                    btnAnalysis.setText("查看分析");
                });
                
                if (response.code() == 200) {
                    try {
                        JSONObject json = new JSONObject(respBody);
                        JSONObject analyses = json.optJSONObject("analyses");
                        if (analyses != null && testType != null) {
                            JSONObject testAnalysis = analyses.optJSONObject(testType);
                            if (testAnalysis != null) {
                                String analysis = testAnalysis.optString("analysis", "");
                                boolean isAnalyzed = testAnalysis.optBoolean("is_analyzed", false);
                                requireActivity().runOnUiThread(() -> {
                                    if (isAnalyzed && !analysis.isEmpty()) {
                                        showAnalysisDialog(analysis);
                                    } else {
                                        Toast.makeText(requireContext(), "暂无分析结果", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "解析分析结果失败", e);
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "解析失败", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            }
        });
    }
    
    private void showAnalysisDialog(String analysis) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle("AI 分析结果");
        
        android.widget.TextView textView = new android.widget.TextView(requireContext());
        textView.setText(analysis);
        textView.setTextSize(14);
        textView.setPadding(40, 20, 40, 20);
        
        android.widget.ScrollView scrollView = new android.widget.ScrollView(requireContext());
        scrollView.addView(textView);
        
        builder.setView(scrollView);
        builder.setPositiveButton("关闭", null);
        builder.show();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLoadsImagesAutomatically(true);
        
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(android.webkit.ConsoleMessage consoleMessage) {
                android.util.Log.d("WebViewConsole", consoleMessage.message() + " -- From line " +
                        consoleMessage.lineNumber() + " of " + consoleMessage.sourceId());
                return true;
            }
            
            @Override
            public boolean onJsAlert(WebView view, String url, String message, android.webkit.JsResult result) {
                android.util.Log.d("WebViewAlert", "Alert: " + message);
                result.confirm();
                return true;
            }
        });
        
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                android.util.Log.d("WebViewURL", "Loading URL: " + url);
                return false;
            }
            
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                android.util.Log.d("WebView", "Page finished loading: " + url);
                // 注入错误处理
                webView.evaluateJavascript(
                    "window.onerror = function(msg, url, line) { " +
                    "   console.log('JavaScript Error: ' + msg + ' at line ' + line); " +
                    "   return true; " +
                    "};", null);
                
                // 传递结果数据给JS
                if (resultJson != null && !resultJson.isEmpty()) {
                    webView.postDelayed(() -> {
                        String js = "if(window.loadSavedResult){window.loadSavedResult(" + resultJson + ");}else{console.log('loadSavedResult not found');}";
                        webView.evaluateJavascript(js, null);
                    }, 500);
                }
            }
        });
        
        webView.addJavascriptInterface(new JsInterface(), "Android");
    }

    private void loadHtml() {
        if (htmlFileName.startsWith("http://") || htmlFileName.startsWith("https://")) {
            webView.loadUrl(htmlFileName);
        } else {
            String assetPath = "file:///android_asset/test/" + htmlFileName;
            webView.loadUrl(assetPath);
        }
    }

    private class JsInterface {
        @JavascriptInterface
        public void saveResult(String jsonResult) {
            if (getContext() == null) return;
            
            try {
                Gson gson = new Gson();
                Map<String, Object> data = gson.fromJson(jsonResult, Map.class);
                
                long timestamp = System.currentTimeMillis();

                switch (testType) {
                    case "bazi":
                        com.ailove.app.model.BaZiResult result = gson.fromJson(jsonResult, com.ailove.app.model.BaZiResult.class);
                        result.timestamp = timestamp;
                        TestResultStorage.saveBaZiResult(getContext(), result);
                        break;
                    case "mbti":
                        com.ailove.app.model.MbtiResult mbtiResult = gson.fromJson(jsonResult, com.ailove.app.model.MbtiResult.class);
                        mbtiResult.timestamp = timestamp;
                        TestResultStorage.saveMbtiResult(getContext(), mbtiResult);
                        break;
                    case "constellation":
                        com.ailove.app.model.ConstellationResult constellationResult = gson.fromJson(jsonResult, com.ailove.app.model.ConstellationResult.class);
                        constellationResult.timestamp = timestamp;
                        TestResultStorage.saveConstellationResult(getContext(), constellationResult);
                        break;
                    case "bigfive":
                        com.ailove.app.model.BigFiveResult bigFiveResult = gson.fromJson(jsonResult, com.ailove.app.model.BigFiveResult.class);
                        bigFiveResult.timestamp = timestamp;
                        TestResultStorage.saveBigFiveResult(getContext(), bigFiveResult);
                        break;
                }

                String resultUrl = com.ailove.app.utils.LocalHttpServerManager.getInstance().getSubmitResultUrl(testType, timestamp);
                okhttp3.RequestBody body = okhttp3.RequestBody.create(jsonResult, okhttp3.MediaType.parse("application/json"));
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(resultUrl)
                        .post(body)
                        .build();
                new okhttp3.OkHttpClient().newCall(request).enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(@NonNull okhttp3.Call call, @NonNull java.io.IOException e) {
                        android.util.Log.e("TestResult", "Failed to save to local server: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws java.io.IOException {
                        android.util.Log.d("TestResult", "Result saved to local server: " + response.code());
                    }
                });
                
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "结果已保存", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        @JavascriptInterface
        public void showToast(String message) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                });
            }
        }
    }

    @Override
    public void onDestroyView() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroyView();
    }
}
