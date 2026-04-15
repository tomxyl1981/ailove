package com.ailove.app.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.ailove.app.R;
import com.ailove.app.utils.LocalHttpServerManager;

public class MatchPlazaFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_match_plaza, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        view.findViewById(R.id.btn_bazi).setOnClickListener(v -> openTest("bazi"));
        
        view.findViewById(R.id.btn_constellation).setOnClickListener(v -> openTest("constellation"));
        
        view.findViewById(R.id.btn_mbti).setOnClickListener(v -> openTest("mbti"));
        
        view.findViewById(R.id.btn_bigfive).setOnClickListener(v -> openTest("bigfive"));
        
        view.findViewById(R.id.btn_history).setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.plaza_container, new TestHistoryFragment())
                .addToBackStack(null)
                .commit();
        });
    }

    private void openTest(String testName) {
        if (LocalHttpServerManager.getInstance().hasTestResult(testName)) {
            requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.plaza_container, TestHistoryFragment.newInstance(testName))
                .addToBackStack(null)
                .commit();
        } else {
            requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.plaza_container, TestWebViewFragment.newInstance(testName, testName + ".html"))
                .addToBackStack(null)
                .commit();
        }
    }
}
