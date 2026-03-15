package com.ailove.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ailove.app.R;
import com.ailove.app.model.ChatSession;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatSessionAdapter extends RecyclerView.Adapter<ChatSessionAdapter.ViewHolder> {
    private List<ChatSession> sessions;
    private OnSessionClickListener listener;
    
    public interface OnSessionClickListener {
        void onSessionClick(ChatSession session);
    }
    
    public ChatSessionAdapter(List<ChatSession> sessions, OnSessionClickListener listener) {
        this.sessions = sessions;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_session, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatSession session = sessions.get(position);
        holder.itemView.setOnClickListener(v -> listener.onSessionClick(session));
        
        String avatarUrl = session.targetUser.avatar;
        if (avatarUrl != null && !avatarUrl.trim().isEmpty()) {
            // 检查是否是系统资源URI
            if (avatarUrl.startsWith("android.resource://")) {
                // 直接使用本地资源ID显示小爱头像
                holder.ivAvatar.setImageResource(R.drawable.ic_avatar_xiaoai);
            } else {
                Picasso.get().load(avatarUrl).into(holder.ivAvatar);
            }
        } else {
            Picasso.get().load(R.drawable.ic_default_avatar).into(holder.ivAvatar);
        }
        holder.tvName.setText(session.targetUser.nickname);
        holder.tvLastMessage.setText(session.lastMessage);
        
        if (session.aiOnline) {
            holder.tvAIStatus.setVisibility(View.VISIBLE);
        } else {
            holder.tvAIStatus.setVisibility(View.GONE);
        }
    }
    
    @Override
    public int getItemCount() {
        return sessions.size();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvName;
        TextView tvLastMessage;
        TextView tvAIStatus;
        
        ViewHolder(View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvName = itemView.findViewById(R.id.tv_name);
            tvLastMessage = itemView.findViewById(R.id.tv_last_message);
            tvAIStatus = itemView.findViewById(R.id.tv_ai_status);
        }
    }
}
