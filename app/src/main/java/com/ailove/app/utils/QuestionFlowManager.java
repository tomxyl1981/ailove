package com.ailove.app.utils;

import android.content.Context;
import android.util.Log;
import com.ailove.app.model.TestQuestion;
import com.ailove.app.model.BaZiResult;
import com.ailove.app.model.MbtiResult;
import com.ailove.app.model.ConstellationResult;
import com.ailove.app.model.BigFiveResult;
import com.ailove.app.storage.TestResultStorage;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionFlowManager {
    private static final String TAG = "QuestionFlowManager";
    private static QuestionFlowManager instance;
    private Context context;
    private Gson gson = new Gson();
    private Map<String, Integer> currentProgress = new HashMap<>();
    private Map<String, Map<String, Object>> pendingAnswers = new HashMap<>();

    // Test types
    public static final String TEST_MBTI = "mbti";
    public static final String TEST_BAZI = "bazi";
    public static final String TEST_CONSTELLATION = "constellation";
    public static final String TEST_BIGFIVE = "bigfive";
    public static final String TEST_SANGUAN = "sanguan";

    private QuestionFlowManager(Context context) {
        this.context = context;
    }

    public static QuestionFlowManager getInstance(Context context) {
        if (instance == null) {
            instance = new QuestionFlowManager(context);
        }
        return instance;
    }

    public void init() {
        currentProgress.put(TEST_MBTI, 0);
        currentProgress.put(TEST_BAZI, 0);
        currentProgress.put(TEST_CONSTELLATION, 0);
        currentProgress.put(TEST_BIGFIVE, 0);
        currentProgress.put(TEST_SANGUAN, 0);
    }

    public void resetProgress(String testType) {
        if (currentProgress.containsKey(testType)) {
            currentProgress.put(testType, 0);
        }
    }

    public void resetAllProgress() {
        for (String key : currentProgress.keySet()) {
            currentProgress.put(key, 0);
        }
        pendingAnswers.clear();
    }

    public TestQuestion getNextQuestion(String testType) {
        List<TestQuestion> questions = getQuestions(testType);
        int progress = currentProgress.getOrDefault(testType, 0);
        
        if (progress >= questions.size()) {
            return null;
        }
        return questions.get(progress);
    }

    public boolean isTestCompleted(String testType) {
        List<TestQuestion> questions = getQuestions(testType);
        int progress = currentProgress.getOrDefault(testType, 0);
        return progress >= questions.size();
    }

    public boolean hasUnansweredQuestions(String testType) {
        List<TestQuestion> questions = getQuestions(testType);
        int progress = currentProgress.getOrDefault(testType, 0);
        return progress < questions.size();
    }

    public void saveAnswer(String testType, String questionId, String answer, String resultValue) {
        if (!pendingAnswers.containsKey(testType)) {
            pendingAnswers.put(testType, new HashMap<>());
        }
        pendingAnswers.get(testType).put(questionId, answer);
        
        // Move to next question
        currentProgress.put(testType, currentProgress.getOrDefault(testType, 0) + 1);
        
        Log.d(TAG, "Saved answer for " + testType + ", progress: " + currentProgress.get(testType));
        
        // If completed, save result
        if (isTestCompleted(testType)) {
            saveTestResult(testType, resultValue);
        }
    }

    private void saveTestResult(String testType, String resultValue) {
        try {
            switch (testType) {
                case TEST_MBTI:
                    MbtiResult mbti = new MbtiResult();
                    mbti.mbtiType = resultValue;
                    mbti.timestamp = System.currentTimeMillis();
                    TestResultStorage.saveMbtiResult(context, mbti);
                    Log.d(TAG, "Saved MBTI result: " + resultValue);
                    break;
                    
                case TEST_BAZI:
                    BaZiResult bazi = new BaZiResult();
                    bazi.timestamp = System.currentTimeMillis();
                    TestResultStorage.saveBaZiResult(context, bazi);
                    Log.d(TAG, "Saved BaZi result");
                    break;
                    
                case TEST_CONSTELLATION:
                    ConstellationResult constellation = new ConstellationResult();
                    constellation.timestamp = System.currentTimeMillis();
                    TestResultStorage.saveConstellationResult(context, constellation);
                    Log.d(TAG, "Saved Constellation result");
                    break;
                    
                case TEST_BIGFIVE:
                    BigFiveResult bigFive = new BigFiveResult();
                    bigFive.timestamp = System.currentTimeMillis();
                    TestResultStorage.saveBigFiveResult(context, bigFive);
                    Log.d(TAG, "Saved BigFive result");
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving result for " + testType, e);
        }
        
        // Clear pending
        pendingAnswers.remove(testType);
    }

    public List<TestQuestion> getQuestions(String testType) {
        List<TestQuestion> questions = new ArrayList<>();
        
        switch (testType) {
            case TEST_MBTI:
                questions = getMbtiQuestions();
                break;
            case TEST_BAZI:
                questions = getBaZiQuestions();
                break;
            case TEST_CONSTELLATION:
                questions = getConstellationQuestions();
                break;
            case TEST_BIGFIVE:
                questions = getBigFiveQuestions();
                break;
            case TEST_SANGUAN:
                questions = getSanguanQuestions();
                break;
        }
        
        return questions;
    }

    private List<TestQuestion> getMbtiQuestions() {
        List<TestQuestion> questions = new ArrayList<>();
        questions.add(new TestQuestion("mbti_1", TEST_MBTI, "E-I", "在社交场合中，你通常更倾向于？", 
            new String[]{"主动结交新朋友", "等待别人主动搭话"}, 1));
        questions.add(new TestQuestion("mbti_2", TEST_MBTI, "S-N", "你更关注事物的哪些方面？", 
            new String[]{"具体的事实和细节", "抽象的可能性和灵感"}, 2));
        questions.add(new TestQuestion("mbti_3", TEST_MBTI, "T-F", "做决定时，你更注重？", 
            new String[]{"逻辑和客观分析", "个人感受和价值观"}, 3));
        questions.add(new TestQuestion("mbti_4", TEST_MBTI, "J-P", "你更喜欢的生活方式是？", 
            new String[]{"有计划和有条理", "灵活和随性"}, 4));
        return questions;
    }

    private List<TestQuestion> getBaZiQuestions() {
        List<TestQuestion> questions = new ArrayList<>();
        questions.add(new TestQuestion("bazi_1", TEST_BAZI, "birth", "请告诉我你的出生年份（如1995年）", 
            new String[]{}, 1));
        questions.add(new TestQuestion("bazi_2", TEST_BAZI, "birth", "请告诉我你的出生月份（如3月）", 
            new String[]{}, 2));
        questions.add(new TestQuestion("bazi_3", TEST_BAZI, "birth", "请告诉我你的出生日期（如15号）", 
            new String[]{}, 3));
        questions.add(new TestQuestion("bazi_4", TEST_BAZI, "birth", "请告诉我你的出生时辰（如晚上8点）", 
            new String[]{}, 4));
        return questions;
    }

    private List<TestQuestion> getConstellationQuestions() {
        List<TestQuestion> questions = new ArrayList<>();
        questions.add(new TestQuestion("const_1", TEST_CONSTELLATION, "star", "请告诉我你的出生月份和日期（如6月15日）", 
            new String[]{}, 1));
        return questions;
    }

    private List<TestQuestion> getBigFiveQuestions() {
        List<TestQuestion> questions = new ArrayList<>();
        questions.add(new TestQuestion("bigfive_1", TEST_BIGFIVE, "openness", "你对待新事物的态度是？", 
            new String[]{"好奇并愿意尝试", "更倾向于熟悉的事物"}, 1));
        questions.add(new TestQuestion("bigfive_2", TEST_BIGFIVE, "conscientiousness", "你做事的风格更接近？", 
            new String[]{"有计划、有条理", "灵活变通、随性而为"}, 2));
        questions.add(new TestQuestion("bigfive_3", TEST_BIGFIVE, "extraversion", "在社交聚会中，你通常？", 
            new String[]{"充满活力，喜欢互动", "安静观察，适时发言"}, 3));
        questions.add(new TestQuestion("bigfive_4", TEST_BIGFIVE, "agreeableness", "当你与他人意见不合时，你会？", 
            new String[]{"寻求共识让步", "坚持自己观点"}, 4));
        questions.add(new TestQuestion("bigfive_5", TEST_BIGFIVE, "neuroticism", "面对压力时，你通常？", 
            new String[]{"冷静处理问题", "容易感到焦虑"}, 5));
        return questions;
    }

    private List<TestQuestion> getSanguanQuestions() {
        List<TestQuestion> questions = new ArrayList<>();
        questions.add(new TestQuestion("sanguan_1", TEST_SANGUAN, "marriage", "你对婚姻的看法是？", 
            new String[]{"人生必须经历的过程", "顺其自然不强求", "可遇不可求"}, 1));
        questions.add(new TestQuestion("sanguan_2", TEST_SANGUAN, "role", "你认为婚后的家庭角色应该是？", 
            new String[]{"平等分担", "传统男主外女主内", "根据双方情况灵活分配"}, 2));
        questions.add(new TestQuestion("sanguan_3", TEST_SANGUAN, "finance", "家庭财务的管理方式？", 
            new String[]{"各自管理，定期汇总", "一方统一管理", "完全AA制"}, 3));
        questions.add(new TestQuestion("sanguan_4", TEST_SANGUAN, "children", "你对生育子女的态度？", 
            new String[]{"必须要有", "顺其自然", "不太想要"}, 4));
        questions.add(new TestQuestion("sanguan_5", TEST_SANGUAN, "parents", "与对方父母同住的看法？", 
            new String[]{"可以接受", "尽量避免", "视情况而定"}, 5));
        return questions;
    }

    public int getProgress(String testType) {
        List<TestQuestion> questions = getQuestions(testType);
        if (questions.isEmpty()) return 100;
        int current = currentProgress.getOrDefault(testType, 0);
        return (current * 100) / questions.size();
    }

    public Map<String, Integer> getAllProgress() {
        Map<String, Integer> progress = new HashMap<>();
        progress.put(TEST_MBTI, getProgress(TEST_MBTI));
        progress.put(TEST_BAZI, getProgress(TEST_BAZI));
        progress.put(TEST_CONSTELLATION, getProgress(TEST_CONSTELLATION));
        progress.put(TEST_BIGFIVE, getProgress(TEST_BIGFIVE));
        progress.put(TEST_SANGUAN, getProgress(TEST_SANGUAN));
        return progress;
    }
}