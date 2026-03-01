package com.ailove.app.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.ailove.app.R;
import com.ailove.app.api.ApiClient;
import com.ailove.app.ui.activity.AIChatActivity;
import com.ailove.app.ui.activity.MatchResultActivity;

public class MatchFragment extends Fragment {
    private TextView tvProgress;
    private Button btnStartAIChat;
    private Button btnFindDestiny;
    private int profileProgress = 35;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_match, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        tvProgress = view.findViewById(R.id.tv_progress);
        btnStartAIChat = view.findViewById(R.id.btn_start_ai_chat);
        btnFindDestiny = view.findViewById(R.id.btn_find_destiny);
        
        btnStartAIChat.setOnClickListener(v -> {
            startActivity(new android.content.Intent(getContext(), AIChatActivity.class));
        });
        
        btnFindDestiny.setOnClickListener(v -> {
            if (profileProgress < 70) {
                Toast.makeText(getContext(), "画像完善度达到70%后才能寻找缘分", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new android.content.Intent(getContext(), MatchResultActivity.class));
            }
        });
        
        loadProfile();
    }
    
    private void loadProfile() {
        ApiClient.getInstance().getUserProfile(new ApiClient.Callback<com.ailove.app.model.UserProfile>() {
            @Override
            public void onSuccess(com.ailove.app.model.UserProfile result) {
                profileProgress = result.profileProgress;
                tvProgress.setText("当前完善度: " + profileProgress + "%");
                
                if (profileProgress >= 70) {
                    btnFindDestiny.setEnabled(true);
                    btnFindDestiny.setAlpha(1.0f);
                } else {
                    btnFindDestiny.setEnabled(false);
                    btnFindDestiny.setAlpha(0.5f);
                }
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
