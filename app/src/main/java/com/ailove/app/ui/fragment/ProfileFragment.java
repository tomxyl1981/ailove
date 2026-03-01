package com.ailove.app.ui.fragment;

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
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {
    private ImageView ivAvatar;
    private TextView tvNickname;
    private TextView tvInfo;
    private TextView tvProgress;
    private View tvVerified;
    
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
