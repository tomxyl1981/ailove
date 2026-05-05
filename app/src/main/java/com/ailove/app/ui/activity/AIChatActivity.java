package com.ailove.app.ui.activity;

import android.app.AlertDialog;
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
import com.ailove.app.storage.ChatHistoryStorage;
import com.ailove.app.utils.QuestionFlowManager;

import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.Call;
import java.io.IOException;

public class AIChatActivity extends AppCompatActivity {
    private static final String BASE_URL_DEEPSEEK = "https://jiehun.mynatapp.cc/deepseek";
    private static final String BASE_URL_DOUBAO = "https://jiehun.mynatapp.cc/v1/chat/completions";
    private static final String DEFAULT_BASE_URL = BASE_URL_DOUBAO;
    private static final String API_KEY = "ailove ailove";
    private static final String MODEL_DEEPSEEK = "deepseek-reasoner";
    private static final String MODEL_DOUBAO = "doubao-seed-2-0-mini-260215";
    private static final String APP_ID = "ai_love";
    private static final String MODEL = "ailove";
    
    private String systemPrompt = "你是一位精通男女结婚匹配的专业红娘，名叫\"小爱\"。你的任务是用温暖、亲切的语气帮助用户分析情感问题、了解择偶需求、牵线搭桥。" +
            "你可以询问用户以下信息来完善用户资料：\n" +
            "1. MBTI人格类型 - 可以通过几个简单问题判断\n" +
            "2. 八字信息 - 出生年月日时\n" +
            "3. 星座信息 - 出生月日\n" +
            "4. 大五人格特质\n" +
            "5. 三观问题 - 对婚姻、家庭、子女的看法\n" +
            "当用户回答了相关问题后，请识别并告知用户你已记录他们的回答。\n" +
            "请始终以红娘的身份和用户交流，用温暖亲切的语气。";

    private RecyclerView recyclerView;
    private EditText etMessage;
    private TextView tvThinking;
    private ChatAdapter adapter;
    private List<ChatMessage> messages = new ArrayList<>();
    private String sessionId;
    private int profileProgress = 35;
    private OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .build();
    private String userId;
    private String userEmail;
    private String sessionToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chat);

        userEmail = getSharedPreferences("ailove_prefs", MODE_PRIVATE)
                .getString("user_email", "");
        sessionToken = getSharedPreferences("ailove_prefs", MODE_PRIVATE)
                .getString("user_token", "");
        userId = getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getString("user_id", "user_" + System.currentTimeMillis());

        getSharedPreferences("user_prefs", MODE_PRIVATE)
                .edit()
                .putString("user_id", userId)
                .apply();

