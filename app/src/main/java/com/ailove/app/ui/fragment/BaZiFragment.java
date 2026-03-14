package com.ailove.app.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.ailove.app.R;
import com.ailove.app.api.ApiClient;

public class BaZiFragment extends Fragment {
    private EditText etBirth;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;
    private Button btnCalc;
    private CardView cardResult;
    private TextView tvScore, tvDetails;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bazi, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etBirth = view.findViewById(R.id.et_bazi_birth);
        rgGender = view.findViewById(R.id.rg_bazi_gender);
        rbMale = view.findViewById(R.id.rb_bazi_m);
        rbFemale = view.findViewById(R.id.rb_bazi_f);
        btnCalc = view.findViewById(R.id.btn_bazi_calc);
        cardResult = view.findViewById(R.id.card_result);
        tvScore = view.findViewById(R.id.tv_bazi_score);
        tvDetails = view.findViewById(R.id.tv_bazi_details);

        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        btnCalc.setOnClickListener(v -> {
            String birth = etBirth.getText().toString().trim();
            if (birth.isEmpty()) {
                Toast.makeText(getContext(), "请输入出生日期", Toast.LENGTH_SHORT).show();
                return;
            }

            String gender = rbMale.isChecked() ? "男" : "女";
            if (!rbMale.isChecked() && !rbFemale.isChecked()) {
                Toast.makeText(getContext(), "请选择性别", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiClient.getInstance().calculateBazi(birth, gender, new ApiClient.Callback<String>() {
                @Override
                public void onSuccess(String result) {
                    showResult(result);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void showResult(String result) {
        cardResult.setVisibility(View.VISIBLE);
        tvScore.setText("匹配度: 85%");
        tvDetails.setText("根据八字分析，你们的五行互补，相处融洽。\n\n" +
            "优点：性格互补，互相促进\n" +
            "建议：多沟通，理解彼此差异");
    }
}
