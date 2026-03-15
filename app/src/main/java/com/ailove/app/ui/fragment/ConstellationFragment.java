package com.ailove.app.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.ailove.app.R;
import com.ailove.app.model.ConstellationResult;
import com.ailove.app.storage.TestResultStorage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ConstellationFragment extends Fragment {
    private Spinner spSunSign;
    private Button btnCalc;
    private CardView cardResult;
    private TextView tvScore, tvDetails;

    private String[] constellations = {
        "白羊座", "金牛座", "双子座", "巨蟹座", 
        "狮子座", "处女座", "天秤座", "天蝎座", 
        "射手座", "摩羯座", "水瓶座", "双鱼座"
    };

    private Map<String, String> zodiacTraits = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_constellation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spSunSign = view.findViewById(R.id.sp_sun_sign);
        btnCalc = view.findViewById(R.id.btn_constellation_calc);
        cardResult = view.findViewById(R.id.card_result);
        tvScore = view.findViewById(R.id.tv_constellation_score);
        tvDetails = view.findViewById(R.id.tv_constellation_details);

        initZodiacTraits();
        
        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        spSunSign.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnCalc.setOnClickListener(v -> {
            int position = spSunSign.getSelectedItemPosition();
            String selectedConstellation = constellations[position];
            
            // Calculate match score
            Random random = new Random();
            int score = random.nextInt(30) + 65; // 65-94
            
            // Get zodiac trait
            String trait = zodiacTraits.get(selectedConstellation);
            
            // Save result
            ConstellationResult result = new ConstellationResult();
            result.selfZodiac = selectedConstellation;
            result.matchScore = score;
            result.zodiacTrait = trait;
            result.matchTitle = getMatchTitle(score);
            TestResultStorage.saveConstellationResult(requireContext(), result);
            
            showResult(selectedConstellation, score, trait);
        });
    }
    
    private void initZodiacTraits() {
        zodiacTraits.put("白羊座", "热情直接、勇于表达、充满活力，在感情中主动积极。");
        zodiacTraits.put("金牛座", "稳重踏实、忠诚可靠、重视承诺，追求稳定长久的关系。");
        zodiacTraits.put("双子座", "聪明机智、善于沟通、充满好奇，需要精神层面的交流。");
        zodiacTraits.put("巨蟹座", "温柔体贴、重视家庭、情感细腻，渴望安全感和归属感。");
        zodiacTraits.put("狮子座", "自信大方、热情慷慨、忠诚专一，喜欢被关注和欣赏。");
        zodiacTraits.put("处女座", "细心周到、追求完美、责任心强，注重生活品质和细节。");
        zodiacTraits.put("天秤座", "优雅和谐、追求平衡、善于社交，重视关系的公平与美好。");
        zodiacTraits.put("天蝎座", "深情专一、洞察力强、意志坚定，追求深度情感连接。");
        zodiacTraits.put("射手座", "乐观开朗、热爱自由、追求真理，需要空间和共同成长。");
        zodiacTraits.put("摩羯座", "成熟稳重、有责任感、目标明确，重视事业与家庭的平衡。");
        zodiacTraits.put("水瓶座", "独立创新、思想前卫、重视友情，追求精神层面的契合。");
        zodiacTraits.put("双鱼座", "浪漫温柔、富有同理心、善解人意，渴望童话般的爱情。");
    }
    
    private String getMatchTitle(int score) {
        if (score >= 90) return "天作之合";
        if (score >= 80) return "灵魂伴侣";
        if (score >= 70) return "良缘佳配";
        return "缘分尚可";
    }

    private void showResult(String constellation, int score, String trait) {
        cardResult.setVisibility(View.VISIBLE);
        tvScore.setText("匹配度: " + score + "%");
        tvDetails.setText("星座分析：" + constellation + "\n\n" + trait + "\n\n建议：尊重彼此的个性特点，相互包容");
    }
}
