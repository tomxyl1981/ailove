package com.ailove.app.model;

public class BaZiResult {
    public String id;
    public long timestamp;
    public String myGender;
    public String myBirthYear;
    public String myBirthMonth;
    public String myBirthDay;
    public String myBirthHour;
    public String myLocation;
    public String hasPartner;
    public String partnerBirthYear;
    public String partnerBirthMonth;
    public String partnerBirthDay;
    public String partnerBirthHour;
    public String partnerGender;
    public String[] preferences;
    public int score;
    public String resultType;
    public String analysis;
    public String idealAgeDiff;
    public String idealDirection;
    public String personalityTrait;

    public BaZiResult() {
        this.timestamp = System.currentTimeMillis();
        this.id = "bazi_" + timestamp;
    }
}
