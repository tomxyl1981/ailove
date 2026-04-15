package com.ailove.app.model;

import java.util.HashMap;
import java.util.Map;

public class MatchSquareCombined {
    // 八字匹配（出生年月日时 + 性别）
    public int baziScore = 0;           // 合婚评分 (0-100)
    public String baziDetails = "";     // 五行、阴阳平衡、生肖夫妻宫分析
    
    // 星座匹配（太阳/月亮/上升星座）
    public int zodiacScore = 0;          // 相位契合度 (0-100)
    public String sunSign = "";         // 太阳星座
    public String moonSign = "";        // 月亮星座  
    public String risingSign = "";      // 上升星座
    public String zodiacDetails = "";   // 合盘分析、性格互补、恋爱预测
    
    // MBTI 匹配（60 题测试，15 分钟完成）
    public int mbtiScore = 0;           // 官配契合度 (0-100)
    public String mbtiType = "";        // 16 型人格
    public String mbtiDetails = "";     // 外向/内向、感觉/直觉等维度分析
    
    // 大五人格 OCEAN（5 维雷达图）
    public int opennessScore = 50;      // 开放性 (0-100)
    public int conscientiousnessScore = 50; // 尽责性 (0-100)
    public int extraversionScore = 50;   // 外向性 (0-100)
    public int agreeablenessScore = 50;  // 宜人性 (0-100)
    public int neuroticismScore = 50;    // 神经质 (0-100)
    public String bigFiveDetails = "";  // OCEAN 详细解读
    
    // 综合评分（权重：八字 20% + 星座 15% + MBTI 25% + 大五 30% + 其他 10%）
    public int overallScore = 0;        // 总匹配度 (0-100)
    
    // 动态更新相关
    private Map<String, Integer> dimensionScores = new HashMap<>();
    
    // 点赞/不喜欢反馈（用于动态调整推荐列表）
    public boolean isLiked = false;     // 是否被点赞
    public boolean isDisliked = false;  // 是否被点不推荐
    
    // 用户输入数据
    private UserInputData userInput;
    
    // 详细解读
    private String overallDetails = ""; // "综合评分：" + overallScore
    private String baZiAnalysis = "";   // 八字合婚分析
    private String zodiacAnalysis = ""; // 星座匹配分析
    private String mbtiAnalysis = "";   // MBTI 性格分析
    private String bigFiveAnalysis = ""; // 大五人格分析
    
    public UserInputData getUserInput() { return userInput; }
    
    public static class UserInputData {
        public String baziBirthDate = "";      // 出生年月日时（八字）
        public String baziGender = "";         // 性别
        public String zodiacBirthDate = "";    // 出生日期（星座）
        public String mbtiEI = "";             // MBTI E/I
        public String mbtiSN = "";             // MBTI S/N
        public String mbtiTF = "";             // MBTI T/F
        public String mbtiJP = "";             // MBTI J/P
    }

    public Map<String, Integer> getDimensionScores() { return dimensionScores; }
}