package com.ailove.app.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VersionChecker {
    private static final String TAG = "VersionChecker";
    private static final String CHECK_URL = "https://jiehun.mynatapp.cc/api/version";
    private static final String DOWNLOAD_URL = "https://jiehun.mynatapp.cc/apk/app-release.apk";

    public interface VersionCheckCallback {
        void onResult(boolean hasUpdate, String latestVersion, String updateUrl, String updateNote);
        void onError(String error);
    }

    public static void checkUpdate(Context context, VersionCheckCallback callback) {
        new Thread(() -> {
            try {
                PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                String currentVersion = pInfo.versionName;
                int currentVersionCode = pInfo.versionCode;

                Log.d(TAG, "Current version: " + currentVersion + " (" + currentVersionCode + ")");

                URL url = new URL(CHECK_URL + "?current_version=" + currentVersion + "&version_code=" + currentVersionCode);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Response code: " + responseCode);

                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONObject json = new JSONObject(response.toString());
                    boolean hasUpdate = json.optBoolean("has_update", false);
                    String latestVersion = json.optString("latest_version", "");
                    String updateNote = json.optString("update_note", "");
                    // Fixed download URL
                    String updateUrl = hasUpdate ? DOWNLOAD_URL : "";

                    Log.d(TAG, "Has update: " + hasUpdate + ", latest: " + latestVersion);

                    if (callback != null) {
                        callback.onResult(hasUpdate, latestVersion, updateUrl, updateNote);
                    }
                } else {
                    if (callback != null) {
                        callback.onError("Server error: " + responseCode);
                    }
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Version check error", e);
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        }).start();
    }

    public static String getCurrentVersion(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName + " (" + pInfo.versionCode + ")";
        } catch (PackageManager.NameNotFoundException e) {
            return "Unknown";
        }
    }
}