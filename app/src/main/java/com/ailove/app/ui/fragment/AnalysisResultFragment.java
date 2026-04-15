package com.ailove.app.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.ailove.app.R;
import org.json.JSONObject;
import okhttp3.*;
import java.io.IOException;

public class AnalysisResultFragment extends Fragment {
    private static final String TAG = "AnalysisResultFragment";
    private static final String BASE_URL = "https://jiehun.mynatapp.cc";
    private static final String ANALYSIS_URL = BASE_URL + "/user/test-analysis";
    private static final String ARG_TEST_TYPE = "testType";
    
    private TextView tvLoading;
    private WebView webView;
    private String userToken;
    private String testType;
    private OkHttpClient httpClient = new OkHttpClient();

    public static AnalysisResultFragment newInstance(String testType) {
        AnalysisResultFragment fragment = new AnalysisResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEST_TYPE, testType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            testType = getArguments().getString(ARG_TEST_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_analysis_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        tvLoading = view.findViewById(R.id.tv_loading);
        webView = view.findViewById(R.id.web_view);
        
        userToken = requireContext().getSharedPreferences("ailove_prefs", android.content.Context.MODE_PRIVATE)
                .getString("user_token", "");
        
        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        
        loadAnalysis();
    }
    
    private void loadAnalysis() {
        if (userToken.isEmpty()) {
            Toast.makeText(requireContext(), "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        
        tvLoading.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);
        
        Request request = new Request.Builder()
                .url(ANALYSIS_URL)
                .addHeader("X-Session-Token", userToken)
                .get()
                .build();
        
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() -> {
                    tvLoading.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "加载失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
            
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String respBody = response.body() != null ? response.body().string() : "";
                Log.d(TAG, "分析响应: " + response.code() + " " + respBody);
                
                if (response.code() == 200) {
                    try {
                        JSONObject json = new JSONObject(respBody);
                        JSONObject analyses = json.optJSONObject("analyses");
                        String html = generateHtml(analyses);
                        requireActivity().runOnUiThread(() -> {
                            tvLoading.setVisibility(View.GONE);
                            webView.setVisibility(View.VISIBLE);
                            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "解析分析结果失败", e);
                        requireActivity().runOnUiThread(() -> {
                            tvLoading.setVisibility(View.GONE);
                            Toast.makeText(requireContext(), "解析失败", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    requireActivity().runOnUiThread(() -> {
                        tvLoading.setVisibility(View.GONE);
                        Toast.makeText(requireContext(), "服务器错误: " + response.code(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
    
    private String generateHtml(JSONObject analyses) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head>");
        sb.append("<meta charset='UTF-8'>");
        sb.append("<style>");
        sb.append("body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; padding: 16px; background: #f5f5f5; }");
        sb.append(".card { background: white; border-radius: 12px; padding: 16px; margin-bottom: 16px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }");
        sb.append(".title { font-size: 18px; font-weight: bold; color: #262626; margin-bottom: 12px; }");
        sb.append(".content { font-size: 14px; color: #666; line-height: 1.6; }");
        sb.append(".empty { text-align: center; color: #999; padding: 40px; }");
        sb.append("</style></head><body>");
        
        if (analyses == null || testType == null) {
            sb.append("<div class='empty'>暂无分析数据</div>");
        } else {
            String[] testNames = {"mbti", "bigfive", "constellation", "bazi"};
            String[] testTitles = {"MBTI 性格分析", "大五人格分析", "星座匹配分析", "八字命理分析"};
            
            int testIndex = -1;
            for (int i = 0; i < testNames.length; i++) {
                if (testNames[i].equals(testType)) {
                    testIndex = i;
                    break;
                }
            }
            
            if (testIndex >= 0) {
                JSONObject testAnalysis = analyses.optJSONObject(testType);
                if (testAnalysis != null && testAnalysis.optBoolean("is_analyzed", false)) {
                    String analysis = testAnalysis.optString("analysis", "");
                    String analyzedAt = testAnalysis.optString("analyzed_at", "");
                    sb.append("<div class='card'>");
                    sb.append("<div class='title'>").append(testTitles[testIndex]).append("</div>");
                    sb.append("<div class='content'>").append(analysis.replace("\n", "<br>")).append("</div>");
                    if (!analyzedAt.isEmpty()) {
                        sb.append("<div class='content' style='color:#999;font-size:12px;margin-top:8px;'>分析时间: ").append(analyzedAt).append("</div>");
                    }
                    sb.append("</div>");
                } else {
                    sb.append("<div class='empty'>暂无").append(testTitles[testIndex]).append("数据</div>");
                }
            } else {
                sb.append("<div class='empty'>暂无分析数据</div>");
            }
        }
        
        sb.append("</body></html>");
        return sb.toString();
    }
}
