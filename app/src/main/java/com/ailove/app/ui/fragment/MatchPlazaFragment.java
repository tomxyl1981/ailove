package com.ailove.app.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.ailove.app.R;

public class MatchPlazaFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_match_plaza, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        view.findViewById(R.id.btn_bazi).setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.plaza_container, TestWebViewFragment.newInstance("bazi", "bazi.html"))
                .addToBackStack(null)
                .commit();
        });
        
        view.findViewById(R.id.btn_constellation).setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.plaza_container, TestWebViewFragment.newInstance("constellation", "constellation.html"))
                .addToBackStack(null)
                .commit();
        });
        
        view.findViewById(R.id.btn_mbti).setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.plaza_container, TestWebViewFragment.newInstance("mbti", "mbti.html"))
                .addToBackStack(null)
                .commit();
        });
        
        view.findViewById(R.id.btn_bigfive).setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.plaza_container, TestWebViewFragment.newInstance("bigfive", "bigfive.html"))
                .addToBackStack(null)
                .commit();
        });
    }
}
