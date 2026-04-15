package com.ailove.app.storage;

import android.content.Context;
import com.ailove.app.model.ChatMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ChatHistoryStorage {
    private static final String FILE_PREFIX = "chat_history_";
    private static final String FILE_SUFFIX = ".json";
    private static final Gson gson = new Gson();

    private static String getFileName(String email) {
        return FILE_PREFIX + email.replace("@", "_at_").replace(".", "_") + FILE_SUFFIX;
    }

    private static void migrateOldFile(Context context, String email) {
        File oldFile = new File(context.getFilesDir(), "chat_history.json");
        File newFile = new File(context.getFilesDir(), getFileName(email));
        if (oldFile.exists() && !newFile.exists()) {
            oldFile.renameTo(newFile);
        }
    }

    public static void saveChatHistory(Context context, List<ChatMessage> messages, String email) {
        if (email == null || email.isEmpty()) return;
        try {
            File file = new File(context.getFilesDir(), getFileName(email));
            FileWriter writer = new FileWriter(file);
            gson.toJson(messages, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<ChatMessage> loadChatHistory(Context context, String email) {
        if (email == null || email.isEmpty()) return new ArrayList<>();
        migrateOldFile(context, email);
        try {
            File file = new File(context.getFilesDir(), getFileName(email));
            if (!file.exists()) {
                return new ArrayList<>();
            }
            FileReader reader = new FileReader(file);
            Type listType = new TypeToken<ArrayList<ChatMessage>>(){}.getType();
            List<ChatMessage> messages = gson.fromJson(reader, listType);
            reader.close();
            return messages != null ? messages : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void clearChatHistory(Context context, String email) {
        if (email == null || email.isEmpty()) return;
        try {
            File file = new File(context.getFilesDir(), getFileName(email));
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
