package com.ailove.app.api;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.ailove.app.model.*;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ApiClient {
    private static final String TAG = "ApiClient";
    private static final String BASE_URL = "https://agentsh.cn:7777";
    private static ApiClient instance;
    private final Handler mainHandler;
    private final Gson gson;
    private final Random random;

    private String currentUserId = "mock_user_001";
    private int profileProgress = 35;
    // Internal flag to indicate whether the current user has passed identity/verification
    private boolean isProfileVerified = false;

    private ApiClient() {
        mainHandler = new Handler(Looper.getMainLooper());
        gson = new Gson();
        random = new Random();
    }

    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    public interface Callback<T> {
        void onSuccess(T result);
        void onError(String error);
    }

    @SuppressWarnings("unchecked")
    private <T> void postSuccess(Callback<T> callback, T result) {
        if (callback != null) {
            mainHandler.post(() -> callback.onSuccess(result));
        }
    }

    private void postError(Callback<?> callback, String error) {
        if (callback != null) {
            mainHandler.post(() -> callback.onError(error));
        }
    }

    public void register(String phone, String wechatCode, Callback<AuthResult> callback) {
        Log.d(TAG, "register called with phone: " + phone);
        mainHandler.postDelayed(() -> {
            AuthResult result = new AuthResult();
            result.token = "mock_token_" + System.currentTimeMillis();
            result.userId = currentUserId;
            result.success = true;
            postSuccess(callback, result);
        }, 500);
    }

    public void realNameVerify(String idCard, String name, String faceImageBase64, Callback<VerifyResult> callback) {
        Log.d(TAG, "realNameVerify called");
        mainHandler.postDelayed(() -> {
            VerifyResult result = new VerifyResult();
            result.success = true;
            result.verified = true;
            result.verifyLevel = "L3";
            result.message = "认证成功";
            // mark profile as verified for UI visibility
            isProfileVerified = true;
            postSuccess(callback, result);
        }, 1000);
    }

    // New:身份证/身份认证的统一入口（UI层调用时作为演示用）
    public void verifyIdentity(String name, String idCard, Callback<VerifyResult> callback) {
        Log.d(TAG, "verifyIdentity called with name=" + name + ", idCard=" + idCard);
        mainHandler.postDelayed(() -> {
            VerifyResult result = new VerifyResult();
            result.success = true;
            result.verified = true;
            result.verifyLevel = "L3";
            result.message = "身份证实名认证通过";
            isProfileVerified = true;
            postSuccess(callback, result);
        }, 900);
    }

    // New: 颜值/照片与视频认证（演示用，实际设备调用请接入 CameraX / Camera API）
    public void verifyPhotoVideo(String imageBase64, Callback<FaceAnalyzeResult> callback) {
        Log.d(TAG, "verifyPhotoVideo called");
        mainHandler.postDelayed(() -> {
            FaceAnalyzeResult result = new FaceAnalyzeResult();
            result.score = 90; // 演示分数
            result.appearance = "自拍/视频认证通过";
            postSuccess(callback, result);
        }, 1100);
    }

    // New: 车辆认证演示
    public void verifyVehicle(Callback<VerifyResult> callback) {
        Log.d(TAG, "verifyVehicle called");
        mainHandler.postDelayed(() -> {
            VerifyResult result = new VerifyResult();
            result.success = true;
            result.verified = true;
            result.message = "行驶证认证通过";
            postSuccess(callback, result);
        }, 800);
    }

    // New: 学历认证演示
    public void verifyEducation(Callback<VerifyResult> callback) {
        Log.d(TAG, "verifyEducation called");
        mainHandler.postDelayed(() -> {
            VerifyResult result = new VerifyResult();
            result.success = true;
            result.verified = true;
            result.message = "学历认证通过";
            postSuccess(callback, result);
        }, 800);
    }

    // New: 健康证明演示
    public void verifyHealth(Callback<VerifyResult> callback) {
        Log.d(TAG, "verifyHealth called");
        mainHandler.postDelayed(() -> {
            VerifyResult result = new VerifyResult();
            result.success = true;
            result.verified = true;
            result.message = "健康证明认证通过";
            postSuccess(callback, result);
        }, 900);
    }

    // New: 资产认证演示
    public void verifyAssets(Callback<VerifyResult> callback) {
        Log.d(TAG, "verifyAssets called");
        mainHandler.postDelayed(() -> {
            VerifyResult result = new VerifyResult();
            result.success = true;
            result.verified = true;
            result.message = "资产认证通过";
            postSuccess(callback, result);
        }, 900);
    }

    // New: 证书/获奖证书认证演示
    public void verifyCertificates(Callback<VerifyResult> callback) {
        Log.d(TAG, "verifyCertificates called");
        mainHandler.postDelayed(() -> {
            VerifyResult result = new VerifyResult();
            result.success = true;
            result.verified = true;
            result.message = "证书认证通过";
            postSuccess(callback, result);
        }, 900);
    }

    public void getUserProfile(Callback<UserProfile> callback) {
        Log.d(TAG, "getUserProfile called");
        mainHandler.postDelayed(() -> {
            UserProfile profile = new UserProfile();
            profile.userId = currentUserId;
            profile.nickname = "用户" + (random.nextInt(9000) + 1000);
            profile.avatar = "https://picsum.photos/200";
            profile.age = random.nextInt(10) + 22;
            profile.gender = "男";
            profile.height = random.nextInt(20) + 165;
            profile.weight = random.nextInt(15) + 60;
            profile.education = "本科";
            profile.income = random.nextInt(20) + 10;
            profile.city = "北京";
            profile.profileProgress = profileProgress;
            // 将实名认证状态绑定到实际的验证标志
            profile.verified = isProfileVerified;
            postSuccess(callback, profile);
        }, 300);
    }

    public void sendAIChat(String userId, String userInput, String conversationId, Callback<AIChatResult> callback) {
        Log.d(TAG, "sendAIChat called: " + userInput);
        mainHandler.postDelayed(() -> {
            AIChatResult result = new AIChatResult();
            result.reply = generateAIReply(userInput);
            result.progress = Math.min(profileProgress + random.nextInt(5), 100);
            profileProgress = result.progress;
            result.conversationId = conversationId != null ? conversationId : "conv_" + System.currentTimeMillis();
            postSuccess(callback, result);
        }, 800);
    }

    private String generateAIReply(String userInput) {
        if (userInput.contains("身高")) {
            return "身高是择偶中很重要的因素呢～你希望找到多高的另一半呢？";
        } else if (userInput.contains("收入") || userInput.contains("钱")) {
            return "收入只是生活的一部分，我看你兴趣爱好很广泛，这也是一种财富呀～";
        } else if (userInput.contains("学历")) {
            return "学历确实能反映一个人的学习能力，你更看重学历还是个人能力呢？";
        } else if (userInput.contains("照片") || userInput.contains("颜值")) {
            return "可以发一张原相机无滤镜的照片给我吗？我帮你分析一下～";
        } else {
            String[] replies = {
                "我了解了～还有更多想分享的吗？",
                "很高兴你能告诉我这些，继续说说你的理想型吧～",
                "这些信息对我了解你很有帮助呢！",
                "你真的很坦诚呀～让我们继续聊聊吧～"
            };
            return replies[random.nextInt(replies.length)];
        }
    }

    public void getUserModel(String userId, Callback<UserModelResult> callback) {
        Log.d(TAG, "getUserModel called");
        mainHandler.postDelayed(() -> {
            UserModelResult result = new UserModelResult();
            result.userId = userId;
            result.personality = random.nextInt(30) + 70;
            result.values = random.nextInt(30) + 70;
            result.economic = random.nextInt(30) + 70;
            result.appearance = random.nextInt(30) + 70;
            result.horoscope = random.nextInt(30) + 70;
            result.nationalRanking = 92.35f;
            result.rankText = "您的身高+学历+收入超过了全国92.35%的男性，处于AiLove男性用户Top 50名。";
            result.recommendedType = "温柔贤惠型";
            postSuccess(callback, result);
        }, 500);
    }

    public void analyzeFace(String imageBase64, Callback<FaceAnalyzeResult> callback) {
        Log.d(TAG, "analyzeFace called");
        mainHandler.postDelayed(() -> {
            FaceAnalyzeResult result = new FaceAnalyzeResult();
            result.score = random.nextInt(20) + 75;
            result.appearance = "面相端正，笑容温暖";
            result.suggestions = new String[]{"建议露出额头会更精神", "笑容很有亲和力"};
            postSuccess(callback, result);
        }, 1200);
    }

    public void getMatchRecommendation(String userId, Callback<MatchResult> callback) {
        Log.d(TAG, "getMatchRecommendation called");
        mainHandler.postDelayed(() -> {
            MatchResult result = new MatchResult();
            result.recommendUser = createMockRecommendUser();
            result.fitPoints = new String[]{"你们都喜欢户外运动", "八字五行互补", "都热爱电影"};
            result.attentionPoints = new String[]{"对方比较敏感，建议沟通时更温柔一些", "对方比较慢热，需要耐心"};
            postSuccess(callback, result);
        }, 800);
    }

    private RecommendUser createMockRecommendUser() {
        RecommendUser user = new RecommendUser();
        user.userId = "user_" + (random.nextInt(9000) + 1000);
        user.nickname = "小" + (random.nextInt(9000) + 1000);
        user.avatar = "https://picsum.photos/200?random=" + random.nextInt(100);
        user.age = random.nextInt(8) + 22;
        user.gender = "女";
        user.height = random.nextInt(15) + 160;
        user.education = "本科";
        user.income = random.nextInt(15) + 8;
        user.city = "北京";
        user.verified = true;
        user.introduction = "热爱生活，喜欢旅行和美食，寻求一份真挚的感情。";
        return user;
    }

    public void likeUser(String userId, String targetUserId, Callback<LikeResult> callback) {
        Log.d(TAG, "likeUser called: " + targetUserId);
        mainHandler.postDelayed(() -> {
            LikeResult result = new LikeResult();
            result.success = true;
            result.matched = random.nextBoolean();
            result.message = result.matched ? "匹配成功！" : "等待对方回应";
            postSuccess(callback, result);
        }, 500);
    }

    public void getMatchStatus(String userId, String targetUserId, Callback<MatchStatusResult> callback) {
        Log.d(TAG, "getMatchStatus called");
        mainHandler.postDelayed(() -> {
            MatchStatusResult result = new MatchStatusResult();
            result.matched = random.nextBoolean();
            result.status = result.matched ? "matched" : "waiting";
            postSuccess(callback, result);
        }, 300);
    }

    public void createSession(String userAId, String userBId, Callback<SessionResult> callback) {
        Log.d(TAG, "createSession called");
        mainHandler.postDelayed(() -> {
            SessionResult result = new SessionResult();
            result.sessionId = "session_" + System.currentTimeMillis();
            result.success = true;
            result.welcomeMessage = "欢迎进入2人+1AI的群聊，小爱为你们牵线搭桥！";
            postSuccess(callback, result);
        }, 500);
    }

    public void sendMessage(String sessionId, String senderId, String content, Callback<MessageResult> callback) {
        Log.d(TAG, "sendMessage called: " + content);
        mainHandler.postDelayed(() -> {
            MessageResult result = new MessageResult();
            result.messageId = "msg_" + System.currentTimeMillis();
            result.success = true;
            result.aiIntervened = random.nextInt(10) < 3;
            if (result.aiIntervened) {
                result.aiMessage = generateAIInterveneMessage();
            }
            postSuccess(callback, result);
        }, 300);
    }

    private String generateAIInterveneMessage() {
        String[] messages = {
            "我看你们都喜欢看电影，最近上的《热辣滚烫》看了没？",
            "你们都很喜欢户外运动呀～有没有一起去徒步的打算？",
            "听说美食能拉近彼此的距离，你们喜欢吃什么呢？"
        };
        return messages[random.nextInt(messages.length)];
    }

    public void getChatList(String userId, Callback<List<ChatSession>> callback) {
        Log.d(TAG, "getChatList called");
        mainHandler.postDelayed(() -> {
            List<ChatSession> sessions = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                ChatSession session = new ChatSession();
                session.sessionId = "session_" + i;
                session.targetUser = createMockRecommendUser();
                session.lastMessage = "最近在忙什么呢？";
                session.lastMessageTime = System.currentTimeMillis() - i * 3600000;
                session.unreadCount = random.nextInt(5);
                session.aiOnline = true;
                sessions.add(session);
            }
            postSuccess(callback, sessions);
        }, 300);
    }

    public void getMessages(String sessionId, Callback<List<ChatMessage>> callback) {
        Log.d(TAG, "getMessages called");
        mainHandler.postDelayed(() -> {
            List<ChatMessage> messages = new ArrayList<>();
            String[] contents = {"你好呀～", "很高兴认识你", "你在干嘛呢？", "今天天气真好"};
            for (int i = 0; i < 10; i++) {
                ChatMessage msg = new ChatMessage();
                msg.messageId = "msg_" + i;
                msg.content = contents[i % contents.length];
                msg.senderId = i % 2 == 0 ? "target_user" : currentUserId;
                msg.senderName = i % 2 == 0 ? "小可爱" : "我";
                msg.timestamp = System.currentTimeMillis() - i * 60000;
                messages.add(msg);
            }
            postSuccess(callback, messages);
        }, 300);
    }

    public void setCurrentUserId(String userId) {
        this.currentUserId = userId;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setProfileProgress(int progress) {
        this.profileProgress = progress;
    }
}
