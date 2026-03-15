package com.ailove.app.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.webkit.JavascriptInterface;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.ailove.app.R;
import com.ailove.app.storage.TestResultStorage;
import com.google.gson.Gson;
import java.util.Map;

public class TestWebViewFragment extends Fragment {
    private WebView webView;
    private String testType;
    private String htmlFileName;
    
    public static TestWebViewFragment newInstance(String testType, String htmlFileName) {
        TestWebViewFragment fragment = new TestWebViewFragment();
        Bundle args = new Bundle();
        args.putString("testType", testType);
        args.putString("htmlFileName", htmlFileName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            testType = getArguments().getString("testType");
            htmlFileName = getArguments().getString("htmlFileName");
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
        
        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        
        setupWebView();
        loadHtml();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        
        webView.addJavascriptInterface(new JsInterface(), "Android");
    }

    private void loadHtml() {
        String assetPath = "file:///android_asset/test/" + htmlFileName;
        webView.loadUrl(assetPath);
    }

    private class JsInterface {
        @JavascriptInterface
        public void saveResult(String jsonResult) {
            if (getContext() == null) return;
            
            try {
                Gson gson = new Gson();
                Map<String, Object> data = gson.fromJson(jsonResult, Map.class);
                
                switch (testType) {
                    case "bazi":
                        com.ailove.app.model.BaZiResult result = gson.fromJson(jsonResult, com.ailove.app.model.BaZiResult.class);
                        TestResultStorage.saveBaZiResult(getContext(), result);
                        break;
                    case "mbti":
                        com.ailove.app.model.MbtiResult mbtiResult = gson.fromJson(jsonResult, com.ailove.app.model.MbtiResult.class);
                        TestResultStorage.saveMbtiResult(getContext(), mbtiResult);
                        break;
                    case "constellation":
                        com.ailove.app.model.ConstellationResult constellationResult = gson.fromJson(jsonResult, com.ailove.app.model.ConstellationResult.class);
                        TestResultStorage.saveConstellationResult(getContext(), constellationResult);
                        break;
                    case "bigfive":
                        com.ailove.app.model.BigFiveResult bigFiveResult = gson.fromJson(jsonResult, com.ailove.app.model.BigFiveResult.class);
                        TestResultStorage.saveBigFiveResult(getContext(), bigFiveResult);
                        break;
                }
                
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
