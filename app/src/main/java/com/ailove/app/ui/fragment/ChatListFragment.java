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
import com.ailove.app.ui.activity.GroupChatActivity;

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
            GroupChatActivity.start(requireContext(), session.sessionId);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        
        loadChatList();
    }
    
    private void loadChatList() {
        ApiClient.getInstance().getChatList(
            ApiClient.getInstance().getCurrentUserId(),
            new ApiClient.Callback<List<ChatSession>>() {
                @Override
                public void onSuccess(List<ChatSession> result) {
                    sessions.clear();
                    sessions.addAll(result);
                    adapter.notifyDataSetChanged();
                }
                
                @Override
                public void onError(String error) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            }
        );
    }
}
