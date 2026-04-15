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

    // 中文名称
    public String idCh = "记录ID";
    public String timestampCh = "时间戳";
    public String opennessCh = "开放性";
    public String conscientiousnessCh = "尽责性";
    public String extraversionCh = "外向性";
    public String agreeablenessCh = "宜人性";
    public String neuroticismCh = "神经质";
    public String summaryCh = "总结";
    public String matchSuggestionCh = "匹配建议";

    public BigFiveResult() {
        this.timestamp = System.currentTimeMillis();
        this.id = "bigfive_" + timestamp;
    }
}
