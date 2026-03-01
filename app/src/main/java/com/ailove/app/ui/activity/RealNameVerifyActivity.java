package com.ailove.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.ailove.app.R;
import com.ailove.app.api.ApiClient;

public class RealNameVerifyActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_name_verify);
        
        findViewById(R.id.btn_verify).setOnClickListener(v -> performVerify());
    }
    
    private void performVerify() {
        Toast.makeText(this, "正在进行实名认证...", Toast.LENGTH_SHORT).show();
        
        ApiClient.getInstance().realNameVerify("110101199001011234", "张三", "mock_face_image", 
            new ApiClient.Callback<com.ailove.app.model.VerifyResult>() {
                @Override
                public void onSuccess(com.ailove.app.model.VerifyResult result) {
                    if (result.success) {
                        showSolemnDialog();
                    }
                }
                
                @Override
                public void onError(String error) {
                    Toast.makeText(RealNameVerifyActivity.this, "认证失败: " + error, Toast.LENGTH_SHORT).show();
                }
            });
    }
    
    private void showSolemnDialog() {
        Intent intent = new Intent(RealNameVerifyActivity.this, SolemnVerifyActivity.class);
        startActivity(intent);
        finish();
    }
}
