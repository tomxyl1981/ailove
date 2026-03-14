package com.ailove.app.ui.activity;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ailove.app.R;
import com.ailove.app.adapter.CertificationHistoryAdapter;
import com.ailove.app.model.CertificationRecord;
import com.ailove.app.storage.LocalJsonPersistence;
import java.util.List;
import java.io.File;

public class CertificationHistoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certification_history);

        RecyclerView rv = findViewById(R.id.recycler_history);
        List<CertificationRecord> records = LocalJsonPersistence.loadCertificationRecords(this);
        CertificationHistoryAdapter adapter = new CertificationHistoryAdapter(records);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        // Export button
        findViewById(R.id.btn_export_history).setOnClickListener(v -> {
            String json = new com.google.gson.Gson().toJson(records);
            try {
                java.io.File dir = getExternalCacheDir();
                if (dir == null) dir = getCacheDir();
                java.io.File exportFile = new java.io.File(dir, "certifications_export.json");
                try (java.io.FileWriter writer = new java.io.FileWriter(exportFile)) {
                    writer.write(json);
                }
                android.net.Uri contentUri = androidx.core.content.FileProvider.getUriForFile(
                        this, getPackageName() + ".fileprovider", exportFile);
                android.content.Intent share = new android.content.Intent(android.content.Intent.ACTION_SEND);
                share.setType("application/json");
                share.putExtra(android.content.Intent.EXTRA_STREAM, contentUri);
                share.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(android.content.Intent.createChooser(share, "分享认证历史"));
            } catch (Exception e) {
                Toast.makeText(this, "导出失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
