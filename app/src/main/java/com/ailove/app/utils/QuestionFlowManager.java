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
    public static final String TEST_DEEP = "deep_profile";

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
        currentProgress.put(TEST_DEEP, 0);
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
        questions.add(new TestQuestion("mbti_1", TEST_MBTI, "E-I", "在社交聚会中，你通常会？", new String[]{"主动与多人交谈，享受热闹的氛围", "与少数人深入交流，或找个安静角落"}, 1));
        questions.add(new TestQuestion("mbti_2", TEST_MBTI, "E-I", "经历了一周忙碌的工作后，周末你更倾向于？", new String[]{"约朋友聚会，从中恢复精力", "独处放松，给自己充电"}, 2));
        questions.add(new TestQuestion("mbti_3", TEST_MBTI, "E-I", "在恋爱关系中，你认为理想的相处模式是？", new String[]{"经常一起参加社交活动", "享受二人世界"}, 3));
        questions.add(new TestQuestion("mbti_4", TEST_MBTI, "E-I", "当你遇到开心的事情时，你会？", new String[]{"迫不及待地与身边的人分享", "在心里细细品味"}, 4));
        questions.add(new TestQuestion("mbti_5", TEST_MBTI, "S-N", "当你认识一个新的人时，你更关注？", new String[]{"对方的外在表现和言行举止", "对方给人的感觉和潜在特质"}, 5));
        questions.add(new TestQuestion("mbti_6", TEST_MBTI, "S-N", "在规划一次约会时，你更喜欢？", new String[]{"制定详细的计划", "随性而行，留出惊喜空间"}, 6));
        questions.add(new TestQuestion("mbti_7", TEST_MBTI, "S-N", "面对感情问题时，你更信任？", new String[]{"过往的经验和实际情况", "直觉和对未来的预感"}, 7));
        questions.add(new TestQuestion("mbti_8", TEST_MBTI, "S-N", "你更容易被什么样的描述打动？", new String[]{"具体、生动、有画面感的故事", "抽象、富有哲理的表达"}, 8));
        questions.add(new TestQuestion("mbti_9", TEST_MBTI, "T-F", "当伴侣向你倾诉烦恼时，你的第一反应是？", new String[]{"分析问题，提供解决方案", "给予情感支持，先安慰"}, 9));
        questions.add(new TestQuestion("mbti_10", TEST_MBTI, "T-F", "在关系中发生分歧时，你认为更重要的是？", new String[]{"理清是非对错", "维护彼此的感受"}, 10));
        questions.add(new TestQuestion("mbti_11", TEST_MBTI, "T-F", "评价一个人时，你更看重？", new String[]{"对方的能力和成就", "对方的善良和真诚"}, 11));
        questions.add(new TestQuestion("mbti_12", TEST_MBTI, "T-F", "如果必须在诚实和善意之间选择？", new String[]{"选择诚实，真相最重要", "选择善意，不想伤害对方"}, 12));
        questions.add(new TestQuestion("mbti_13", TEST_MBTI, "J-P", "对于约会时间，你通常？", new String[]{"提前计划，不喜欢变动", "保持灵活，享受随性"}, 13));
        questions.add(new TestQuestion("mbti_14", TEST_MBTI, "J-P", "在一段恋爱关系中，你更希望？", new String[]{"有明确的未来规划", "顺其自然发展"}, 14));
        questions.add(new TestQuestion("mbti_15", TEST_MBTI, "J-P", "面对一项任务，你倾向于？", new String[]{"提前完成，避免压力", "在压力下更有灵感"}, 15));
        questions.add(new TestQuestion("mbti_16", TEST_MBTI, "J-P", "你的生活空间通常是？", new String[]{"整洁有序", "随性自然"}, 16));
        return questions;
    }

    private List<TestQuestion> getBaZiQuestions() {
        List<TestQuestion> questions = new ArrayList<>();
        questions.add(new TestQuestion("bazi_1", TEST_BAZI, "myGender", "您的性别是？", new String[]{"男", "女"}, 1));
        questions.add(new TestQuestion("bazi_2", TEST_BAZI, "myBirth", "请输入您的出生年份（如1995年）", new String[]{}, 2));
        questions.add(new TestQuestion("bazi_3", TEST_BAZI, "myBirth", "请输入您的出生月份（如3月）", new String[]{}, 3));
        questions.add(new TestQuestion("bazi_4", TEST_BAZI, "myBirth", "请输入您的出生日期（如15号）", new String[]{}, 4));
        questions.add(new TestQuestion("bazi_5", TEST_BAZI, "myBirth", "请输入您的出生时辰（如晚上8点）", new String[]{}, 5));
        questions.add(new TestQuestion("bazi_6", TEST_BAZI, "hasPartner", "您目前有心仪对象或伴侣吗？", new String[]{"有，我想测双人匹配", "没有，看我的命定画像"}, 6));
        questions.add(new TestQuestion("bazi_7", TEST_BAZI, "preferences", "您最看重伴侣的哪个特质？", new String[]{"善解人意", "事业心强", "幽默风趣", "责任心强"}, 7));
        return questions;
    }

    private List<TestQuestion> getConstellationQuestions() {
        List<TestQuestion> questions = new ArrayList<>();
        questions.add(new TestQuestion("const_1", TEST_CONSTELLATION, "selfZodiac", "请选择你的太阳星座", new String[]{}, 1));
        questions.add(new TestQuestion("const_2", TEST_CONSTELLATION, "partnerZodiac", "期望伴侣的星座（可多选）", new String[]{}, 2));
        questions.add(new TestQuestion("const_3", TEST_CONSTELLATION, "values", "在关系中，你最看重什么？", new String[]{}, 3));
        questions.add(new TestQuestion("const_4", TEST_CONSTELLATION, "communication", "你的沟通风格是？", new String[]{}, 4));
        questions.add(new TestQuestion("const_5", TEST_CONSTELLATION, "dating", "理想中的约会方式？", new String[]{}, 5));
        questions.add(new TestQuestion("const_6", TEST_CONSTELLATION, "conflict", "面对冲突时，你通常会？", new String[]{}, 6));
        questions.add(new TestQuestion("const_7", TEST_CONSTELLATION, "future", "你对未来的规划倾向？", new String[]{}, 7));
        return questions;
    }

    private List<TestQuestion> getBigFiveQuestions() {
        List<TestQuestion> questions = new ArrayList<>();
        questions.add(new TestQuestion("bigfive_1", TEST_BIGFIVE, "openness", "我喜欢尝试新的事物和体验", new String[]{"非常同意", "有些同意", "中立", "不太同意", "很不同意"}, 1));
        questions.add(new TestQuestion("bigfive_2", TEST_BIGFIVE, "openness", "我经常思考抽象的概念和理论", new String[]{"非常同意", "有些同意", "中立", "不太同意", "很不同意"}, 2));
        questions.add(new TestQuestion("bigfive_3", TEST_BIGFIVE, "openness", "我对艺术和美学有浓厚的兴趣", new String[]{"非常同意", "有些同意", "中立", "不太同意", "很不同意"}, 3));
        questions.add(new TestQuestion("bigfive_4", TEST_BIGFIVE, "conscientiousness", "我做事总是有计划、有条理", new String[]{"非常同意", "有些同意", "中立", "不太同意", "很不同意"}, 4));
        questions.add(new TestQuestion("bigfive_5", TEST_BIGFIVE, "conscientiousness", "我能坚持完成困难的任务", new String[]{"非常同意", "有些同意", "中立", "不太同意", "很不同意"}, 5));
        questions.add(new TestQuestion("bigfive_6", TEST_BIGFIVE, "conscientiousness", "我注重细节，追求完美", new String[]{"非常同意", "有些同意", "中立", "不太同意", "很不同意"}, 6));
        questions.add(new TestQuestion("bigfive_7", TEST_BIGFIVE, "extraversion", "我喜欢参加社交活动和聚会", new String[]{"非常同意", "有些同意", "中立", "不太同意", "很不同意"}, 7));
        questions.add(new TestQuestion("bigfive_8", TEST_BIGFIVE, "extraversion", "我在人群中感到精力充沛", new String[]{"非常同意", "有些同意", "中立", "不太同意", "很不同意"}, 8));
        questions.add(new TestQuestion("bigfive_9", TEST_BIGFIVE, "extraversion", "我容易与陌生人交谈", new String[]{"非常同意", "有些同意", "中立", "不太同意", "很不同意"}, 9));
        questions.add(new TestQuestion("bigfive_10", TEST_BIGFIVE, "agreeableness", "我很容易理解和体谅他人", new String[]{"非常同意", "有些同意", "中立", "不太同意", "很不同意"}, 10));
        questions.add(new TestQuestion("bigfive_11", TEST_BIGFIVE, "agreeableness", "我愿意帮助有需要的人", new String[]{"非常同意", "有些同意", "中立", "不太同意", "很不同意"}, 11));
        questions.add(new TestQuestion("bigfive_12", TEST_BIGFIVE, "agreeableness", "我相信大多数人是善良的", new String[]{"非常同意", "有些同意", "中立", "不太同意", "很不同意"}, 12));
        questions.add(new TestQuestion("bigfive_13", TEST_BIGFIVE, "neuroticism", "我能够很好地管理自己的情绪", new String[]{"非常同意", "有些同意", "中立", "不太同意", "很不同意"}, 13));
        questions.add(new TestQuestion("bigfive_14", TEST_BIGFIVE, "neuroticism", "面对压力时我能保持冷静", new String[]{"非常同意", "有些同意", "中立", "不太同意", "很不同意"}, 14));
        questions.add(new TestQuestion("bigfive_15", TEST_BIGFIVE, "neuroticism", "我很少感到焦虑或担忧", new String[]{"非常同意", "有些同意", "中立", "不太同意", "很不同意"}, 15));
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

    private List<TestQuestion> getDeepProfileQuestions() {
        List<TestQuestion> questions = new ArrayList<>();
        questions.add(new TestQuestion("deep_1", TEST_DEEP, "personality_deep", "深度性格分析 - 面对压力时你通常会怎么做？", 
            new String[]{"独自消化", "找朋友倾诉", "运动/听音乐", "分析解决问题", "暂时逃避"}, 1));
        questions.add(new TestQuestion("deep_2", TEST_DEEP, "relationship_view", "感情观 - 你认为理想的恋爱关系是？", 
            new String[]{"互相尊重独立", "无话不谈陪伴", "稳定共同目标", "充满激情新鲜", "默契不需言语"}, 2));
        questions.add(new TestQuestion("deep_3", TEST_DEEP, "family_background", "原生家庭 - 你与父母的关系？", 
            new String[]{"亲密经常交流", "一般礼貌距离", "疏远很少联系", "复杂有爱有矛盾", "正在努力修复"}, 3));
        questions.add(new TestQuestion("deep_4", TEST_DEEP, "growth_experience", "成长经历 - 最重要的转折点是？", 
            new String[]{"考学升学", "第一次恋爱", "重大挫折失败", "家庭变故", "独立生活工作"}, 4));
        questions.add(new TestQuestion("deep_5", TEST_DEEP, "psychological_traits", "心理特质 - 你最敏感的心理触发点是？", 
            new String[]{"被忽视冷落", "被批评否定", "不确定性变化", "不公平对待", "失去控制感"}, 5));
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
        progress.put(TEST_DEEP, getProgress(TEST_DEEP));
        return progress;
    }
}