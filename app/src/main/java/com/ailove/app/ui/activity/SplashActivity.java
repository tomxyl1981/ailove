package com.ailove.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.ailove.app.R;
import com.ailove.app.utils.LocalHttpServerManager;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        LocalHttpServerManager.getInstance().init(this);
        LocalHttpServerManager.getInstance().startServer();
        
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
            finish();
        }, 2000);
    }
}
