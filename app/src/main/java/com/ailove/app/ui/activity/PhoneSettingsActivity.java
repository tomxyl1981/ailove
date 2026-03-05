package com.ailove.app.ui.activity;

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

public class PhoneSettingsActivity extends AppCompatActivity {

    private EditText etPhone;
    private EditText etCode1, etCode2, etCode3, etCode4;
    private Button btnSendCode;
    private Button btnBind;
    private TextView tvCountdown;
    private View codeContainer;

    private CountDownTimer countDownTimer;
    private String phoneNumber;
    private static final String MOCK_CODE = "1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_settings);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etPhone = findViewById(R.id.et_phone);
        etCode1 = findViewById(R.id.et_code_1);
        etCode2 = findViewById(R.id.et_code_2);
        etCode3 = findViewById(R.id.et_code_3);
        etCode4 = findViewById(R.id.et_code_4);
        btnSendCode = findViewById(R.id.btn_send_code);
        btnBind = findViewById(R.id.btn_bind);
        tvCountdown = findViewById(R.id.tv_countdown);
        codeContainer = findViewById(R.id.code_container);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void setupListeners() {
        btnSendCode.setOnClickListener(v -> sendCode());
        btnBind.setOnClickListener(v -> bind());

        etCode1.addTextChangedListener(new CodeWatcher(etCode1, etCode2));
        etCode2.addTextChangedListener(new CodeWatcher(etCode2, etCode3));
        etCode3.addTextChangedListener(new CodeWatcher(etCode3, etCode4));
        etCode4.addTextChangedListener(new CodeWatcher(etCode4, null));
    }

    private void sendCode() {
        String input = etPhone.getText().toString().trim();
        
        if (input.isEmpty()) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!input.startsWith("1") || input.length() != 11) {
            Toast.makeText(this, "请输入正确的11位手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        phoneNumber = input;
        btnSendCode.setEnabled(false);
        btnSendCode.setText("发送中...");

        new android.os.Handler().postDelayed(() -> {
            runOnUiThread(() -> {
                Toast.makeText(this, "验证码已发送", Toast.LENGTH_SHORT).show();
                codeContainer.setVisibility(View.VISIBLE);
                btnSendCode.setVisibility(View.GONE);
                startCountdown();
                
                etCode1.setText("1");
                etCode2.setText("2");
                etCode3.setText("3");
                etCode4.setText("4");
            });
        }, 500);
    }

    private void startCountdown() {
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millis) {
                tvCountdown.setText((millis / 1000) + "秒后重发");
                tvCountdown.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                tvCountdown.setVisibility(View.GONE);
                btnSendCode.setVisibility(View.VISIBLE);
                btnSendCode.setText("重新发送");
                btnSendCode.setEnabled(true);
            }
        }.start();
    }

    private void bind() {
        String c1 = etCode1.getText().toString();
        String c2 = etCode2.getText().toString();
        String c3 = etCode3.getText().toString();
        String c4 = etCode4.getText().toString();

        if (c1.isEmpty() || c2.isEmpty() || c3.isEmpty() || c4.isEmpty()) {
            Toast.makeText(this, "请输入完整验证码", Toast.LENGTH_SHORT).show();
            return;
        }

        String inputCode = c1 + c2 + c3 + c4;

        if (!inputCode.equals(MOCK_CODE)) {
            Toast.makeText(this, "验证码错误", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "手机绑定成功", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private class CodeWatcher implements TextWatcher {
        private final EditText current, next;

        CodeWatcher(EditText current, EditText next) {
            this.current = current;
            this.next = next;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 1 && next != null) {
                next.requestFocus();
            }
        }
    }
}
