package com.ailove.app.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ailove.app.R;
import com.ailove.app.adapter.ChatAdapter;
import com.ailove.app.model.ChatMessage;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.Call;
import java.io.IOException;

public class AIChatActivity extends AppCompatActivity {
    private static final String BASE_URL = "https://agentsh.cn:7777/api/v1";
    private static final String APP_ID = "ai_love";
    private static final String MODEL = "qwen2.5-omni-7b";
    private static final String SYSTEM_PROMPT = "你是一位精通男女结婚匹配的专业红娘，名叫\"小爱\"。你的任务是用温暖、亲切的语气帮助用户分析情感问题、了解择偶需求、牵线搭桥。说话要亲切温柔，多用\"呀\"、\"呢\"、\"啦\"等语气词，展现出对婚姻幸福的美好愿景。记住：你是一个有血有肉的红娘阿姨，不是冷冰冰的AI助手。请始终以红娘的身份和用户交流。";

    private RecyclerView recyclerView;
    private EditText etMessage;
    private TextView tvProgress;
    private ChatAdapter adapter;
    private List<ChatMessage> messages = new ArrayList<>();
    private String sessionId;
    private int profileProgress = 35;
    private OkHttpClient httpClient = new OkHttpClient();
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chat);

        userId = getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getString("user_id", "user_" + System.currentTimeMillis());

        getSharedPreferences("user_prefs", MODE_PRIVATE)
                .edit()
                .putString("user_id", userId)
                .apply();

        initViews();
        initChat();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        etMessage = findViewById(R.id.et_message);
        tvProgress = findViewById(R.id.tv_progress);

        adapter = new ChatAdapter(messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        findViewById(R.id.btn_send).setOnClickListener(v -> sendMessage());
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.tv_progress).setOnClickListener(v -> showProfileAnalysis());
    }

    private void initChat() {
        addMessage("小爱", "你好呀！我是AI红娘小爱～很高兴认识你！为了更好地帮你找到合适的伴侣，让我来了解一下你吧～", false);
        addMessage("小爱", "请问你的身高是多少呢？", false);
    }

    private void sendMessage() {
        String content = etMessage.getText().toString().trim();
        if (content.isEmpty()) {
            return;
        }

        addMessage("我", content, true);
        etMessage.setText("");

        findViewById(R.id.btn_send).setEnabled(false);

        sendToAPI(content);
    }

    private void sendToAPI(String userMessage) {
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("app_id", APP_ID);
            jsonBody.put("user_id", userId);
            jsonBody.put("message", userMessage);
            jsonBody.put("channel", "mobile");
            jsonBody.put("model", MODEL);
            jsonBody.put("custom_prompt", SYSTEM_PROMPT);
            if (sessionId != null) {
                jsonBody.put("session_id", sessionId);
            }

            String requestUrl = BASE_URL + "/create_chat";
            android.util.Log.d("AIChat_DEBUG", "请求 URL: " + requestUrl);
            android.util.Log.d("AIChat_DEBUG", "请求参数：" + jsonBody.toString());

            RequestBody body = RequestBody.create(
                    jsonBody.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(requestUrl)
                    .post(body)
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    android.util.Log.e("AIChat_DEBUG", "请求失败：" + e.getMessage());
                    runOnUiThread(() -> {
                        findViewById(R.id.btn_send).setEnabled(true);
                        Toast.makeText(AIChatActivity.this, "网络请求失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    int statusCode = response.code();
                    String responseBody = response.body() != null ? response.body().string() : "";
                    android.util.Log.d("AIChat_DEBUG", "响应状态码：" + statusCode);
                    android.util.Log.d("AIChat_DEBUG", "响应内容：" + responseBody);
                    
                    runOnUiThread(() -> {
                        findViewById(R.id.btn_send).setEnabled(true);
                    });

                    if (statusCode == 200) {
                        try {
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            final String reply = jsonResponse.optString("message", "抱歉，回复出错啦～");
                            final String newSessionId = jsonResponse.optString("session_id");

                            if (newSessionId != null && !newSessionId.isEmpty()) {
                                sessionId = newSessionId;
                            }

                            runOnUiThread(() -> {
                                addMessage("小爱", reply, false);
                            });
                        } catch (Exception e) {
                            android.util.Log.e("AIChat_DEBUG", "解析响应失败：" + e.getMessage());
                            runOnUiThread(() -> {
                                Toast.makeText(AIChatActivity.this, "解析响应失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    } else {
                        android.util.Log.e("AIChat_DEBUG", "HTTP 错误：" + statusCode);
                        runOnUiThread(() -> {
                            Toast.makeText(AIChatActivity.this, "服务器错误：" + statusCode + "\n响应：" + responseBody, Toast.LENGTH_LONG).show();
                        });
                    }
                }
            });
        } catch (Exception e) {
            findViewById(R.id.btn_send).setEnabled(true);
            Toast.makeText(this, "发送失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void addMessage(String sender, String content, boolean isUser) {
        ChatMessage msg = new ChatMessage();
        msg.senderName = sender;
        msg.content = content;
        msg.senderId = isUser ? userId : "ai";
        msg.timestamp = System.currentTimeMillis();
        messages.add(msg);
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);
    }

    private void showProfileAnalysis() {
        if (profileProgress >= 70) {
            ProfileAnalysisActivity.start(this);
        } else {
            Toast.makeText(this, "完善度达到70%后解锁", Toast.LENGTH_SHORT).show();
        }
    }
}
