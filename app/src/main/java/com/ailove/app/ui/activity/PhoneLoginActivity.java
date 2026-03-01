package com.ailove.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.ailove.app.R;
import com.ailove.app.api.ApiClient;

public class PhoneLoginActivity extends AppCompatActivity {
    private TextView tvPhoneNumber;
    private Button btnSendCode;
    private Button btnVerifyCode;
    private View codeInputContainer;
    private EditText etCode1, etCode2, etCode3, etCode4;
    private TextView tvCountdown;
    
    private CountDownTimer countDownTimer;
    private String phoneNumber = "138****8888"; // Mock手机号
    private String verificationCode = "1234"; // Mock验证码
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        
        initViews();
        setupListeners();
        displayPhoneNumber();
    }
    
    private void initViews() {
        tvPhoneNumber = findViewById(R.id.tv_phone_number);
        btnSendCode = findViewById(R.id.btn_send_code);
        btnVerifyCode = findViewById(R.id.btn_verify_code);
        codeInputContainer = findViewById(R.id.code_input_container);
        etCode1 = findViewById(R.id.et_code_1);
        etCode2 = findViewById(R.id.et_code_2);
        etCode3 = findViewById(R.id.et_code_3);
        etCode4 = findViewById(R.id.et_code_4);
        tvCountdown = findViewById(R.id.tv_countdown);
        
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }
    
    private void setupListeners() {
        btnSendCode.setOnClickListener(v -> sendVerificationCode());
        btnVerifyCode.setOnClickListener(v -> verifyCode());
        
        // 设置验证码输入框自动跳转
        setupCodeInputWatchers();
    }
    
    private void setupCodeInputWatchers() {
        etCode1.addTextChangedListener(new CodeTextWatcher(etCode1, etCode2));
        etCode2.addTextChangedListener(new CodeTextWatcher(etCode2, etCode3));
        etCode3.addTextChangedListener(new CodeTextWatcher(etCode3, etCode4));
        etCode4.addTextChangedListener(new CodeTextWatcher(etCode4, null));
    }
    
    private void displayPhoneNumber() {
        // 这里应该获取真实的本机号码，现在使用mock数据
        tvPhoneNumber.setText("+86 " + phoneNumber);
    }
    
    private void sendVerificationCode() {
        // Mock发送验证码逻辑
        Toast.makeText(this, "验证码已发送至 " + phoneNumber, Toast.LENGTH_SHORT).show();
        
        // 显示验证码输入界面
        codeInputContainer.setVisibility(View.VISIBLE);
        btnSendCode.setVisibility(View.GONE);
        
        // 开始倒计时
        startCountdown();
        
        // 自动填充mock验证码（方便测试）
        etCode1.setText("1");
        etCode2.setText("2");
        etCode3.setText("3");
        etCode4.setText("4");
    }
    
    private void startCountdown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                tvCountdown.setText(seconds + "秒后重新发送");
                tvCountdown.setVisibility(View.VISIBLE);
            }
            
            @Override
            public void onFinish() {
                tvCountdown.setVisibility(View.GONE);
                btnSendCode.setVisibility(View.VISIBLE);
                btnSendCode.setText("重新发送验证码");
            }
        }.start();
    }
    
    private void verifyCode() {
        String code1 = etCode1.getText().toString().trim();
        String code2 = etCode2.getText().toString().trim();
        String code3 = etCode3.getText().toString().trim();
        String code4 = etCode4.getText().toString().trim();
        
        if (code1.isEmpty() || code2.isEmpty() || code3.isEmpty() || code4.isEmpty()) {
            Toast.makeText(this, "请输入完整的验证码", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String inputCode = code1 + code2 + code3 + code4;
        
        if (inputCode.equals(verificationCode)) {
            // 验证成功，注册新用户
            registerNewUser();
        } else {
            Toast.makeText(this, "验证码错误", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void registerNewUser() {
        // 显示加载状态
        btnVerifyCode.setText("注册中...");
        btnVerifyCode.setEnabled(false);
        
        // 调用API注册用户（仅手机号）
        ApiClient.getInstance().register(phoneNumber, null, new ApiClient.Callback<com.ailove.app.model.AuthResult>() {
            @Override
            public void onSuccess(com.ailove.app.model.AuthResult result) {
                if (result.success) {
                    Toast.makeText(PhoneLoginActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                    
                    // 跳转到主界面
                    Intent intent = new Intent(PhoneLoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    showError("注册失败，请重试");
                }
            }
            
            @Override
            public void onError(String error) {
                showError(error);
            }
        });
    }
    
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        btnVerifyCode.setText("验证并登录");
        btnVerifyCode.setEnabled(true);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
    
    // 验证码输入框监听器
    private class CodeTextWatcher implements TextWatcher {
        private EditText currentEditText;
        private EditText nextEditText;
        
        public CodeTextWatcher(EditText current, EditText next) {
            this.currentEditText = current;
            this.nextEditText = next;
        }
        
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        
        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 1 && nextEditText != null) {
                nextEditText.requestFocus();
            }
        }
    }
}