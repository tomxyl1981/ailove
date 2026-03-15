package com.ailove.app.model;

public class MbtiResult {
    public String id;
    public long timestamp;
    public String mbtiType;
    public String title;
    public String subtitle;
    public String description;
    public String[] matches;
    public int eScore;
    public int iScore;
    public int sScore;
    public int nScore;
    public int tScore;
    public int fScore;
    public int jScore;
    public int pScore;

    public MbtiResult() {
        this.timestamp = System.currentTimeMillis();
        this.id = "mbti_" + timestamp;
    }
}
