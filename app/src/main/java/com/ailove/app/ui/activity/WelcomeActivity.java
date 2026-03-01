package com.ailove.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.ailove.app.R;

public class WelcomeActivity extends AppCompatActivity {
    private static final int REQUEST_USER_PROTOCOL = 1001;
    private static final int REQUEST_PRIVACY_POLICY = 1002;
    
    private CheckBox cbProtocol;
    private boolean userProtocolAgreed = false;
    private boolean privacyPolicyAgreed = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        
        cbProtocol = findViewById(R.id.cb_protocol);
        
        // 设置协议文本点击事件
        findViewById(R.id.tv_user_protocol).setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, UserProtocolActivity.class);
            startActivityForResult(intent, REQUEST_USER_PROTOCOL);
        });
        
        findViewById(R.id.tv_privacy_policy).setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, PrivacyPolicyActivity.class);
            startActivityForResult(intent, REQUEST_PRIVACY_POLICY);
        });
        
        findViewById(R.id.btn_phone_login).setOnClickListener(v -> performPhoneLogin());
        findViewById(R.id.btn_wechat_login).setOnClickListener(v -> performWeChatLogin());
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK && data != null) {
            boolean agreed = data.getBooleanExtra("agreed", false);
            
            switch (requestCode) {
                case REQUEST_USER_PROTOCOL:
                    userProtocolAgreed = agreed;
                    break;
                case REQUEST_PRIVACY_POLICY:
                    privacyPolicyAgreed = agreed;
                    break;
            }
            
            // 如果两个协议都同意了，自动勾选主协议框
            if (userProtocolAgreed && privacyPolicyAgreed) {
                cbProtocol.setChecked(true);
            }
        }
    }
    
    private void performPhoneLogin() {
        if (!cbProtocol.isChecked()) {
            Toast.makeText(this, "请先阅读并同意用户协议", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 跳转到手机号登录页面
        Intent intent = new Intent(WelcomeActivity.this, PhoneLoginActivity.class);
        startActivity(intent);
    }
    
    private void performWeChatLogin() {
        if (!cbProtocol.isChecked()) {
            Toast.makeText(this, "请先阅读并同意用户协议", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
        showAfterLogin();
    }
    
    private void showAfterLogin() {
        findViewById(R.id.login_buttons_container).setVisibility(View.GONE);
        findViewById(R.id.after_login_container).setVisibility(View.VISIBLE);
        
        findViewById(R.id.btn_verify_now).setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, RealNameVerifyActivity.class);
            startActivity(intent);
        });
        
        findViewById(R.id.btn_look_around).setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}