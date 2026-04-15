package com.ailove.app.model;

public class ChatSession {
    public String sessionId;
    public RecommendUser targetUser;
    public String lastMessage;
    public long lastMessageTime;
    public int unreadCount;
    public boolean aiOnline;
    public boolean isPinned;
}
