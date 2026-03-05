package com.ailove.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.ailove.app.R;
import com.ailove.app.api.ApiClient;
import org.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import java.io.IOException;

public class EmailLoginActivity extends AppCompatActivity {

    private static final String TAG = "EmailLogin";
    private static final String VERIFY_API_URL = "https://lamellirostral-nonirenical-kobe.ngrok-free.dev/api/v1/auth/send-verify-code";
    
    private EditText etEmail;
    private EditText etCode1, etCode2, etCode3, etCode4, etCode5, etCode6;
    private Button btnSendCode;
    private Button btnLogin;
    private TextView tvCountdown;
    private View codeContainer;

    private CountDownTimer countDownTimer;
    private String email;
    private String verifyCode = "";
    private OkHttpClient httpClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etCode1 = findViewById(R.id.et_code_1);
        etCode2 = findViewById(R.id.et_code_2);
        etCode3 = findViewById(R.id.et_code_3);
        etCode4 = findViewById(R.id.et_code_4);
        etCode5 = findViewById(R.id.et_code_5);
        etCode6 = findViewById(R.id.et_code_6);
        btnSendCode = findViewById(R.id.btn_send_code);
        btnLogin = findViewById(R.id.btn_login);
        tvCountdown = findViewById(R.id.tv_countdown);
        codeContainer = findViewById(R.id.code_container);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void setupListeners() {
        btnSendCode.setOnClickListener(v -> sendCode());
        btnLogin.setOnClickListener(v -> login());

        etCode1.addTextChangedListener(new CodeWatcher(etCode1, etCode2));
        etCode2.addTextChangedListener(new CodeWatcher(etCode2, etCode3));
        etCode3.addTextChangedListener(new CodeWatcher(etCode3, etCode4));
        etCode4.addTextChangedListener(new CodeWatcher(etCode4, etCode5));
        etCode5.addTextChangedListener(new CodeWatcher(etCode5, etCode6));
        etCode6.addTextChangedListener(new CodeWatcher(etCode6, null));
    }

    private void sendCode() {
        email = etEmail.getText().toString().trim();
        
        if (email.isEmpty()) {
            Toast.makeText(this, "请输入邮箱地址", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.contains("@")) {
            Toast.makeText(this, "请输入正确的邮箱地址", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSendCode.setEnabled(false);
        btnSendCode.setText("发送中...");

        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
            .url(VERIFY_API_URL)
            .post(body)
            .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(EmailLoginActivity.this, "发送失败，请重试", Toast.LENGTH_SHORT).show();
                    btnSendCode.setEnabled(true);
                    btnSendCode.setText("重新发送");
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body() != null ? response.body().string() : "";
                Log.d(TAG, "验证码响应：" + responseBody);
                
                try {
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    String status = jsonResponse.optString("status", "");
                    String receivedCode = jsonResponse.optString("verify_code", "");
                    
                    Log.d(TAG, "状态：" + status + ", 验证码：" + receivedCode);
                    
                    if ("success".equals(status) && !receivedCode.equals("-1")) {
                        verifyCode = receivedCode;
                        runOnUiThread(() -> {
                            Toast.makeText(EmailLoginActivity.this, "验证码已发送至 " + maskEmail(email), Toast.LENGTH_SHORT).show();
                            codeContainer.setVisibility(View.VISIBLE);
                            btnSendCode.setVisibility(View.GONE);
                            startCountdown();
                            
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(EmailLoginActivity.this, "发送失败，请重试", Toast.LENGTH_SHORT).show();
                            btnSendCode.setEnabled(true);
                            btnSendCode.setText("重新发送");
                        });
                    }
                } catch (Exception e) {
                    Log.e(TAG, "解析响应失败", e);
                    runOnUiThread(() -> {
                        Toast.makeText(EmailLoginActivity.this, "发送失败，请重试", Toast.LENGTH_SHORT).show();
                        btnSendCode.setEnabled(true);
                        btnSendCode.setText("重新发送");
                    });
                }
            }
        });
    }

    private String maskEmail(String email) {
        int atIndex = email.indexOf("@");
        if (atIndex <= 1) return email;
        String prefix = email.substring(0, 2);
        String suffix = email.substring(atIndex);
        return prefix + "***" + suffix;
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

    private void login() {
        String c1 = etCode1.getText().toString();
        String c2 = etCode2.getText().toString();
        String c3 = etCode3.getText().toString();
        String c4 = etCode4.getText().toString();
        String c5 = etCode5.getText().toString();
        String c6 = etCode6.getText().toString();

        if (c1.isEmpty() || c2.isEmpty() || c3.isEmpty() || c4.isEmpty() || c5.isEmpty() || c6.isEmpty()) {
            Toast.makeText(this, "请输入完整验证码", Toast.LENGTH_SHORT).show();
            return;
        }

        String inputCode = c1 + c2 + c3 + c4 + c5 + c6;

        if (!inputCode.equals(verifyCode)) {
            Toast.makeText(this, "验证码错误", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("登录中...");

        ApiClient.getInstance().register(null, null, new ApiClient.Callback<com.ailove.app.model.AuthResult>() {
            @Override
            public void onSuccess(com.ailove.app.model.AuthResult result) {
                if (result.success) {
                    Toast.makeText(EmailLoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EmailLoginActivity.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
                } else {
                    showError("登录失败");
                }
            }

            @Override
            public void onError(String error) {
                showError(error);
            }
        });
    }

    private void showError(String msg) {
        runOnUiThread(() -> {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            btnLogin.setText("登录");
            btnLogin.setEnabled(true);
        });
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
