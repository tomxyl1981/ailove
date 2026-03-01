package com.ailove.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ailove.app.R;
import com.ailove.app.model.ChatMessage;
import com.ailove.app.api.ApiClient;

import java.util.List;

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.ViewHolder> {
    private List<ChatMessage> messages;
    
    public GroupChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_chat_message, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage msg = messages.get(position);
        holder.tvSender.setText(msg.senderName);
        holder.tvContent.setText(msg.content);
        
        boolean isAI = "ai".equals(msg.senderId);
        boolean isUser = msg.senderId != null && msg.senderId.equals(ApiClient.getInstance().getCurrentUserId());
        
        if (isAI) {
            holder.itemView.setBackgroundResource(R.drawable.bg_chat_ai);
        } else if (isUser) {
            holder.itemView.setBackgroundResource(R.drawable.bg_chat_user);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_chat_other);
        }
    }
    
    @Override
    public int getItemCount() {
        return messages.size();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSender;
        TextView tvContent;
        
        ViewHolder(View itemView) {
            super(itemView);
            tvSender = itemView.findViewById(R.id.tv_sender);
            tvContent = itemView.findViewById(R.id.tv_content);
        }
    }
}
