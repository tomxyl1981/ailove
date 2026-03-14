package com.ailove.app.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.ailove.app.R;
import com.ailove.app.api.ApiClient;
import com.ailove.app.model.UserProfile;
import com.ailove.app.ui.activity.WelcomeActivity;
import com.ailove.app.ui.activity.MatchPlazaActivity;
import com.ailove.app.ui.fragment.CertificationCenterFragment;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {
    private ImageView ivAvatar;
    private TextView tvNickname;
    private TextView tvInfo;
    private TextView tvProgress;
    private View tvVerified;
    
    private static final String PREFS_NAME = "ailove_prefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        ivAvatar = view.findViewById(R.id.iv_avatar);
        tvNickname = view.findViewById(R.id.tv_nickname);
        tvInfo = view.findViewById(R.id.tv_info);
        tvProgress = view.findViewById(R.id.tv_progress);
        tvVerified = view.findViewById(R.id.tv_verified);
        
        view.findViewById(R.id.tv_logout).setOnClickListener(v -> {
            SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, 0);
            prefs.edit().putBoolean(KEY_IS_LOGGED_IN, false).commit();
            
            Toast.makeText(getContext(), "已退出登录", Toast.LENGTH_SHORT).show();
            
            Intent intent = new Intent(getContext(), WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        // 进入匹配中心入口
        view.findViewById(R.id.tv_match_center).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MatchPlazaActivity.class);
            startActivity(intent);
        });

        // 进入认证中心入口
        view.findViewById(R.id.tv_verify_center).setOnClickListener(v -> {
            CertificationCenterFragment fragment = new CertificationCenterFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        
        loadProfile();
    }
    
    private void loadProfile() {
        ApiClient.getInstance().getUserProfile(new ApiClient.Callback<UserProfile>() {
            @Override
            public void onSuccess(UserProfile result) {
                displayProfile(result);
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void displayProfile(UserProfile profile) {
        Picasso.get().load(profile.avatar).into(ivAvatar);
        tvNickname.setText(profile.nickname);
        tvInfo.setText(profile.city + " | " + profile.age + "岁 | " + profile.education);
        tvProgress.setText("画像完善度: " + profile.profileProgress + "%");
        tvVerified.setVisibility(profile.verified ? View.VISIBLE : View.GONE);
    }
}
