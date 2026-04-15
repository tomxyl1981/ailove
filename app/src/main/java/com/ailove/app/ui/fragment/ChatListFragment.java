package com.ailove.app.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ailove.app.R;
import com.ailove.app.adapter.ChatSessionAdapter;
import com.ailove.app.api.ApiClient;
import com.ailove.app.model.ChatSession;
import com.ailove.app.ui.activity.AIChatActivity;
import com.ailove.app.ui.activity.PrivateChatActivity;

import java.util.ArrayList;
import java.util.List;

public class ChatListFragment extends Fragment {
    private RecyclerView recyclerView;
    private ChatSessionAdapter adapter;
    private List<ChatSession> sessions = new ArrayList<>();
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        recyclerView = view.findViewById(R.id.recycler_view);
        adapter = new ChatSessionAdapter(sessions, session -> {
            if ("ai_matchmaker".equals(session.sessionId)) {
                startActivity(new android.content.Intent(getContext(), AIChatActivity.class));
            } else if (session.targetUser != null) {
                PrivateChatActivity.start(requireContext(), session.sessionId, session.targetUser.nickname);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        
        loadChatList();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadChatList();
    }
    
    private void loadChatList() {
        sessions.clear();
        
        ChatSession matchmakerSession = new ChatSession();
        matchmakerSession.sessionId = "ai_matchmaker";
        matchmakerSession.targetUser = new com.ailove.app.model.RecommendUser();
        matchmakerSession.targetUser.nickname = "小爱";
        matchmakerSession.targetUser.avatar = "android.resource://com.ailove.app/drawable/ic_avatar_xiaoai";
        matchmakerSession.lastMessage = "点击开始与AI红娘聊天";
        matchmakerSession.lastMessageTime = System.currentTimeMillis();
        matchmakerSession.unreadCount = 0;
        matchmakerSession.aiOnline = true;
        matchmakerSession.isPinned = true;
        sessions.add(matchmakerSession);
        
        adapter.notifyDataSetChanged();
    }
}
