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
import com.ailove.app.api.ApiClient;
import com.ailove.app.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class AIChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText etMessage;
    private TextView tvProgress;
    private ChatAdapter adapter;
    private List<ChatMessage> messages = new ArrayList<>();
    private String conversationId;
    private int profileProgress = 35;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chat);
        
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
        
        ApiClient.getInstance().sendAIChat(
            ApiClient.getInstance().getCurrentUserId(),
            content,
            conversationId,
            new ApiClient.Callback<com.ailove.app.model.AIChatResult>() {
                @Override
                public void onSuccess(com.ailove.app.model.AIChatResult result) {
                    conversationId = result.conversationId;
                    profileProgress = result.progress;
                    tvProgress.setText("当前完善度：" + profileProgress + "%");
                    addMessage("小爱", result.reply, false);
                }
                
                @Override
                public void onError(String error) {
                    Toast.makeText(AIChatActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        );
    }
    
    private void addMessage(String sender, String content, boolean isUser) {
        ChatMessage msg = new ChatMessage();
        msg.senderName = sender;
        msg.content = content;
        msg.senderId = isUser ? ApiClient.getInstance().getCurrentUserId() : "ai";
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
