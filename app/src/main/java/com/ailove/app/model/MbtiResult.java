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

    // 中文名称
    public String idCh = "记录ID";
    public String timestampCh = "时间戳";
    public String mbtiTypeCh = "MBTI类型";
    public String titleCh = "类型标题";
    public String subtitleCh = "类型副标题";
    public String descriptionCh = "类型描述";
    public String matchesCh = "匹配类型";
    public String eScoreCh = "外向得分";
    public String iScoreCh = "内向得分";
    public String sScoreCh = "感觉得分";
    public String nScoreCh = "直觉得分";
    public String tScoreCh = "思考得分";
    public String fScoreCh = "情感得分";
    public String jScoreCh = "判断得分";
    public String pScoreCh = "知觉得分";

    public MbtiResult() {
        this.timestamp = System.currentTimeMillis();
        this.id = "mbti_" + timestamp;
    }
}