initViews();
        loadChatHistory();
        
        // Sync chat history from server on startup
        syncChatHistoryFromServer();
        
        // Initialize question flow manager
        QuestionFlowManager.getInstance(this).init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveChatHistory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncChatHistoryFromServer();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        etMessage = findViewById(R.id.et_message);
        tvThinking = findViewById(R.id.tv_thinking);

        adapter = new ChatAdapter(messages);
        adapter.setOnMessageLongClickListener(new ChatAdapter.OnMessageLongClickListener() {
            @Override
            public void onMessageLongClick(ChatMessage message, int position) {
                if ("ai".equals(message.senderId)) {
                    showDeleteMessageDialog(position);
                }
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        findViewById(R.id.btn_send).setOnClickListener(v -> sendMessage());
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void loadChatHistory() {
        List<ChatMessage> history = ChatHistoryStorage.loadChatHistory(this, userEmail);
        if (history.isEmpty()) {
            addMessage("小爱", "您好，我是您的专属小爱，我会帮您找到真爱：）", false);
        } else {
            messages.addAll(history);
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(messages.size() - 1);
        }
    }

    private void saveChatHistory() {
        List<ChatMessage> toSave = new ArrayList<>();
        for (ChatMessage msg : messages) {
            if (!msg.isThinking) {
                toSave.add(msg);
            }
        }
        ChatHistoryStorage.saveChatHistory(this, toSave, userEmail);
    }

    private int selectedMessagePosition = -1;
    
    private void showDeleteMessageDialog(int position) {
        selectedMessagePosition = position;
        new AlertDialog.Builder(this)
                .setTitle("删除消息")
                .setMessage("确定要删除这条消息吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    if (selectedMessagePosition >= 0 && selectedMessagePosition < messages.size()) {
                        messages.remove(selectedMessagePosition);
                        adapter.notifyItemRemoved(selectedMessagePosition);
                        saveChatHistory();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void sendMessage() {
        String content = etMessage.getText().toString().trim();
        if (content.isEmpty()) {
            return;
        }

        addMessage("我", content, true);
        
        // Detect and save test answers
        detectAndSaveTestAnswer(content);
        
        etMessage.setText("");

        addThinkingMessage();

        findViewById(R.id.btn_send).setEnabled(false);

        sendToAPI(content);
    }
    
    private void detectAndSaveTestAnswer(String userMessage) {
        String lowerMsg = userMessage.toLowerCase();
        
        // MBTI detection
        if (lowerMsg.contains("intj") || lowerMsg.contains("intp") || lowerMsg.contains("infp") || 
            lowerMsg.contains("enfp") || lowerMsg.contains("entp") || lowerMsg.contains("isfj") ||
            lowerMsg.contains("istj") || lowerMsg.contains("esfj") || lowerMsg.contains("estj")) {
            String mbtiType = extractMbtiType(userMessage);
            if (mbtiType != null) {
                com.ailove.app.model.MbtiResult result = new com.ailove.app.model.MbtiResult();
                result.mbtiType = mbtiType;
                result.timestamp = System.currentTimeMillis();
                com.ailove.app.storage.TestResultStorage.saveMbtiResult(this, result);
                android.util.Log.d("AIChat_DEBUG", "Saved MBTI result: " + mbtiType);
            }
        }
        
        // BaZi detection - look for year/month/day/time patterns
        if (lowerMsg.contains("年") || lowerMsg.matches(".*19\\d{2}.*") || lowerMsg.matches(".*20\\d{2}.*")) {
            detectBaZiAnswer(userMessage);
        }
        
        // Constellation detection
        String[] constellations = {"白羊", "金牛", "双子", "巨蟹", "狮子", "处女", "天秤", "天蝎", "射手", "摩羯", "水瓶", "双鱼"};
        for (String c : constellations) {
            if (lowerMsg.contains(c)) {
                com.ailove.app.model.ConstellationResult result = new com.ailove.app.model.ConstellationResult();
                result.timestamp = System.currentTimeMillis();
                com.ailove.app.storage.TestResultStorage.saveConstellationResult(this, result);
                android.util.Log.d("AIChat_DEBUG", "Saved Constellation result");
                break;
            }
        }
    }
    
    private String extractMbtiType(String msg) {
        String[] mbtiTypes = {"INTJ", "INTP", "INFJ", "INFP", "ENFJ", "ENFP", "ENTJ", "ENTP", 
                            "ISTJ", "ISFJ", "ESTJ", "ESFJ", "ISTP", "ISFP", "ESTP", "ESFP"};
        msg = msg.toUpperCase();
        for (String type : mbtiTypes) {
            if (msg.contains(type)) {
                return type;
            }
        }
        return null;
    }
    
    private void detectBaZiAnswer(String msg) {
        java.util.regex.Pattern yearPattern = java.util.regex.Pattern.compile("(19|20)\\d{2}");
        java.util.regex.Matcher yearMatcher = yearPattern.matcher(msg);
        if (yearMatcher.find()) {
            com.ailove.app.model.BaZiResult result = new com.ailove.app.model.BaZiResult();
            result.timestamp = System.currentTimeMillis();
            com.ailove.app.storage.TestResultStorage.saveBaZiResult(this, result);
            android.util.Log.d("AIChat_DEBUG", "Saved BaZi result");
        }
    }

    private void addThinkingMessage() {
        tvThinking.setVisibility(View.VISIBLE);
    }

    private void removeThinkingMessage() {
        tvThinking.setVisibility(View.GONE);
    }

    private void sendToAPI(String userMessage) {
        try {
            JSONObject jsonBody = new JSONObject();
            JSONArray messagesArray = new JSONArray();
            
            for (ChatMessage msg : messages) {
                JSONObject msgObj = new JSONObject();
                msgObj.put("role", msg.senderId.equals(userId) ? "user" : "assistant");
                msgObj.put("content", msg.content);
                messagesArray.put(msgObj);
            }
            
            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messagesArray.put(userMsg);
            
            jsonBody.put("messages", messagesArray);
            jsonBody.put("email", userEmail);
            jsonBody.put("stream", false);

            String requestUrl = DEFAULT_BASE_URL;
            android.util.Log.d("AIChat_DEBUG", "请求 URL: " + requestUrl);
            android.util.Log.d("AIChat_DEBUG", "请求参数：" + jsonBody.toString());

            RequestBody body = RequestBody.create(
                    jsonBody.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(requestUrl)
                    .addHeader("Authorization", API_KEY)
                    .post(body)
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    android.util.Log.e("AIChat_DEBUG", "请求失败：" + e.getMessage());
                    runOnUiThread(() -> {
                        removeThinkingMessage();
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
                        removeThinkingMessage();
                        findViewById(R.id.btn_send).setEnabled(true);
                    });

                    if (statusCode == 200) {
                        try {
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            JSONArray choices = jsonResponse.optJSONArray("choices");
                            String reply;
                            if (choices != null && choices.length() > 0) {
                                JSONObject firstChoice = choices.getJSONObject(0);
                                JSONObject message = firstChoice.getJSONObject("message");
                                reply = message.optString("content", "抱歉，回复出错啦～");
                            } else {
                                reply = "抱歉，回复出错啦～";
                            }

                            reply = filterMarkdown(reply);

                            final String finalReply = reply;
                            runOnUiThread(() -> {
                                addMessage("小爱", finalReply, false);
                                
                                if (userEmail != null && !userEmail.isEmpty()) {
                                    com.ailove.app.storage.ChatHistoryStorage.saveSingleChat(
                                        AIChatActivity.this, userEmail, userMessage, finalReply, "doubao", "doubao");
                                }
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

    private String filterMarkdown(String text) {
        if (text == null) return "";
        text = Pattern.compile("```[\\s\\S]*?```").matcher(text).replaceAll("");
        text = Pattern.compile("`[^`]+`").matcher(text).replaceAll("");
        text = Pattern.compile("^#{1,6}\\s+", Pattern.MULTILINE).matcher(text).replaceAll("");
        text = text.replaceAll("\\*\\*([^*]+)\\*\\*", "$1");
        text = text.replaceAll("\\*([^*]+)\\*", "$1");
        text = text.replaceAll("__([^_]+)__", "$1");
        text = text.replaceAll("_([^_]+)_", "$1");
        text = Pattern.compile("^[-*+]\\s+", Pattern.MULTILINE).matcher(text).replaceAll("");
        text = Pattern.compile("^\\d+\\.\\s+", Pattern.MULTILINE).matcher(text).replaceAll("");
        text = text.replaceAll("\\[([^\\]]+)\\]\\([^)]+\\)", "$1");
        return text.trim();
    }

    private void syncChatHistoryFromServer() {
        if (userEmail == null || userEmail.isEmpty()) {
            android.util.Log.d("AIChat_DEBUG", "No email, skipping sync");
            return;
        }

        String syncUrl = "https://jiehun.mynatapp.cc/user/chat-history?email=" + userEmail + "&limit=50&offset=0";
        
        Request request = new Request.Builder()
                .url(syncUrl)
                .get()
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                android.util.Log.e("AIChat_DEBUG", "Sync failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int statusCode = response.code();
                String responseBody = response.body() != null ? response.body().string() : "";
                android.util.Log.d("AIChat_DEBUG", "Sync response: " + statusCode);
                android.util.Log.d("AIChat_DEBUG", "Sync body: " + responseBody);
                
                if (statusCode == 200) {
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        if (jsonResponse.optBoolean("success", false)) {
                            JSONArray historyArray = jsonResponse.optJSONArray("history");
                            if (historyArray != null && historyArray.length() > 0) {
                                List<ChatMessage> serverMessages = new ArrayList<>();
                                
                                for (int i = 0; i < historyArray.length(); i++) {
                                    JSONObject chat = historyArray.getJSONObject(i);
                                    String role = chat.optString("role", "");
                                    String content = chat.optString("content", "").trim();
                                    
                                    if (content.isEmpty()) continue;
                                    
                                    ChatMessage msg = new ChatMessage();
                                    msg.content = content;
                                    
                                    if ("assistant".equals(role)) {
                                        msg.senderId = "ai";
                                        msg.senderName = "小爱";
                                    } else {
                                        msg.senderId = "user";
                                        msg.senderName = "我";
                                    }
                                    
                                    msg.timestamp = System.currentTimeMillis();
                                    serverMessages.add(msg);
                                }
                                
                                com.ailove.app.storage.ChatHistoryStorage.saveChatHistory(
                                    AIChatActivity.this, serverMessages, userEmail);
                                
                                final int totalCount = serverMessages.size();
                                runOnUiThread(() -> {
                                    messages.clear();
                                    messages.addAll(serverMessages);
                                    adapter.notifyDataSetChanged();
                                    recyclerView.scrollToPosition(messages.size() - 1);
                                    android.util.Log.d("AIChat_DEBUG", "Loaded " + totalCount + " messages from server");
                                });
                            }
                        }
                    } catch (Exception e) {
                        android.util.Log.e("AIChat_DEBUG", "Parse sync response failed: " + e.getMessage());
                    }
                }
            }
        });
    }
}
