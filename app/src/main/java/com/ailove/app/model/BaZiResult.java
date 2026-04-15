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

    // 中文名称
    public String idCh = "记录ID";
    public String timestampCh = "时间戳";
    public String myGenderCh = "本人性别";
    public String myBirthYearCh = "本人出生年";
    public String myBirthMonthCh = "本人出生月";
    public String myBirthDayCh = "本人出生日";
    public String myBirthHourCh = "本人出生时";
    public String myLocationCh = "本人所在地";
    public String hasPartnerCh = "是否有伴侣";
    public String partnerBirthYearCh = "伴侣出生年";
    public String partnerBirthMonthCh = "伴侣出生月";
    public String partnerBirthDayCh = "伴侣出生日";
    public String partnerBirthHourCh = "伴侣出生时";
    public String partnerGenderCh = "伴侣性别";
    public String preferencesCh = "偏好";
    public String scoreCh = "八字分数";
    public String resultTypeCh = "结果类型";
    public String analysisCh = "分析";
    public String idealAgeDiffCh = "理想年龄差";
    public String idealDirectionCh = "理想方向";
    public String personalityTraitCh = "性格特质";

    public BaZiResult() {
        this.timestamp = System.currentTimeMillis();
        this.id = "bazi_" + timestamp;
    }
}
