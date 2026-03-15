package com.ailove.app.model;

public class ConstellationResult {
    public String id;
    public long timestamp;
    public String userName;
    public String gender;
    public String selfZodiac;
    public String[] partnerZodiacs;
    public String[] values;
    public String communication;
    public String[] dating;
    public String conflict;
    public int career;
    public int family;
    public int balance;
    public int spontaneous;
    public int matchScore;
    public String matchTitle;
    public String matchDesc;
    public String[] topMatches;
    public String zodiacTrait;
    public String communicationAdvice;
    public String relationshipAdvice;

    public ConstellationResult() {
        this.timestamp = System.currentTimeMillis();
        this.id = "constellation_" + timestamp;
    }
}
