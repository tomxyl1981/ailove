package com.ailove.app.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class MbtiFragment extends Fragment {
    private RadioGroup rgEI, rgSN, rgTF, rgJP;
    private RadioButton rbE, rbI, rbS, rbN, rbT, rbF, rbJ, rbP;
    private Button btnCalc;
    private CardView cardResult;
    private TextView tvScore, tvDetails;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mbti, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rgEI = view.findViewById(R.id.sp_mbti_ei);
        rgSN = view.findViewById(R.id.sp_mbti_sn);
        rgTF = view.findViewById(R.id.sp_mbti_tf);
        rgJP = view.findViewById(R.id.sp_mbti_jp);
        rbE = view.findViewById(R.id.rb_e);
        rbI = view.findViewById(R.id.rb_i);
        rbS = view.findViewById(R.id.rb_s);
        rbN = view.findViewById(R.id.rb_n);
        rbT = view.findViewById(R.id.rb_t);
        rbF = view.findViewById(R.id.rb_f);
        rbJ = view.findViewById(R.id.rb_j);
        rbP = view.findViewById(R.id.rb_p);
        btnCalc = view.findViewById(R.id.btn_mbti_test);
        cardResult = view.findViewById(R.id.card_result);
        tvScore = view.findViewById(R.id.tv_mbti_score);
        tvDetails = view.findViewById(R.id.tv_mbti_details);

        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        btnCalc.setOnClickListener(v -> {
            if (!rbE.isChecked() && !rbI.isChecked()) {
                Toast.makeText(getContext(), "请选择第一个问题", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!rbS.isChecked() && !rbN.isChecked()) {
                Toast.makeText(getContext(), "请选择第二个问题", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!rbT.isChecked() && !rbF.isChecked()) {
                Toast.makeText(getContext(), "请选择第三个问题", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!rbJ.isChecked() && !rbP.isChecked()) {
                Toast.makeText(getContext(), "请选择第四个问题", Toast.LENGTH_SHORT).show();
                return;
            }

            StringBuilder mbti = new StringBuilder();
            mbti.append(rbE.isChecked() ? "E" : "I");
            mbti.append(rbS.isChecked() ? "S" : "N");
            mbti.append(rbT.isChecked() ? "T" : "F");
            mbti.append(rbJ.isChecked() ? "J" : "P");

            String mbtiType = mbti.toString();

            ApiClient.getInstance().calculateMbti(mbtiType, new ApiClient.Callback<String>() {
                @Override
                public void onSuccess(String result) {
                    showResult(mbtiType);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void showResult(String mbtiType) {
        cardResult.setVisibility(View.VISIBLE);
        tvScore.setText("您的MBTI: " + mbtiType);
        tvDetails.setText("MBTI性格分析\n\n" +
            "根据您的选择，您是一个具有独特性格特点的人。\n" +
            "建议：了解自己的优势，选择合适的伴侣");
    }
}
