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
import com.ailove.app.model.MbtiResult;
import com.ailove.app.storage.TestResultStorage;
import java.util.HashMap;
import java.util.Map;

public class MbtiFragment extends Fragment {
    private RadioGroup rgEI, rgSN, rgTF, rgJP;
    private RadioButton rbE, rbI, rbS, rbN, rbT, rbF, rbJ, rbP;
    private Button btnCalc;
    private CardView cardResult;
    private TextView tvScore, tvDetails;

    private Map<String, Map<String, String>> mbtiTypes = new HashMap<>();

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

        initMbtiTypes();
        
        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        btnCalc.setOnClickListener(v -> calculateMbti());
    }
    
    private void initMbtiTypes() {
        mbtiTypes.put("INTJ", createMbtiType("INTJ", "建筑师", "独立的战略家", "在爱情中追求深度和智慧，不轻易敞开心扉，但一旦认定便会全心投入。", new String[]{"ENFP", "ENTP"}));
        mbtiTypes.put("INTP", createMbtiType("INTP", "逻辑学家", "创新的思考者", "在爱情中充满好奇心，喜欢探索伴侣的内心世界。", new String[]{"ENTJ", "ENFJ"}));
        mbtiTypes.put("ENTJ", createMbtiType("ENTJ", "指挥官", "果断的领导者", "在爱情中充满激情和目标感，会把经营关系当作重要项目。", new String[]{"INFP", "INTP"}));
        mbtiTypes.put("ENTP", createMbtiType("ENTP", "辩论家", "机智的创新者", "在爱情中充满活力和创意，喜欢与伴侣进行思想碰撞。", new String[]{"INFJ", "INTJ"}));
        mbtiTypes.put("INFJ", createMbtiType("INFJ", "提倡者", "温柔的理想主义者", "在爱情中追求真挚的情感连接，渴望深层共鸣。", new String[]{"ENFP", "ENTP"}));
        mbtiTypes.put("INFP", createMbtiType("INFP", "调解员", "浪漫的理想家", "真正的浪漫主义者，追求纯粹、深刻的情感体验。", new String[]{"ENTJ", "ENFJ"}));
        mbtiTypes.put("ENFJ", createMbtiType("ENFJ", "主人公", "魅力四射的领导者", "在爱情中热情、体贴，天生懂得如何照顾伴侣。", new String[]{"INFP", "INTP"}));
        mbtiTypes.put("ENFP", createMbtiType("ENFP", "竞选者", "热情的探索者", "在爱情中充满热情和创造力，喜欢探索可能性。", new String[]{"INFJ", "INTJ"}));
        mbtiTypes.put("ISTJ", createMbtiType("ISTJ", "物流师", "可靠的守护者", "在爱情中忠诚、可靠，会用实际行动表达爱意。", new String[]{"ESFP", "ESTP"}));
        mbtiTypes.put("ISFJ", createMbtiType("ISFJ", "守卫者", "温暖的保护者", "在爱情中温柔、体贴，总是把伴侣放在心上。", new String[]{"ESFP", "ESTP"}));
        mbtiTypes.put("ESTJ", createMbtiType("ESTJ", "总经理", "高效的管理者", "在爱情中认真负责，重视承诺和稳定。", new String[]{"ISFP", "ISTP"}));
        mbtiTypes.put("ESFJ", createMbtiType("ESFJ", "执政官", "热心的助人者", "在爱情中热情、体贴，天生擅长照顾他人。", new String[]{"ISFP", "ISTP"}));
        mbtiTypes.put("ISTP", createMbtiType("ISTP", "鉴赏家", "灵活的工匠", "在爱情中独立、实际，喜欢用行动表达爱意。", new String[]{"ESFJ", "ENFJ"}));
        mbtiTypes.put("ISFP", createMbtiType("ISFP", "探险家", "灵活的艺术家", "在爱情中温柔、敏感，追求真实和美好。", new String[]{"ESFJ", "ENFJ"}));
        mbtiTypes.put("ESTP", createMbtiType("ESTP", "企业家", "精明的冒险家", "在爱情中充满活力和魅力，喜欢追求新鲜感。", new String[]{"ISFJ", "INFJ"}));
        mbtiTypes.put("ESFP", createMbtiType("ESFP", "表演者", "热情的娱乐家", "在爱情中热情、开朗，喜欢让伴侣开心。", new String[]{"ISFJ", "ISTJ"}));
    }
    
    private Map<String, String> createMbtiType(String type, String title, String subtitle, String desc, String[] matches) {
        Map<String, String> mbti = new HashMap<>();
        mbti.put("title", title);
        mbti.put("subtitle", subtitle);
        mbti.put("description", desc);
        mbti.put("matches", String.join(", ", matches));
        return mbti;
    }

    private void calculateMbti() {
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
        Map<String, String> result = mbtiTypes.get(mbtiType);
        
        // Save result
        MbtiResult saveResult = new MbtiResult();
        saveResult.mbtiType = mbtiType;
        saveResult.title = result.get("title");
        saveResult.subtitle = result.get("subtitle");
        saveResult.description = result.get("description");
        saveResult.matches = result.get("matches").split(", ");
        TestResultStorage.saveMbtiResult(requireContext(), saveResult);
        
        showResult(mbtiType, result);
    }

    private void showResult(String mbtiType, Map<String, String> result) {
        cardResult.setVisibility(View.VISIBLE);
        tvScore.setText("您的MBTI: " + mbtiType + "\n" + result.get("title") + " - " + result.get("subtitle"));
        tvDetails.setText(result.get("description") + "\n\n最佳匹配类型: " + result.get("matches"));
    }
}
