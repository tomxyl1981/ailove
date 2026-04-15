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

    // 中文名称
    public String idCh = "记录ID";
    public String timestampCh = "时间戳";
    public String userNameCh = "用户名";
    public String genderCh = "性别";
    public String selfZodiacCh = "本人星座";
    public String partnerZodiacsCh = "理想星座";
    public String valuesCh = "价值观";
    public String communicationCh = "沟通方式";
    public String datingCh = "约会方式";
    public String conflictCh = "冲突处理";
    public String careerCh = "事业评分";
    public String familyCh = "家庭评分";
    public String balanceCh = "平衡评分";
    public String spontaneousCh = " spontaneity评分";
    public String matchScoreCh = "匹配分数";
    public String matchTitleCh = "匹配标题";
    public String matchDescCh = "匹配描述";
    public String topMatchesCh = "最佳匹配";
    public String zodiacTraitCh = "星座特质";
    public String communicationAdviceCh = "沟通建议";
    public String relationshipAdviceCh = "恋爱建议";

    public ConstellationResult() {
        this.timestamp = System.currentTimeMillis();
        this.id = "constellation_" + timestamp;
    }
}
