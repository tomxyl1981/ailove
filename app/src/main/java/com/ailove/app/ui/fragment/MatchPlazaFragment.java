package com.ailove.app.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.ailove.app.R;
import com.ailove.app.model.MatchSquareCombined;

public class MatchPlazaFragment extends Fragment {

    private View combinedContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_match_plaza_combined, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        combinedContainer = view.findViewById(R.id.ll_tabs);
        
        // Initialize default scores (60 for each dimension to start neutral)
        updateDefaultScores();
        
        // 八字匹配系统
        view.findViewById(R.id.btn_tab_bazi).setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new MatchPlazaCombinedFragment())
                .addToBackStack(null)
                .commit();
        });
        
        // 星座匹配系统
        view.findViewById(R.id.btn_tab_constellation).setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new MatchPlazaCombinedFragment())
                .addToBackStack(null)
                .commit();
        });
        
        // MBTI 匹配
        view.findViewById(R.id.btn_tab_mbti).setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new MatchPlazaCombinedFragment())
                .addToBackStack(null)
                .commit();
        });
        
        // 大五人格
        view.findViewById(R.id.btn_tab_bigfive).setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new MatchPlazaCombinedFragment())
                .addToBackStack(null)
                .commit();
        });
    }

    private void updateDefaultScores() {
        // Update default scores to 60 for each dimension (neutral starting point)
        if (combinedContainer != null) {
            Button btnBaZi = combinedContainer.findViewById(R.id.btn_bazi_calc);
            Button btnConstellation = combinedContainer.findViewById(R.id.btn_constellation_calc);
            Button btnMbti = combinedContainer.findViewById(R.id.btn_mbti_test);
            Button btnBigFive = combinedContainer.findViewById(R.id.btn_bigfive_calc);
            
            if (btnBaZi != null) btnBaZi.setText("计算合婚评分");
            if (btnConstellation != null) btnConstellation.setText("计算星座匹配");
            if (btnMbti != null) btnMbti.setText("计算 MBTI 匹配");
            if (btnBigFive != null) btnBigFive.setText("计算大五人格评分");
            
            // Also update the overall score to show neutral starting point
            TextView tvOverall = combinedContainer.findViewById(R.id.tv_overall_score);
            if (tvOverall != null) {
                tvOverall.setText("综合评分：60");
            }
        }
    }

    @Override
    public void onDetach() { super.onDetach(); } // 简化清理，避免内存泄漏
}
