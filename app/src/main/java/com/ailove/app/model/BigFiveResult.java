package com.ailove.app.model;

public class BigFiveResult {
    public String id;
    public long timestamp;
    public int openness;
    public int conscientiousness;
    public int extraversion;
    public int agreeableness;
    public int neuroticism;
    public String summary;
    public String matchSuggestion;

    public BigFiveResult() {
        this.timestamp = System.currentTimeMillis();
        this.id = "bigfive_" + timestamp;
    }
}
