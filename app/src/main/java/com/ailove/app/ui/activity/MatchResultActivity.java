package com.ailove.app.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.ailove.app.R;
import com.ailove.app.api.ApiClient;
import com.ailove.app.model.RecommendUser;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class MatchResultActivity extends AppCompatActivity {
    private ImageView ivAvatar;
    private TextView tvName;
    private TextView tvInfo;
    private TextView tvIntro;
    private TextView tvFitPoints;
    private TextView tvAttentionPoints;
    private TextView tvStatus;
    private RecommendUser currentUser;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_result);
        
        initViews();
        loadMatchData();
    }
    
    private void initViews() {
        ivAvatar = findViewById(R.id.iv_avatar);
        tvName = findViewById(R.id.tv_name);
        tvInfo = findViewById(R.id.tv_info);
        tvIntro = findViewById(R.id.tv_intro);
        tvFitPoints = findViewById(R.id.tv_fit_points);
        tvAttentionPoints = findViewById(R.id.tv_attention_points);
        tvStatus = findViewById(R.id.tv_status);
        
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_heart_like).setOnClickListener(v -> clickLike());
        findViewById(R.id.btn_not_now).setOnClickListener(v -> finish());
    }
    
    private void loadMatchData() {
        ApiClient.getInstance().getMatchRecommendation(
            ApiClient.getInstance().getCurrentUserId(),
            new ApiClient.Callback<com.ailove.app.model.MatchResult>() {
                @Override
                public void onSuccess(com.ailove.app.model.MatchResult result) {
                    currentUser = result.recommendUser;
                    displayUser(result);
                }
                
                @Override
                public void onError(String error) {
                    Toast.makeText(MatchResultActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        );
    }
    
    private void displayUser(com.ailove.app.model.MatchResult result) {
        RecommendUser user = result.recommendUser;
        
        Picasso.get().load(user.avatar).into(ivAvatar);
        tvName.setText(user.nickname + ", " + user.age);
        tvInfo.setText(user.city + " | " + user.height + "cm | " + user.education + " | " + user.income + "万/年");
        tvIntro.setText(user.introduction);
        
        StringBuilder fitBuilder = new StringBuilder();
        for (String point : result.fitPoints) {
            fitBuilder.append("• ").append(point).append("\n");
        }
        tvFitPoints.setText(fitBuilder.toString());
        
        StringBuilder attentionBuilder = new StringBuilder();
        for (String point : result.attentionPoints) {
            attentionBuilder.append("• ").append(point).append("\n");
        }
        tvAttentionPoints.setText(attentionBuilder.toString());
    }
    
    private void clickLike() {
        if (currentUser == null) return;
        
        ApiClient.getInstance().likeUser(
            ApiClient.getInstance().getCurrentUserId(),
            currentUser.userId,
            new ApiClient.Callback<com.ailove.app.model.LikeResult>() {
                @Override
                public void onSuccess(com.ailove.app.model.LikeResult result) {
                    if (result.matched) {
                        Toast.makeText(MatchResultActivity.this, "匹配成功！", Toast.LENGTH_SHORT).show();
                        tvStatus.setText("匹配成功");
                        tvStatus.setVisibility(View.VISIBLE);
                        
                        String newSessionId = "session_" + UUID.randomUUID().toString();
                        PrivateChatActivity.start(MatchResultActivity.this, newSessionId, currentUser.nickname);
                        finish();
                    } else {
                        Toast.makeText(MatchResultActivity.this, "等待对方回应", Toast.LENGTH_SHORT).show();
                        tvStatus.setText("等待对方回应");
                        tvStatus.setVisibility(View.VISIBLE);
                        findViewById(R.id.btn_heart_like).setEnabled(false);
                    }
                }
                
                @Override
                public void onError(String error) {
                    Toast.makeText(MatchResultActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        );
    }
}
