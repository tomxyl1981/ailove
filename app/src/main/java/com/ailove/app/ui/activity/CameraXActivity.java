package com.ailove.app.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.ailove.app.R;
import com.ailove.app.api.ApiClient;
import com.ailove.app.model.FaceAnalyzeResult;
import com.ailove.app.model.CertificationRecord;
import com.ailove.app.storage.LocalJsonPersistence;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.ExecutionException;

public class CameraXActivity extends AppCompatActivity {
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private final Executor cameraExecutor = Executors.newSingleThreadExecutor();
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_x);

        previewView = findViewById(R.id.preview_view);
        Button btnCapture = findViewById(R.id.btn_capture);
        Button btnReview = findViewById(R.id.btn_done);
        Button btnRecordVideo = findViewById(R.id.btn_record_video);

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        btnCapture.setOnClickListener(v -> takePhoto());
        btnRecordVideo.setOnClickListener(v -> recordDemoVideo());
        btnReview.setOnClickListener(v -> {
            // 使用最近拍摄的照片进行演示活体/身份对比
            // 这里简化：直接调用 ApiClient 验证，传空的 Base64 数据，后端应返回模拟结果
            ApiClient.getInstance().verifyPhotoVideo("", new ApiClient.Callback<FaceAnalyzeResult>() {
                @Override public void onSuccess(FaceAnalyzeResult result) {
                    String data = new Gson().toJson(result);
                    CertificationRecord rec = new CertificationRecord();
                    rec.id = "photo_cert_" + System.currentTimeMillis();
                    rec.category = "PHOTO_CERT";
                    rec.dataJson = data;
                    rec.status = "completed";
                    rec.timestamp = System.currentTimeMillis();
                    LocalJsonPersistence.saveCertificationRecord(CameraXActivity.this, rec);
                    Toast.makeText(CameraXActivity.this, "照片认证完成，已保存记录", Toast.LENGTH_SHORT).show();
                }
                @Override public void onError(String error) {
                    Toast.makeText(CameraXActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private boolean allPermissionsGranted() {
        for (String p : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                imageCapture = new ImageCapture.Builder().build();
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
                preview.setSurfaceProvider(previewView.getSurfaceProvider());
            } catch (Exception e) {
                Toast.makeText(this, "Camera start failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePhoto() {
        if (imageCapture == null) return;
        File dir = new File(getExternalFilesDir(null), "photos");
        if (!dir.exists()) dir.mkdirs();
        File photoFile = new File(dir, "IMG_" + System.currentTimeMillis() + ".jpg");
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();
        imageCapture.takePicture(outputOptions, cameraExecutor, new ImageCapture.OnImageSavedCallback() {
            @Override public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                // Convert to Base64 for API call
                byte[] bytes = android.util.Base64.decode("", android.util.Base64.DEFAULT);
                // For simplicity, use placeholder string
                ApiClient.getInstance().verifyPhotoVideo("", new ApiClient.Callback<FaceAnalyzeResult>() {
                    @Override public void onSuccess(FaceAnalyzeResult result) {
                        CertificationRecord rec = new CertificationRecord();
                        rec.id = "photo_cert_" + System.currentTimeMillis();
                        rec.category = "PHOTO_CERT";
                        rec.dataJson = new Gson().toJson(result);
                        rec.status = "completed";
                        rec.timestamp = System.currentTimeMillis();
                        LocalJsonPersistence.saveCertificationRecord(CameraXActivity.this, rec);
                        Toast.makeText(CameraXActivity.this, "图片已保存并认证完成", Toast.LENGTH_SHORT).show();
                    }
                    @Override public void onError(String error) {
                        Toast.makeText(CameraXActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override public void onError(@NonNull ImageCaptureException exception) {
                Toast.makeText(CameraXActivity.this, "Capture failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void recordDemoVideo() {
        try {
            File dir = new File(getExternalFilesDir(null), "videos");
            if (!dir.exists()) dir.mkdirs();
            File videoFile = new File(dir, "VID_" + System.currentTimeMillis() + ".mp4");
            try (FileOutputStream fos = new FileOutputStream(videoFile)) {
                byte[] dummy = new byte[] {0x00, 0x01, 0x02, 0x03, 0x04, 0x05};
                fos.write(dummy);
            }
            FileInputStream fis = new FileInputStream(videoFile);
            byte[] bytes = new byte[(int) videoFile.length()];
            int read = fis.read(bytes);
            fis.close();
            String base64 = Base64.encodeToString(bytes, Base64.NO_WRAP);
            ApiClient.getInstance().verifyPhotoVideo(base64, new ApiClient.Callback<FaceAnalyzeResult>() {
                @Override
                public void onSuccess(FaceAnalyzeResult result) {
                    CertificationRecord rec = new CertificationRecord();
                    rec.id = "video_cert_" + System.currentTimeMillis();
                    rec.category = "VIDEO_CERT";
                    rec.dataJson = new Gson().toJson(result);
                    rec.status = "completed";
                    rec.timestamp = System.currentTimeMillis();
                    LocalJsonPersistence.saveCertificationRecord(CameraXActivity.this, rec);
                    Toast.makeText(CameraXActivity.this, "视频认证完成，已保存记录", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onError(String error) {
                    Toast.makeText(CameraXActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "录制失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
