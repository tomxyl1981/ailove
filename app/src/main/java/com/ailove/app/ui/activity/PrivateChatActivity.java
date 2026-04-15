package com.ailove.app.ui.activity;

import android.content.Context;
import android.content.Intent;
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

import org.json.JSONArray;
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

public class PrivateChatActivity extends AppCompatActivity {
    private static final String BASE_URL = "https://agentsh.cn:7777/api/v1";
    private static final String APP_ID = "ai_love";
    private static final String MODEL = "qwen2.5-omni-7b";
    private static final String SYSTEM_PROMPT = "你是一位专业的婚恋红娘，现在正在帮助两位用户牵线搭桥。请以红娘的身份参与对话，促进双方了解，分享有趣的话题，帮助他们建立感情。请用温暖亲切的语气交流，保持积极乐观的态度。";
    private static final String EXTRA_TARGET_NAME = "target_name";

    private RecyclerView recyclerView;
    private EditText etMessage;
    private TextView tvTitle;
    private ChatAdapter adapter;
    private List<ChatMessage> messages = new ArrayList<>();
    private String sessionId;
    private String targetName;
    private OkHttpClient httpClient = new OkHttpClient();
    private String userId;

    public static void start(Context context, String sessionId, String targetName) {
        Intent intent = new Intent(context, PrivateChatActivity.class);
        intent.putExtra("session_id", sessionId);
        intent.putExtra(EXTRA_TARGET_NAME, targetName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_chat);

        userId = getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getString("user_id", "user_default");

        sessionId = getIntent().getStringExtra("session_id");
        targetName = getIntent().getStringExtra(EXTRA_TARGET_NAME);

        initViews();
        loadHistory();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        etMessage = findViewById(R.id.et_message);
        tvTitle = findViewById(R.id.tv_title);

        if (targetName != null) {
            tvTitle.setText(targetName);
        }

        adapter = new ChatAdapter(messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        findViewById(R.id.btn_send).setOnClickListener(v -> sendMessage());
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void loadHistory() {
        addMessage(targetName, "你好！很高兴认识你～", false);
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

            RequestBody body = RequestBody.create(
                    jsonBody.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(BASE_URL + "/create_chat")
                    .post(body)
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        findViewById(R.id.btn_send).setEnabled(true);
                        Toast.makeText(PrivateChatActivity.this, "网络请求失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(() -> {
                        findViewById(R.id.btn_send).setEnabled(true);
                    });

                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        try {
                            JSONObject jsonResponse = new JSONObject(responseData);
                            final String reply = jsonResponse.optString("message", "抱歉，回复出错啦～");
                            final String newSessionId = jsonResponse.optString("session_id");

                            if (newSessionId != null && !newSessionId.isEmpty()) {
                                sessionId = newSessionId;
                            }

                            runOnUiThread(() -> {
                                addMessage(targetName, reply, false);
                            });
                        } catch (Exception e) {
                            runOnUiThread(() -> {
                                Toast.makeText(PrivateChatActivity.this, "解析响应失败", Toast.LENGTH_SHORT).show();
                            });
                        }
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(PrivateChatActivity.this, "请求失败: " + response.code(), Toast.LENGTH_SHORT).show();
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
        msg.senderId = isUser ? userId : "target_user";
        msg.timestamp = System.currentTimeMillis();
        messages.add(msg);
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);
    }
}
