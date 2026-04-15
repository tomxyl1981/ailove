package com.ailove.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.ailove.app.R;

public class PrivacyPolicyActivity extends AppCompatActivity {
    private ScrollView scrollView;
    private CheckBox cbAgree;
    private Button btnConfirm;
    private boolean hasScrolledToBottom = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        
        initViews();
        setupScrollListener();
    }
    
    private void initViews() {
        scrollView = findViewById(R.id.scroll_view);
        cbAgree = findViewById(R.id.cb_agree);
        btnConfirm = findViewById(R.id.btn_confirm);
        
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        
        btnConfirm.setOnClickListener(v -> {
            if (!cbAgree.isChecked()) {
                Toast.makeText(this, "请先阅读并勾选同意隐私政策", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // 返回结果给上一个Activity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("agreed", true);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
    
    private void setupScrollListener() {
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            View view = scrollView.getChildAt(scrollView.getChildCount() - 1);
            if (view != null) {
                int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
                if (diff <= 0) {
                    // 滚动到底部
                    hasScrolledToBottom = true;
                    cbAgree.setEnabled(true);
                }
            }
        });
        
        // 初始状态下禁用复选框
        cbAgree.setEnabled(false);
    }
}