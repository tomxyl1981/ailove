package com.ailove.app.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.ailove.app.R;
import com.ailove.app.api.ApiClient;

public class BigFiveFragment extends Fragment {
    private SeekBar sbOpenness, sbConscientious, sbExtraversion, sbAgreeableness, sbNeuroticism;
    private Button btnCalc;
    private CardView cardResult;
    private TextView tvScore, tvDetails;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bigfive, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sbOpenness = view.findViewById(R.id.sb_openness);
        sbConscientious = view.findViewById(R.id.sb_conscientious);
        sbExtraversion = view.findViewById(R.id.sb_extraversion);
        sbAgreeableness = view.findViewById(R.id.sb_agreeableness);
        sbNeuroticism = view.findViewById(R.id.sb_neuroticism);
        btnCalc = view.findViewById(R.id.btn_bigfive_calc);
        cardResult = view.findViewById(R.id.card_result);
        tvScore = view.findViewById(R.id.tv_bigfive_score);
        tvDetails = view.findViewById(R.id.tv_bigfive_details);

        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        btnCalc.setOnClickListener(v -> {
            int openness = sbOpenness.getProgress();
            int conscientious = sbConscientious.getProgress();
            int extraversion = sbExtraversion.getProgress();
            int agreeableness = sbAgreeableness.getProgress();
            int neuroticism = sbNeuroticism.getProgress();

            String profile = openness + "," + conscientious + "," + extraversion + "," + agreeableness + "," + neuroticism;

            ApiClient.getInstance().calculateBigFive(profile, new ApiClient.Callback<String>() {
                @Override
                public void onSuccess(String result) {
                    showResult(openness, conscientious, extraversion, agreeableness, neuroticism);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void showResult(int openness, int conscientious, int extraversion, int agreeableness, int neuroticism) {
        cardResult.setVisibility(View.VISIBLE);
        tvScore.setText("大五人格分析");
        tvDetails.setText(
            "开放性: " + openness + "%\n" +
            "尽责性: " + conscientious + "%\n" +
            "外向性: " + extraversion + "%\n" +
            "宜人性: " + agreeableness + "%\n" +
            "神经质: " + neuroticism + "%\n\n" +
            "根据您的人格特征分析，您是一个性格丰富的人。\n" +
            "建议：寻找与您人格互补的伴侣");
    }
}
