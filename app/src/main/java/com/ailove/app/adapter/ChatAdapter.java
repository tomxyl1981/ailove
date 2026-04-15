package com.ailove.app.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ailove.app.R;
import com.ailove.app.model.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<ChatMessage> messages;
    private OnMessageLongClickListener longClickListener;
    
    public interface OnMessageLongClickListener {
        void onMessageLongClick(ChatMessage message, int position);
    }
    
    public void setOnMessageLongClickListener(OnMessageLongClickListener listener) {
        this.longClickListener = listener;
    }
    
    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage msg = messages.get(position);
        holder.tvSender.setText(msg.senderName);
        holder.tvContent.setText(msg.content);
        
        if (msg.isThinking) {
            holder.tvSender.setVisibility(View.GONE);
            holder.tvContent.setTextColor(Color.parseColor("#888888"));
            holder.itemView.setBackgroundResource(R.drawable.bg_chat_ai);
        } else {
            holder.tvSender.setVisibility(View.VISIBLE);
            holder.tvContent.setTextColor(Color.parseColor("#333333"));
            boolean isAIMessage = msg.senderId != null && msg.senderId.equals("ai");
            holder.itemView.setBackgroundResource(isAIMessage ? R.drawable.bg_chat_ai : R.drawable.bg_chat_user);
            
            if (isAIMessage && longClickListener != null) {
                holder.itemView.setOnLongClickListener(v -> {
                    longClickListener.onMessageLongClick(msg, position);
                    return true;
                });
            }
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
