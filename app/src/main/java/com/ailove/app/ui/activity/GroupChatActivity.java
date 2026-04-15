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
import com.ailove.app.adapter.GroupChatAdapter;
import com.ailove.app.api.ApiClient;
import com.ailove.app.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class GroupChatActivity extends AppCompatActivity {
    private static final String EXTRA_SESSION_ID = "session_id";
    
    private RecyclerView recyclerView;
    private EditText etMessage;
    private GroupChatAdapter adapter;
    private List<ChatMessage> messages = new ArrayList<>();
    private String sessionId;
    
    public static void start(Context context, String sessionId) {
        Intent intent = new Intent(context, GroupChatActivity.class);
        intent.putExtra(EXTRA_SESSION_ID, sessionId);
        context.startActivity(intent);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        
        sessionId = getIntent().getStringExtra(EXTRA_SESSION_ID);
        initViews();
        loadMessages();
    }
    
    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        etMessage = findViewById(R.id.et_message);
        
        adapter = new GroupChatAdapter(messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        
        findViewById(R.id.btn_send).setOnClickListener(v -> sendMessage());
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }
    
    private void loadMessages() {
        ApiClient.getInstance().getMessages(sessionId, new ApiClient.Callback<List<ChatMessage>>() {
            @Override
            public void onSuccess(List<ChatMessage> result) {
                messages.addAll(result);
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messages.size() - 1);
                
                addAIMessage("小爱", "欢迎进入2人+1AI的群聊！很高兴为你们牵线搭桥～");
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(GroupChatActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void sendMessage() {
        String content = etMessage.getText().toString().trim();
        if (content.isEmpty()) return;
        
        addMessage("我", content, true);
        etMessage.setText("");
        
        ApiClient.getInstance().sendMessage(
            sessionId,
            ApiClient.getInstance().getCurrentUserId(),
            content,
            new ApiClient.Callback<com.ailove.app.model.MessageResult>() {
                @Override
                public void onSuccess(com.ailove.app.model.MessageResult result) {
                    if (result.aiIntervened && result.aiMessage != null) {
                        addAIMessage("小爱", result.aiMessage);
                    }
                }
                
                @Override
                public void onError(String error) {
                    Toast.makeText(GroupChatActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        );
    }
    
    private void addMessage(String sender, String content, boolean isUser) {
        ChatMessage msg = new ChatMessage();
        msg.senderName = sender;
        msg.content = content;
        msg.senderId = isUser ? ApiClient.getInstance().getCurrentUserId() : "other";
        msg.timestamp = System.currentTimeMillis();
        messages.add(msg);
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);
    }
    
    private void addAIMessage(String sender, String content) {
        ChatMessage msg = new ChatMessage();
        msg.senderName = sender;
        msg.content = content;
        msg.senderId = "ai";
        msg.timestamp = System.currentTimeMillis();
        messages.add(msg);
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);
    }
}
