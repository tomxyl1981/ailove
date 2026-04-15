package com.ailove.app.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.ailove.app.R;
import com.ailove.app.api.ApiClient;

public class ProfileAnalysisActivity extends AppCompatActivity {
    private static final String EXTRA_USER_ID = "user_id";
    
    private TextView tvRankText;
    private TextView tvRecommendedType;
    
    public static void start(Context context) {
        Intent intent = new Intent(context, ProfileAnalysisActivity.class);
        context.startActivity(intent);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_analysis);
        
        tvRankText = findViewById(R.id.tv_rank_text);
        tvRecommendedType = findViewById(R.id.tv_recommended_type);
        
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        
        loadUserModel();
    }
    
    private void loadUserModel() {
        ApiClient.getInstance().getUserModel(
            ApiClient.getInstance().getCurrentUserId(),
            new ApiClient.Callback<com.ailove.app.model.UserModelResult>() {
                @Override
                public void onSuccess(com.ailove.app.model.UserModelResult result) {
                    displayResult(result);
                }
                
                @Override
                public void onError(String error) {
                    Toast.makeText(ProfileAnalysisActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        );
    }
    
    private void displayResult(com.ailove.app.model.UserModelResult result) {
        tvRankText.setText(result.rankText);
        tvRecommendedType.setText("推荐类型: " + result.recommendedType);
    }
}
