package com.ailove.app.ui.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.ailove.app.R;
import com.ailove.app.ui.fragment.ChatListFragment;
import com.ailove.app.ui.fragment.MatchFragment;
import com.ailove.app.ui.fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity {
    private Fragment chatListFragment;
    private Fragment matchFragment;
    private Fragment profileFragment;
    private Fragment currentFragment;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initFragments();
        showFragment(chatListFragment);
        setupBottomNav();
    }
    
    private void initFragments() {
        chatListFragment = new ChatListFragment();
        matchFragment = new MatchFragment();
        profileFragment = new ProfileFragment();
    }
    
    private void showFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (currentFragment != null) {
            transaction.hide(currentFragment);
        }
        if (!fragment.isAdded()) {
            transaction.add(R.id.fragment_container, fragment);
        } else {
            transaction.show(fragment);
        }
        transaction.commit();
        currentFragment = fragment;
    }
    
    private void setupBottomNav() {
        findViewById(R.id.nav_chat).setOnClickListener(v -> showFragment(chatListFragment));
        findViewById(R.id.nav_match).setOnClickListener(v -> showFragment(matchFragment));
        findViewById(R.id.nav_profile).setOnClickListener(v -> showFragment(profileFragment));
    }
}
