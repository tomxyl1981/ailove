package com.ailove.app.storage;

import android.content.Context;
import com.ailove.app.model.BaZiResult;
import com.ailove.app.model.MbtiResult;
import com.ailove.app.model.ConstellationResult;
import com.ailove.app.model.BigFiveResult;
import com.ailove.app.model.DeepProfileResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TestResultStorage {
    private static final String BAZI_FILE_PREFIX = "bazi_";
    private static final String MBTI_FILE_PREFIX = "mbti_";
    private static final String CONSTELLATION_FILE_PREFIX = "constellation_";
    private static final String BIGFIVE_FILE_PREFIX = "bigfive_";
    private static final String DEEP_PROFILE_FILE_PREFIX = "deep_profile_";
    private static final Gson gson = new Gson();

    private static String getEmailKey(String email) {
        if (email == null || email.isEmpty()) return "default";
        return email.replace("@", "_at_").replace(".", "_");
    }

    private static String getFileName(String prefix, String email) {
        return prefix + "results_" + getEmailKey(email) + ".json";
    }

    private static File getFile(Context context, String prefix, String email) {
        return new File(context.getFilesDir(), getFileName(prefix, email));
    }

    public static void setCurrentUserEmail(Context context, String email) {
        context.getSharedPreferences("ailove_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("user_email", email)
            .putString("current_user_email", email)
            .apply();
    }

    public static String getCurrentUserEmail(Context context) {
        android.content.SharedPreferences prefs = context.getSharedPreferences("ailove_prefs", Context.MODE_PRIVATE);
        String email = prefs.getString("user_email", "");
        if (email == null || email.isEmpty()) {
            email = prefs.getString("current_user_email", "");
        }
        return email;
    }

    public static void clearCurrentUserData(Context context) {
        String email = getCurrentUserEmail(context);
        if (email == null || email.isEmpty()) return;
        
        getFile(context, BAZI_FILE_PREFIX, email).delete();
        getFile(context, MBTI_FILE_PREFIX, email).delete();
        getFile(context, CONSTELLATION_FILE_PREFIX, email).delete();
        getFile(context, BIGFIVE_FILE_PREFIX, email).delete();
    }

    // BaZi Results
    public static void saveBaZiResult(Context context, BaZiResult result) {
        String email = getCurrentUserEmail(context);
        List<BaZiResult> results = loadBaZiResults(context, email);
        results.add(result);
        saveToFile(context, BAZI_FILE_PREFIX, email, results);
    }

    public static List<BaZiResult> loadBaZiResults(Context context, String email) {
        return loadFromFile(context, BAZI_FILE_PREFIX, email, new TypeToken<List<BaZiResult>>(){}.getType());
    }

    public static BaZiResult getLatestBaZiResult(Context context, String email) {
        List<BaZiResult> results = loadBaZiResults(context, email);
        if (results.isEmpty()) return null;
        return results.get(results.size() - 1);
    }

    // MBTI Results
    public static void saveMbtiResult(Context context, MbtiResult result) {
        String email = getCurrentUserEmail(context);
        List<MbtiResult> results = loadMbtiResults(context, email);
        results.add(result);
        saveToFile(context, MBTI_FILE_PREFIX, email, results);
    }

    public static List<MbtiResult> loadMbtiResults(Context context, String email) {
        return loadFromFile(context, MBTI_FILE_PREFIX, email, new TypeToken<List<MbtiResult>>(){}.getType());
    }

    public static MbtiResult getLatestMbtiResult(Context context, String email) {
        List<MbtiResult> results = loadMbtiResults(context, email);
        if (results.isEmpty()) return null;
        return results.get(results.size() - 1);
    }

    // Constellation Results
    public static void saveConstellationResult(Context context, ConstellationResult result) {
        String email = getCurrentUserEmail(context);
        List<ConstellationResult> results = loadConstellationResults(context, email);
        results.add(result);
        saveToFile(context, CONSTELLATION_FILE_PREFIX, email, results);
    }

    public static List<ConstellationResult> loadConstellationResults(Context context, String email) {
        return loadFromFile(context, CONSTELLATION_FILE_PREFIX, email, new TypeToken<List<ConstellationResult>>(){}.getType());
    }

    public static ConstellationResult getLatestConstellationResult(Context context, String email) {
        List<ConstellationResult> results = loadConstellationResults(context, email);
        if (results.isEmpty()) return null;
        return results.get(results.size() - 1);
    }

    // BigFive Results
    public static void saveBigFiveResult(Context context, BigFiveResult result) {
        String email = getCurrentUserEmail(context);
        List<BigFiveResult> results = loadBigFiveResults(context, email);
        results.add(result);
        saveToFile(context, BIGFIVE_FILE_PREFIX, email, results);
    }

    public static List<BigFiveResult> loadBigFiveResults(Context context, String email) {
        return loadFromFile(context, BIGFIVE_FILE_PREFIX, email, new TypeToken<List<BigFiveResult>>(){}.getType());
    }

    public static BigFiveResult getLatestBigFiveResult(Context context, String email) {
        List<BigFiveResult> results = loadBigFiveResults(context, email);
        if (results.isEmpty()) return null;
        return results.get(results.size() - 1);
    }

    // Deep Profile Results
    public static void saveDeepProfileResult(Context context, DeepProfileResult result) {
        String email = getCurrentUserEmail(context);
        android.util.Log.d("Debug", "Saving deep_profile, email: " + email);
        List<DeepProfileResult> results = loadDeepProfileResults(context, email);
        results.add(result);
        saveToFile(context, DEEP_PROFILE_FILE_PREFIX, email, results);
        android.util.Log.d("Debug", "Saved deep_profile, count: " + results.size());
    }

    public static List<DeepProfileResult> loadDeepProfileResults(Context context, String email) {
        return loadFromFile(context, DEEP_PROFILE_FILE_PREFIX, email, new TypeToken<List<DeepProfileResult>>(){}.getType());
    }

    public static DeepProfileResult getLatestDeepProfileResult(Context context, String email) {
        List<DeepProfileResult> results = loadDeepProfileResults(context, email);
        if (results.isEmpty()) return null;
        return results.get(results.size() - 1);
    }
    
    public static DeepProfileResult getLatestDeepProfileResult(Context context) {
        return getLatestDeepProfileResult(context, getCurrentUserEmail(context));
    }

    // Helper methods
    private static <T> void saveToFile(Context context, String prefix, String email, List<T> data) {
        File file = getFile(context, prefix, email);
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static <T> List<T> loadFromFile(Context context, String prefix, String email, Type type) {
        File file = getFile(context, prefix, email);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (FileReader reader = new FileReader(file)) {
            List<T> data = gson.fromJson(reader, type);
            return data != null ? data : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Legacy methods (for backwards compatibility)
    public static void saveBaZiResult(Context context, BaZiResult result, String email) {
        List<BaZiResult> results = loadBaZiResults(context, email);
        results.add(result);
        saveToFile(context, BAZI_FILE_PREFIX, email, results);
    }

    public static void saveMbtiResult(Context context, MbtiResult result, String email) {
        List<MbtiResult> results = loadMbtiResults(context, email);
        results.add(result);
        saveToFile(context, MBTI_FILE_PREFIX, email, results);
    }

    public static void saveConstellationResult(Context context, ConstellationResult result, String email) {
        List<ConstellationResult> results = loadConstellationResults(context, email);
        results.add(result);
        saveToFile(context, CONSTELLATION_FILE_PREFIX, email, results);
    }

    public static void saveBigFiveResult(Context context, BigFiveResult result, String email) {
        List<BigFiveResult> results = loadBigFiveResults(context, email);
        results.add(result);
        saveToFile(context, BIGFIVE_FILE_PREFIX, email, results);
    }

    public static BaZiResult getLatestBaZiResult(Context context) {
        return getLatestBaZiResult(context, getCurrentUserEmail(context));
    }

    public static MbtiResult getLatestMbtiResult(Context context) {
        return getLatestMbtiResult(context, getCurrentUserEmail(context));
    }

    public static ConstellationResult getLatestConstellationResult(Context context) {
        return getLatestConstellationResult(context, getCurrentUserEmail(context));
    }

    public static BigFiveResult getLatestBigFiveResult(Context context) {
        return getLatestBigFiveResult(context, getCurrentUserEmail(context));
    }

    public static List<BaZiResult> loadBaZiResults(Context context) {
        return loadBaZiResults(context, getCurrentUserEmail(context));
    }

    public static List<MbtiResult> loadMbtiResults(Context context) {
        return loadMbtiResults(context, getCurrentUserEmail(context));
    }

    public static List<ConstellationResult> loadConstellationResults(Context context) {
        return loadConstellationResults(context, getCurrentUserEmail(context));
    }

    public static List<BigFiveResult> loadBigFiveResults(Context context) {
        return loadBigFiveResults(context, getCurrentUserEmail(context));
    }

    public static List<DeepProfileResult> loadDeepProfileResults(Context context) {
        return loadDeepProfileResults(context, getCurrentUserEmail(context));
    }
    
    public static void saveSyncedTests(Context context, String testsJson, String email) {
        try {
            org.json.JSONObject tests = new org.json.JSONObject(testsJson);
            
            if (tests.has("mbti")) {
                org.json.JSONObject mbti = tests.getJSONObject("mbti");
                MbtiResult result = new MbtiResult();
                result.mbtiType = mbti.optString("mbtiType", "");
                result.title = mbti.optString("title", "");
                result.description = mbti.optString("description", "");
                result.timestamp = mbti.optLong("timestamp", System.currentTimeMillis());
                List<MbtiResult> list = new ArrayList<>();
                list.add(result);
                saveToFile(context, MBTI_FILE_PREFIX, email, list);
            }
            
            if (tests.has("bigfive")) {
                org.json.JSONObject bigfive = tests.getJSONObject("bigfive");
                BigFiveResult result = new BigFiveResult();
                result.openness = bigfive.optInt("openness", 0);
                result.conscientiousness = bigfive.optInt("conscientiousness", 0);
                result.extraversion = bigfive.optInt("extraversion", 0);
                result.agreeableness = bigfive.optInt("agreeableness", 0);
                result.neuroticism = bigfive.optInt("neuroticism", 0);
                result.summary = bigfive.optString("summary", "");
                result.matchSuggestion = bigfive.optString("matchSuggestion", "");
                result.timestamp = bigfive.optLong("timestamp", System.currentTimeMillis());
                List<BigFiveResult> list = new ArrayList<>();
                list.add(result);
                saveToFile(context, BIGFIVE_FILE_PREFIX, email, list);
            }
            
            if (tests.has("constellation")) {
                org.json.JSONObject constellation = tests.getJSONObject("constellation");
                ConstellationResult result = new ConstellationResult();
                result.selfZodiac = constellation.optString("selfZodiac", "");
                result.gender = constellation.optString("gender", "");
                result.communication = constellation.optString("communication", "");
                result.conflict = constellation.optString("conflict", "");
                result.matchScore = constellation.optInt("matchScore", 0);
                result.matchTitle = constellation.optString("matchTitle", "");
                result.timestamp = constellation.optLong("timestamp", System.currentTimeMillis());
                List<ConstellationResult> list = new ArrayList<>();
                list.add(result);
                saveToFile(context, CONSTELLATION_FILE_PREFIX, email, list);
            }
            
            if (tests.has("bazi")) {
                org.json.JSONObject bazi = tests.getJSONObject("bazi");
                BaZiResult result = new BaZiResult();
                result.score = bazi.optInt("score", 0);
                result.timestamp = bazi.optLong("timestamp", System.currentTimeMillis());
                List<BaZiResult> list = new ArrayList<>();
                list.add(result);
                saveToFile(context, BAZI_FILE_PREFIX, email, list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}