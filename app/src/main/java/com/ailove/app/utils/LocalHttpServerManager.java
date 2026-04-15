package com.ailove.app.utils;

import android.content.Context;
import android.util.Log;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Callback;
import okhttp3.Call;

import java.io.File;

public class LocalHttpServerManager {
    private static final String TAG = "ServerManager";
    private static LocalHttpServerManager instance;
    private LocalHttpServer server;
    private OkHttpClient httpClient = new OkHttpClient();
    private String remoteServerUrl = "https://jiehun.mynatapp.cc";
    private Context appContext;

    private LocalHttpServerManager() {}

    public static synchronized LocalHttpServerManager getInstance() {
        if (instance == null) {
            instance = new LocalHttpServerManager();
        }
        return instance;
    }

    public void init(Context context) {
        this.appContext = context.getApplicationContext();
    }

    public void startServer() {
        if (server != null && server.isAlive()) {
            Log.d(TAG, "Server already running");
            return;
        }

        server = new LocalHttpServer(7777);
        server.setContext(appContext);
        server.setCallback((surveyId, answers) -> {
            Log.d(TAG, "Survey received: " + surveyId);
            forwardToRemoteServer(surveyId, answers);
            return "{\"success\":true,\"message\":\"Survey submitted\"}";
        });

        try {
            server.start();
            Log.d(TAG, "Local HTTP server started on port 7777");
            Log.d(TAG, "Survey URL: http://localhost:7777/survey/{id}");
            Log.d(TAG, "Test URLs:");
            Log.d(TAG, "  http://localhost:7777/test/mbti");
            Log.d(TAG, "  http://localhost:7777/test/constellation");
            Log.d(TAG, "  http://localhost:7777/test/bazi");
            Log.d(TAG, "  http://localhost:7777/test/bigfive");
        } catch (Exception e) {
            Log.e(TAG, "Failed to start server", e);
        }
    }

    public void stopServer() {
        if (server != null && server.isAlive()) {
            server.stop();
            Log.d(TAG, "Server stopped");
        }
    }

    public boolean isRunning() {
        return server != null && server.isAlive();
    }

    public String getLocalUrl() {
        return "http://localhost:7777";
    }

    public String getSurveyUrl(String surveyId) {
        return "http://localhost:7777/survey/" + surveyId;
    }

    public String getTestUrl(String testName) {
        return "http://localhost:7777/test/" + testName;
    }

    public String getResultUrl(String testName) {
        return "http://localhost:7777/result/" + testName + ".json";
    }

    public String getSubmitResultUrl(String testName, long timestamp) {
        return "http://localhost:7777/test/result/" + testName + "_" + timestamp + ".json";
    }

    public String getCertificationSaveUrl() {
        return "http://localhost:7777/certification/save";
    }

    public String getCertificationGetUrl() {
        return "http://localhost:7777/certification/get";
    }

    public boolean hasTestResult(String testName) {
        if (server == null) return false;
        File resultDir = new File(appContext.getFilesDir(), "test_results");
        if (!resultDir.exists()) return false;
        File[] files = resultDir.listFiles((dir, name) -> name.startsWith("test_result_" + testName + "_") && name.endsWith(".json"));
        return files != null && files.length > 0;
    }

    public void setRemoteServerUrl(String url) {
        this.remoteServerUrl = url;
    }

    private void forwardToRemoteServer(String surveyId, String answers) {
        String url = remoteServerUrl + "/api/survey/" + surveyId;
        
        RequestBody body = RequestBody.create(answers, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, java.io.IOException e) {
                Log.e(TAG, "Failed to forward survey to remote: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws java.io.IOException {
                Log.d(TAG, "Survey forwarded to remote server, status: " + response.code());
            }
        });
    }

    public void submitSurvey(String surveyId, String answers, Callback callback) {
        String url = remoteServerUrl + "/api/survey/" + surveyId;
        
        RequestBody body = RequestBody.create(answers, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        httpClient.newCall(request).enqueue(callback);
    }
}
