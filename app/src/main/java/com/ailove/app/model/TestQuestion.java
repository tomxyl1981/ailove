package com.ailove.app.model;

import java.util.Map;

public class TestQuestion {
    public String id;
    public String testType; // mbti, bazi, constellation, bigfive, sanguan
    public String category;
    public String question;
    public String[] options;
    public Map<String, String> optionMappings; // option -> result value
    public int order;

    public TestQuestion() {}

    public TestQuestion(String id, String testType, String category, String question, String[] options, int order) {
        this.id = id;
        this.testType = testType;
        this.category = category;
        this.question = question;
        this.options = options;
        this.order = order;
    }

    public String toConversationText() {
        if (options == null || options.length == 0) {
            return question;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(question).append("\n\n");
        for (int i = 0; i < options.length; i++) {
            sb.append(i + 1).append(". ").append(options[i]).append("\n");
        }
        return sb.toString();
    }
}

class UserTestProgress {
    public int mbtiProgress = 0;
    public int baziProgress = 0;
    public int constellationProgress = 0;
    public int bigFiveProgress = 0;
    public int sanguanProgress = 0;
    public long lastUpdateTime;
}

class UserTestResult {
    public String testType;
    public Map<String, Object> answers;
    public String result;
    public long timestamp;
}