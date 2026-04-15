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
import com.ailove.app.ui.activity.WelcomeActivity;
import com.ailove.app.ui.activity.MatchPlazaActivity;
import com.ailove.app.ui.fragment.CertificationCenterFragment;

public class ProfileFragment extends Fragment {
    private ImageView ivAvatar;
    private TextView tvNickname;
    private TextView tvInfo;
    
    private static final String PREFS_NAME = "ailove_prefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_EMAIL = "user_email";
    
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
        
        view.findViewById(R.id.tv_logout).setOnClickListener(v -> {
            SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, 0);
            prefs.edit().putBoolean(KEY_IS_LOGGED_IN, false).commit();
            
            Toast.makeText(getContext(), "已退出登录", Toast.LENGTH_SHORT).show();
            
            Intent intent = new Intent(getContext(), WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        view.findViewById(R.id.tv_match_center).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MatchPlazaActivity.class);
            startActivity(intent);
        });

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
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, 0);
        String email = prefs.getString(KEY_USER_EMAIL, "");
        
        if (!email.isEmpty()) {
            tvNickname.setText(email);
            tvInfo.setText("登录邮箱");
        } else {
            tvNickname.setText("用户");
            tvInfo.setText("未设置");
        }
    }
}
