package com.ailove.app.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.ailove.app.R;
import com.ailove.app.model.BaZiResult;
import com.ailove.app.ui.fragment.AnalysisResultFragment;
import com.ailove.app.model.BigFiveResult;
import com.ailove.app.model.ConstellationResult;
import com.ailove.app.model.MbtiResult;
import com.ailove.app.storage.TestResultStorage;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.util.List;
import okhttp3.*;
import java.io.IOException;

public class TestHistoryFragment extends Fragment {

    private static final String TAG = "TestHistoryFragment";
    private static final String BASE_URL = "https://jiehun.mynatapp.cc";
    private static final String SYNC_URL = BASE_URL + "/user/test-results";

    private LinearLayout historyContainer;
    private TextView tvSyncStatus;
    private Button btnAnalysis;
    private Gson gson = new Gson();
    private String filterType;
    private String userEmail;
    private String userToken;
    private OkHttpClient httpClient = new OkHttpClient();

    public static TestHistoryFragment newInstance(String testType) {
        TestHistoryFragment fragment = new TestHistoryFragment();
        Bundle args = new Bundle();
        args.putString("filterType", testType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            filterType = getArguments().getString("filterType");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        historyContainer = view.findViewById(R.id.history_container);
        tvSyncStatus = view.findViewById(R.id.tv_sync_status);
        btnAnalysis = view.findViewById(R.id.btn_analysis);
        
        userEmail = requireContext().getSharedPreferences("ailove_prefs", android.content.Context.MODE_PRIVATE)
                .getString("user_email", "");
        userToken = requireContext().getSharedPreferences("ailove_prefs", android.content.Context.MODE_PRIVATE)
                .getString("user_token", "");
        
        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        
        btnAnalysis.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.plaza_container, AnalysisResultFragment.newInstance(filterType))
                .addToBackStack(null)
                .commit();
        });
        
        loadHistory();
    }

    private void loadHistory() {
        // 先显示本地数据
        displayLocalHistory();
        
        // 再同步到服务器
        syncToServer();
    }

    private void displayLocalHistory() {
        historyContainer.removeAllViews();
        
        boolean hasData = false;
        
        if (filterType == null || filterType.equals("mbti")) {
            List<MbtiResult> mbtiResults = TestResultStorage.loadMbtiResults(requireContext());
            for (int i = mbtiResults.size() - 1; i >= 0; i--) {
                MbtiResult result = mbtiResults.get(i);
                addResultItem("mbti", "https://jiehun.mynatapp.cc/test/mbti.html", result, 
                    "MBTI 性格测试", result.mbtiType + " - " + result.title, 
                    formatTime(result.timestamp), result.description);
                hasData = true;
            }
        }
        
        if (filterType == null || filterType.equals("bazi")) {
            List<BaZiResult> baziResults = TestResultStorage.loadBaZiResults(requireContext());
            for (int i = baziResults.size() - 1; i >= 0; i--) {
                BaZiResult result = baziResults.get(i);
                String title = "yes".equals(result.hasPartner) ? "八字合盘分析" : "命定伴侣画像";
                String subtitle = "yes".equals(result.hasPartner) ? "匹配度: " + result.score + "%" : result.personalityTrait;
                addResultItem("bazi", "https://jiehun.mynatapp.cc/test/bazi.html", result,
                    "八字匹配测试", title, formatTime(result.timestamp), subtitle);
                hasData = true;
            }
        }
        
        if (filterType == null || filterType.equals("constellation")) {
            List<ConstellationResult> constellationResults = TestResultStorage.loadConstellationResults(requireContext());
            for (int i = constellationResults.size() - 1; i >= 0; i--) {
                ConstellationResult result = constellationResults.get(i);
                addResultItem("constellation", "https://jiehun.mynatapp.cc/test/constellation.html", result,
                    "星座匹配测试", result.selfZodiac, 
                    formatTime(result.timestamp), "匹配度: " + result.matchScore + "%");
                hasData = true;
            }
        }
        
        if (filterType == null || filterType.equals("bigfive")) {
            List<BigFiveResult> bigFiveResults = TestResultStorage.loadBigFiveResults(requireContext());
            for (int i = bigFiveResults.size() - 1; i >= 0; i--) {
                BigFiveResult result = bigFiveResults.get(i);
                String subtitle = "开放性:" + result.openness + "% 尽责性:" + result.conscientiousness + "%";
                addResultItem("bigfive", "http://localhost:7777/test/bigfive.html", result,
                    "五大人格测试", subtitle, formatTime(result.timestamp), result.summary);
                hasData = true;
            }
        }
        
        if (!hasData) {
            TextView emptyView = new TextView(requireContext());
            emptyView.setText("暂无测试记录\n完成测试后可在此查看");
            emptyView.setTextColor(getResources().getColor(R.color.text_hint));
            emptyView.setTextSize(14);
            emptyView.setGravity(android.view.Gravity.CENTER);
            emptyView.setPadding(0, 100, 0, 0);
            historyContainer.addView(emptyView);
        }
    }

    private void syncToServer() {
        Log.d(TAG, "syncToServer called, email=" + userEmail + ", token=" + userToken);
        
        if (userEmail.isEmpty() || userToken.isEmpty()) {
            Log.d(TAG, "未登录，跳过同步");
            return;
        }

        // 读取本地测试结果
        List<MbtiResult> mbtiResults = TestResultStorage.loadMbtiResults(requireContext());
        List<BigFiveResult> bigFiveResults = TestResultStorage.loadBigFiveResults(requireContext());
        List<ConstellationResult> constellationResults = TestResultStorage.loadConstellationResults(requireContext());
        List<BaZiResult> baziResults = TestResultStorage.loadBaZiResults(requireContext());
        
        Log.d(TAG, "本地测试结果数量: mbti=" + mbtiResults.size() + ", bigfive=" + bigFiveResults.size() 
                + ", constellation=" + constellationResults.size() + ", bazi=" + baziResults.size());

        // 构造同步JSON
        JSONObject syncData = new JSONObject();
        try {
            if (!mbtiResults.isEmpty()) {
                syncData.put("mbti", new JSONObject(gson.toJson(mbtiResults.get(mbtiResults.size() - 1))));
            }
            if (!bigFiveResults.isEmpty()) {
                syncData.put("bigfive", new JSONObject(gson.toJson(bigFiveResults.get(bigFiveResults.size() - 1))));
            }
            if (!constellationResults.isEmpty()) {
                syncData.put("constellation", new JSONObject(gson.toJson(constellationResults.get(constellationResults.size() - 1))));
            }
            if (!baziResults.isEmpty()) {
                syncData.put("bazi", new JSONObject(gson.toJson(baziResults.get(baziResults.size() - 1))));
            }
        } catch (Exception e) {
            Log.e(TAG, "构造同步数据失败", e);
            return;
        }

        if (syncData.length() == 0) {
            Log.d(TAG, "无本地数据，跳过同步");
            return;
        }

        Log.d(TAG, "同步数据: " + syncData.toString());

        RequestBody body = RequestBody.create(syncData.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(SYNC_URL)
                .post(body)
                .addHeader("X-Session-Token", userToken)
                .addHeader("Content-Type", "application/json")
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "同步失败: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String respBody = response.body() != null ? response.body().string() : "";
                Log.d(TAG, "同步响应: " + response.code() + " " + respBody);
                if (response.code() == 200) {
                    try {
                        JSONObject json = new JSONObject(respBody);
                        if (json.optBoolean("success", false)) {
                            Log.d(TAG, "测试结果同步成功");
                            requireActivity().runOnUiThread(() -> {
                                tvSyncStatus.setText("✓ 已同步到服务器");
                                tvSyncStatus.setVisibility(View.VISIBLE);
                            });
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "解析响应失败", e);
                    }
                }
            }
        });
    }

    private void addResultItem(String testType, String htmlFileName, Object resultData, String type, String title, String time, String desc) {
        View item = getLayoutInflater().inflate(R.layout.item_test_history, historyContainer, false);
        
        TextView tvType = item.findViewById(R.id.tv_test_type);
        TextView tvTitle = item.findViewById(R.id.tv_test_title);
        TextView tvTime = item.findViewById(R.id.tv_test_time);
        TextView tvDesc = item.findViewById(R.id.tv_test_desc);
        
        tvType.setText(type);
        tvTitle.setText(title);
        tvTime.setText(time);
        tvDesc.setText(desc);
        
        final String json = gson.toJson(resultData);
        
        item.setOnClickListener(v -> {
            showJsonDialog(title, json);
        });
        
        historyContainer.addView(item);
    }

    private void showJsonDialog(String title, String json) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle(title);
        
        android.widget.TextView textView = new android.widget.TextView(requireContext());
        textView.setText(json);
        textView.setTextSize(12);
        textView.setPadding(40, 20, 40, 20);
        textView.setTypeface(android.graphics.Typeface.MONOSPACE);
        
        android.widget.ScrollView scrollView = new android.widget.ScrollView(requireContext());
        scrollView.addView(textView);
        
        builder.setView(scrollView);
        builder.setPositiveButton("关闭", null);
        builder.show();
    }

    private String formatTime(long timestamp) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date(timestamp));
    }
}
