package com.ailove.app.utils;

import android.content.Context;
import android.util.Log;
import fi.iki.elonen.NanoHTTPD;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LocalHttpServer extends NanoHTTPD {
    private static final String TAG = "LocalHttpServer";
    private static final int DEFAULT_PORT = 7777;
    private ServerCallback callback;
    private Context context;

    public interface ServerCallback {
        String onSurveyRequest(String surveyId, String answers);
    }

    public LocalHttpServer() {
        super(DEFAULT_PORT);
    }

    public LocalHttpServer(int port) {
        super(port);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setCallback(ServerCallback callback) {
        this.callback = callback;
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        String method = session.getMethod().name();

        Log.d(TAG, "Received " + method + " request: " + uri);

        if (method.equals("POST")) {
            if (uri.startsWith("/survey/")) {
                return handleSurveyRequest(session, uri);
            }
            if (uri.startsWith("/test/result/")) {
                return handleTestResult(session, uri);
            }
            if (uri.equals("/certification/save") || uri.startsWith("/certification/save")) {
                return handleCertificationSave(session, uri);
            }
        }

        if (method.equals("GET")) {
            if (uri.equals("/health")) {
                return newFixedLengthResponse(Response.Status.OK, "application/json", "{\"status\":\"ok\"}");
            }

            if (uri.startsWith("/test/")) {
                return handleTestRequest(uri);
            }

            if (uri.startsWith("/result/")) {
                return handleGetResult(uri);
            }

            if (uri.equals("/certification/get") || uri.equals("/certification")) {
                return handleCertificationGet();
            }
        }

        return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not Found: " + uri);
    }

    private Response handleTestRequest(String uri) {
        String testName = uri.replace("/test/", "");
        
        String[] validTests = {"mbti.html", "constellation.html", "bazi.html", "bigfive.html"};
        boolean isValid = false;
        for (String test : validTests) {
            if (uri.endsWith(test) || uri.equals("/test/" + test.replace(".html", ""))) {
                isValid = true;
                testName = test;
                break;
            }
        }

        if (!isValid) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Test not found: " + uri);
        }

        try {
            InputStream is = context.getAssets().open("test/" + testName);
            String content = convertStreamToString(is);
            is.close();
            return newFixedLengthResponse(Response.Status.OK, "text/html; charset=utf-8", content);
        } catch (Exception e) {
            Log.e(TAG, "Error loading test file: " + e.getMessage());
            return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Test not found: " + testName);
        }
    }

    private Response handleGetResult(String uri) {
        if (uri.equals("/result/") || uri.equals("/result")) {
            return handleGetAllResults();
        }
        
        String fileName = uri.replace("/result/", "").replace(".json", "");
        
        File resultDir = new File(context.getFilesDir(), "test_results");
        if (!resultDir.exists()) {
            resultDir.mkdirs();
        }
        
        File[] files = resultDir.listFiles((dir, name) -> name.startsWith(fileName + "_") && name.endsWith(".json"));
        
        if (files != null && files.length > 0) {
            Arrays.sort(files, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));
            try {
                FileInputStream fis = new FileInputStream(files[0]);
                String content = convertStreamToString(fis);
                fis.close();
                return newFixedLengthResponse(Response.Status.OK, "application/json", content);
            } catch (Exception e) {
                Log.e(TAG, "Error reading result: " + e.getMessage());
                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "application/json", "{\"error\":\"Failed to read result\"}");
            }
        } else {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, "application/json", "{\"error\":\"No result found\"}");
        }
    }

    private Response handleGetAllResults() {
        StringBuilder sb = new StringBuilder("{\"results\":[");
        
        File resultDir = new File(context.getFilesDir(), "test_results");
        if (resultDir.exists()) {
            File[] files = resultDir.listFiles((dir, name) -> name.endsWith(".json"));
            boolean first = true;
            if (files != null) {
                Arrays.sort(files, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));
                for (File file : files) {
                    try {
                        FileInputStream fis = new FileInputStream(file);
                        String content = convertStreamToString(fis);
                        fis.close();
                        if (!first) sb.append(",");
                        sb.append("{\"name\":\"").append(file.getName().replace(".json", "").replace("test_result_", "")).append("\",\"data\":").append(content).append("}");
                        first = false;
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading result file: " + e.getMessage());
                    }
                }
            }
        }
        
        sb.append("]}");
        return newFixedLengthResponse(Response.Status.OK, "application/json", sb.toString());
    }

    private Response handleTestResult(IHTTPSession session, String uri) {
        try {
            Map<String, String> files = new HashMap<>();
            session.parseBody(files);
            String postData = files.get("postData");

            String[] parts = uri.replace("/test/result/", "").replace(".json", "").split("_");
            String testName = parts[0];
            long timestamp = System.currentTimeMillis();
            if (parts.length > 1) {
                try {
                    timestamp = Long.parseLong(parts[1]);
                } catch (Exception e) {}
            }

            Log.d(TAG, "Test result for: " + testName + ", Data: " + postData);

            File resultDir = new File(context.getFilesDir(), "test_results");
            if (!resultDir.exists()) {
                resultDir.mkdirs();
            }

            File resultFile = new File(resultDir, "test_result_" + testName + "_" + timestamp + ".json");
            FileOutputStream fos = new FileOutputStream(resultFile);
            fos.write(postData.getBytes());
            fos.close();

            String result = "{\"success\":true,\"message\":\"Result saved\",\"timestamp\":" + timestamp + "}";
            return newFixedLengthResponse(Response.Status.OK, "application/json", result);
        } catch (Exception e) {
            Log.e(TAG, "Error saving test result: " + e.getMessage());
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "application/json", "{\"success\":false,\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private Response handleSurveyRequest(IHTTPSession session, String uri) {
        try {
            Map<String, String> files = new HashMap<>();
            session.parseBody(files);
            String postData = files.get("postData");

            String surveyId = uri.substring("/survey/".length());

            Log.d(TAG, "Survey ID: " + surveyId + ", Data: " + postData);

            String result;
            if (callback != null) {
                result = callback.onSurveyRequest(surveyId, postData);
            } else {
                result = "{\"success\":true,\"message\":\"Survey received\"}";
            }

            return newFixedLengthResponse(Response.Status.OK, "application/json", result);
        } catch (Exception e) {
            Log.e(TAG, "Error handling survey request", e);
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "application/json", "{\"success\":false,\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private String convertStreamToString(InputStream is) throws Exception {
        java.io.ByteArrayOutputStream result = new java.io.ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8");
    }

    private Response handleCertificationSave(IHTTPSession session, String uri) {
        try {
            Map<String, String> files = new HashMap<>();
            session.parseBody(files);
            String postData = files.get("postData");

            Log.d(TAG, "Certification save: " + postData);

            File resultFile = new File(context.getFilesDir(), "certification_user.json");
            FileOutputStream fos = new FileOutputStream(resultFile);
            fos.write(postData.getBytes());
            fos.close();

            String result = "{\"success\":true,\"message\":\"Certification saved\"}";
            return newFixedLengthResponse(Response.Status.OK, "application/json", result);
        } catch (Exception e) {
            Log.e(TAG, "Error saving certification: " + e.getMessage());
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "application/json", "{\"success\":false,\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private Response handleCertificationGet() {
        File resultFile = new File(context.getFilesDir(), "certification_user.json");
        
        if (resultFile.exists()) {
            try {
                FileInputStream fis = new FileInputStream(resultFile);
                String content = convertStreamToString(fis);
                fis.close();
                return newFixedLengthResponse(Response.Status.OK, "application/json", content);
            } catch (Exception e) {
                Log.e(TAG, "Error reading certification: " + e.getMessage());
                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "application/json", "{\"error\":\"Failed to read certification\"}");
            }
        } else {
            return newFixedLengthResponse(Response.Status.OK, "application/json", "{}");
        }
    }

    public String getServerUrl() {
        return "http://localhost:" + getListeningPort();
    }

    public String getTestUrl(String testName) {
        return "http://localhost:" + getListeningPort() + "/test/" + testName;
    }

    public String getResultUrl(String testName) {
        return "http://localhost:" + getListeningPort() + "/result/" + testName + ".json";
    }

    public boolean hasResult(String testName) {
        File resultFile = new File(context.getFilesDir(), "test_result_" + testName + ".json");
        return resultFile.exists();
    }
}
