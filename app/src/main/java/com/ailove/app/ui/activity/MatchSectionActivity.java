package com.ailove.app.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.ailove.app.R;
import com.ailove.app.api.ApiClient;
import com.ailove.app.model.MatchSquareResult;
import com.ailove.app.storage.LocalJsonPersistence;
import com.ailove.app.model.CertificationRecord;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;

public class MatchSectionActivity extends AppCompatActivity {
    public static final String EXTRA_MODE = "mode"; // baZi, constellation, mbti, bigfive
    private String mode;
    private Gson gson = new Gson();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_section);
        mode = getIntent().getStringExtra(EXTRA_MODE);
        if (mode == null) mode = "baZi";
        Button btnSubmit = findViewById(R.id.btn_submit);
        EditText etInput = findViewById(R.id.et_input);
        btnSubmit.setOnClickListener(v -> {
            String input = etInput != null ? etInput.getText().toString() : "";
            // Compute a mock score
            int score = Math.abs((mode + input).hashCode()) % 100;
            MatchSquareResult result = new MatchSquareResult();
            result.mode = mode;
            result.score = score;
            result.timestamp = System.currentTimeMillis();
            CertificationRecord rec = new CertificationRecord();
            rec.id = mode + "_match_" + result.timestamp;
            rec.category = "MATCH_SQUARE_SECTION";
            rec.dataJson = gson.toJson(result);
            rec.status = "completed";
            rec.timestamp = result.timestamp;
            LocalJsonPersistence.saveCertificationRecord(this, rec);
            Toast.makeText(this, "匹配完成，分数: " + score, Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
