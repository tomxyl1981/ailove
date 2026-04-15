package com.ailove.app.storage;

import android.content.Context;
import com.ailove.app.model.BaZiResult;
import com.ailove.app.model.MbtiResult;
import com.ailove.app.model.ConstellationResult;
import com.ailove.app.model.BigFiveResult;
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
    private static final String BAZI_FILE = "bazi_results.json";
    private static final String MBTI_FILE = "mbti_results.json";
    private static final String CONSTELLATION_FILE = "constellation_results.json";
    private static final String BIGFIVE_FILE = "bigfive_results.json";
    private static final Gson gson = new Gson();

    private static File getFile(Context context, String filename) {
        return new File(context.getFilesDir(), filename);
    }

    // BaZi Results
    public static void saveBaZiResult(Context context, BaZiResult result) {
        List<BaZiResult> results = loadBaZiResults(context);
        results.add(result);
        saveToFile(context, BAZI_FILE, results);
    }

    public static List<BaZiResult> loadBaZiResults(Context context) {
        return loadFromFile(context, BAZI_FILE, new TypeToken<List<BaZiResult>>(){}.getType());
    }

    public static BaZiResult getLatestBaZiResult(Context context) {
        List<BaZiResult> results = loadBaZiResults(context);
        if (results.isEmpty()) return null;
        return results.get(results.size() - 1);
    }

    // MBTI Results
    public static void saveMbtiResult(Context context, MbtiResult result) {
        List<MbtiResult> results = loadMbtiResults(context);
        results.add(result);
        saveToFile(context, MBTI_FILE, results);
    }

    public static List<MbtiResult> loadMbtiResults(Context context) {
        return loadFromFile(context, MBTI_FILE, new TypeToken<List<MbtiResult>>(){}.getType());
    }

    public static MbtiResult getLatestMbtiResult(Context context) {
        List<MbtiResult> results = loadMbtiResults(context);
        if (results.isEmpty()) return null;
        return results.get(results.size() - 1);
    }

    // Constellation Results
    public static void saveConstellationResult(Context context, ConstellationResult result) {
        List<ConstellationResult> results = loadConstellationResults(context);
        results.add(result);
        saveToFile(context, CONSTELLATION_FILE, results);
    }

    public static List<ConstellationResult> loadConstellationResults(Context context) {
        return loadFromFile(context, CONSTELLATION_FILE, new TypeToken<List<ConstellationResult>>(){}.getType());
    }

    public static ConstellationResult getLatestConstellationResult(Context context) {
        List<ConstellationResult> results = loadConstellationResults(context);
        if (results.isEmpty()) return null;
        return results.get(results.size() - 1);
    }

    // BigFive Results
    public static void saveBigFiveResult(Context context, BigFiveResult result) {
        List<BigFiveResult> results = loadBigFiveResults(context);
        results.add(result);
        saveToFile(context, BIGFIVE_FILE, results);
    }

    public static List<BigFiveResult> loadBigFiveResults(Context context) {
        return loadFromFile(context, BIGFIVE_FILE, new TypeToken<List<BigFiveResult>>(){}.getType());
    }

    public static BigFiveResult getLatestBigFiveResult(Context context) {
        List<BigFiveResult> results = loadBigFiveResults(context);
        if (results.isEmpty()) return null;
        return results.get(results.size() - 1);
    }

    // Helper methods
    private static <T> void saveToFile(Context context, String filename, List<T> data) {
        File file = getFile(context, filename);
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static <T> List<T> loadFromFile(Context context, String filename, Type type) {
        File file = getFile(context, filename);
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

    public static void clearAllResults(Context context) {
        getFile(context, BAZI_FILE).delete();
        getFile(context, MBTI_FILE).delete();
        getFile(context, CONSTELLATION_FILE).delete();
        getFile(context, BIGFIVE_FILE).delete();
    }
}
