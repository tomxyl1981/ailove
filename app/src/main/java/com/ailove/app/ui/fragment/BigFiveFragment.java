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
import com.ailove.app.model.BigFiveResult;
import com.ailove.app.storage.TestResultStorage;

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

            // Generate summary
            String summary = "";
            if (openness > 70) {
                summary += "您是一个富有创造力和好奇心的人，喜欢探索新事物。";
            } else if (openness < 40) {
                summary += "您更倾向于务实和传统，注重实际经验。";
            }
            
            if (conscientious > 70) {
                summary += "做事有条理、自律性强，能够可靠地完成任务。";
            } else if (conscientious < 40) {
                summary += "您更加灵活随性，有时可能需要提高计划性。";
            }
            
            if (extraversion > 70) {
                summary += "外向开朗，善于社交，在人群中充满活力。";
            } else if (extraversion < 40) {
                summary += "您更享受独处时光，内心世界丰富。";
            }
            
            if (agreeableness > 70) {
                summary += "富有同理心，善解人意，是很好的倾听者。";
            } else if (agreeableness < 40) {
                summary += "您更加独立自主，有明确的个人立场。";
            }
            
            if (neuroticism > 70) {
                summary += "情绪稳定，能够很好地应对压力和挑战。";
            } else if (neuroticism < 40) {
                summary += "情感丰富细腻，建议多关注情绪管理。";
            }
            
            // Match suggestion
            String[] traits = {"开放心态", "计划性", "社交活力", "同理心", "情绪稳定"};
            int[] scores = {openness, conscientious, extraversion, agreeableness, neuroticism};
            int highest = 0;
            for (int i = 1; i < 5; i++) {
                if (scores[i] > scores[highest]) highest = i;
            }
            String matchSuggestion = "根据您的人格特质，建议寻找" + traits[highest] + "的伴侣。";
            
            // Save result
            BigFiveResult result = new BigFiveResult();
            result.openness = openness;
            result.conscientiousness = conscientious;
            result.extraversion = extraversion;
            result.agreeableness = agreeableness;
            result.neuroticism = neuroticism;
            result.summary = summary;
            result.matchSuggestion = matchSuggestion;
            
            TestResultStorage.saveBigFiveResult(requireContext(), result);
            
            showResult(openness, conscientious, extraversion, agreeableness, neuroticism, summary, matchSuggestion);
        });
    }

    private void showResult(int openness, int conscientious, int extraversion, int agreeableness, int neuroticism, String summary, String matchSuggestion) {
        cardResult.setVisibility(View.VISIBLE);
        tvScore.setText("大五人格分析");
        tvDetails.setText(
            "开放性: " + openness + "%\n" +
            "尽责性: " + conscientious + "%\n" +
            "外向性: " + extraversion + "%\n" +
            "宜人性: " + agreeableness + "%\n" +
            "神经质: " + neuroticism + "%\n\n" +
            summary + "\n\n" + matchSuggestion);
    }
}
