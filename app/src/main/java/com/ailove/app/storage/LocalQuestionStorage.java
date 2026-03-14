package com.ailove.app.storage;

import android.content.Context;
import com.ailove.app.model.QuestionnaireResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class LocalQuestionStorage {
    private static final String DIR = "questionnaires";
    private static final Gson gson = new Gson();

    private static File getDir(Context ctx) {
        File dir = new File(ctx.getFilesDir(), DIR);
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    public static void saveQuestionnaire(Context context, QuestionnaireResponse q) {
        File dir = getDir(context);
        File f = new File(dir, (q.id != null ? q.id : "qn" + System.currentTimeMillis()) + ".json");
        try (Writer writer = new FileWriter(f)) {
            gson.toJson(q, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<QuestionnaireResponse> loadAll(Context context) {
        File dir = getDir(context);
        List<QuestionnaireResponse> list = new ArrayList<>();
        File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
        if (files == null) return list;
        for (File f : files) {
            try (Reader reader = new FileReader(f)) {
                Type type = new TypeToken<QuestionnaireResponse>() {}.getType();
                QuestionnaireResponse q = gson.fromJson(reader, type);
                if (q != null) list.add(q);
            } catch (IOException ignore) {}
        }
        return list;
    }
}
