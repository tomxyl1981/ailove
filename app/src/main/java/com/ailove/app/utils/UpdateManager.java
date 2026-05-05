package com.ailove.app.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class UpdateManager {
    private static final String TAG = "UpdateManager";
    private static UpdateManager instance;
    private Activity activity;
    private Handler handler;

    public static UpdateManager getInstance(Activity activity) {
        instance = new UpdateManager(activity);
        return instance;
    }

    private UpdateManager(Activity activity) {
        this.activity = activity;
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void checkForUpdate() {
        Log.d(TAG, "checkForUpdate called");
        Toast.makeText(activity, "正在检查更新...", Toast.LENGTH_SHORT).show();
        
        VersionChecker.checkUpdate(activity, new VersionChecker.VersionCheckCallback() {
            @Override
            public void onResult(boolean hasUpdate, String latestVersion, String updateUrl, String updateNote) {
                Log.d(TAG, "onResult: hasUpdate=" + hasUpdate + " version=" + latestVersion);
                handler.post(() -> {
                    if (hasUpdate) {
                        showUpdateDialog(latestVersion, updateUrl, updateNote);
                    } else {
                        showNoUpdateDialog();
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "onError: " + error);
                handler.post(() -> {
                    Toast.makeText(activity, "检查更新失败: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showNoUpdateDialog() {
        try {
            new AlertDialog.Builder(activity)
                .setTitle("版本更新")
                .setMessage("当前已是最新版本")
                .setPositiveButton("确定", null)
                .show();
        } catch (Exception e) {
            Log.e(TAG, "showNoUpdateDialog error", e);
        }
    }

    private void showUpdateDialog(String version, String url, String note) {
        try {
            StringBuilder message = new StringBuilder();
            message.append("发现新版本: ").append(version).append("\n\n");
            if (note != null && !note.isEmpty()) {
                message.append("更新内容:\n").append(note).append("\n\n");
            }
            message.append("是否立即更新?");

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("版本更新");
            builder.setMessage(message.toString());
            builder.setPositiveButton("立即更新", (dialog, which) -> {
                downloadAndInstall(url);
            });
            builder.setNegativeButton("稍后再说", null);
            builder.setCancelable(false);
            builder.show();
        } catch (Exception e) {
            Log.e(TAG, "showUpdateDialog error", e);
        }
    }

    private void downloadAndInstall(String url) {
        try {
            Log.d(TAG, "Downloading from: " + url);
            Toast.makeText(activity, "开始下载...", Toast.LENGTH_SHORT).show();
            
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDescription("正在下载新版本...");
            request.setTitle("AiLove 更新");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "AiLove_update.apk");

            DownloadManager dm = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
            long downloadId = dm.enqueue(request);
            Log.d(TAG, "Download started, id: " + downloadId);

            showDownloadProgress(downloadId);
        } catch (Exception e) {
            Log.e(TAG, "Download failed", e);
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            } catch (Exception ex) {
                Log.e(TAG, "Failed to open browser", ex);
            }
        }
    }

    private void showDownloadProgress(long downloadId) {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (id == downloadId) {
                    Log.d(TAG, "Download completed");
                    installDownloadedApk(downloadId);
                    try {
                        activity.unregisterReceiver(this);
                    } catch (Exception e) {}
                }
            }
        };
        activity.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private void installDownloadedApk(long downloadId) {
        try {
            DownloadManager dm = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = dm.getUriForDownloadedFile(downloadId);
            if (uri != null) {
                Log.d(TAG, "Installing from: " + uri);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Install failed", e);
        }
    }
}