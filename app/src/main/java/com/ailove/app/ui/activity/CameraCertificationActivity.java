package com.ailove.app.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.ailove.app.R;
import com.ailove.app.api.ApiClient;
import com.ailove.app.model.FaceAnalyzeResult;
import com.ailove.app.model.CertificationRecord;
import com.ailove.app.storage.LocalJsonPersistence;
import com.google.gson.Gson;

import android.content.Intent;

public class CameraCertificationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_certification);

        Button btnComplete = findViewById(R.id.btn_complete_demo);
        btnComplete.setOnClickListener(v -> {
            // 演示用：模拟拍照/视频认证通过结果
            ApiClient.getInstance().verifyPhotoVideo("", new ApiClient.Callback<FaceAnalyzeResult>() {
                @Override
                public void onSuccess(FaceAnalyzeResult result) {
                    // 保存到本地JSON
                    CertificationRecord rec = new CertificationRecord();
                    rec.id = "photo_cert_" + System.currentTimeMillis();
                    rec.category = "PHOTO_CERT";
                    rec.status = "completed";
                    rec.timestamp = System.currentTimeMillis();
                    rec.dataJson = new Gson().toJson(result);
                    LocalJsonPersistence.saveCertificationRecord(CameraCertificationActivity.this, rec);
                    Toast.makeText(CameraCertificationActivity.this, "照片认证完成，已保存记录", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onError(String error) {
                    Toast.makeText(CameraCertificationActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
