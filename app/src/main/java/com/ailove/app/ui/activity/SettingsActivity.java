package com.ailove.app.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.ailove.app.R;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "ailove_prefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_EMAIL = "user_email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        findViewById(R.id.item_phone).setOnClickListener(v -> {
            startActivity(new Intent(this, PhoneSettingsActivity.class));
        });

        findViewById(R.id.item_logout).setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            prefs.edit().putBoolean(KEY_IS_LOGGED_IN, false).apply();
            
            Toast.makeText(this, "已退出登录", Toast.LENGTH_SHORT).show();
            
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            
            android.os.Process.killProcess(android.os.Process.myPid());
        });
    }
}
