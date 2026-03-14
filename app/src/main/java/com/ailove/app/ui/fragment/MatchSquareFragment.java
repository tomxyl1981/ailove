package com.ailove.app.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.ailove.app.R;
import com.ailove.app.model.MatchSquareResult;
import com.ailove.app.model.CertificationRecord;
import com.ailove.app.storage.LocalJsonPersistence;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import com.ailove.app.model.CertificationRecord;

public class MatchSquareFragment extends Fragment {
    // UI sections
    private LinearLayout llBaZi, llConstellation, llMbti, llBigFive;

    // 八字 inputs
    private EditText etBaZiBirth, etBaZiTime;
    private RadioGroup rgBaZiGender;
    // 星座 inputs
    private EditText etBirthDateConstell;
    // MBTI inputs
    private Spinner spMbtiEI, spMbtiSN, spMbtiTF, spMbtiJP;
    // 大五输入
    private SeekBar sbOpenness, sbConscientious, sbExtraversion, sbAgreeableness, sbNeuroticism;
    // display
    private TextView tvOverall, tvDetails;
    private Gson gson = new Gson();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_match_square, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // tabs
        Button btnTabBaZi = view.findViewById(R.id.btn_tab_bazi);
        Button btnTabConstellation = view.findViewById(R.id.btn_tab_constellation);
        Button btnTabMbti = view.findViewById(R.id.btn_tab_mbti);
        Button btnTabBigFive = view.findViewById(R.id.btn_tab_bigfive);
        // sections
        llBaZi = view.findViewById(R.id.ll_bazi);
        llConstellation = view.findViewById(R.id.ll_constellation);
        llMbti = view.findViewById(R.id.ll_mbti);
        llBigFive = view.findViewById(R.id.ll_bigfive);

        // inputs per section
        etBaZiBirth = view.findViewById(R.id.et_bazi_birth);
        etBaZiTime = view.findViewById(R.id.et_bazi_time);
        rgBaZiGender = view.findViewById(R.id.rg_bazi_gender);

        etBirthDateConstell = view.findViewById(R.id.et_constellation_birth);
        spMbtiEI = view.findViewById(R.id.sp_mbti_ei);
        spMbtiSN = view.findViewById(R.id.sp_mbti_sn);
        spMbtiTF = view.findViewById(R.id.sp_mbti_tf);
        spMbtiJP = view.findViewById(R.id.sp_mbti_jp);

        sbOpenness = view.findViewById(R.id.sb_openness);
        sbConscientious = view.findViewById(R.id.sb_conscientious);
        sbExtraversion = view.findViewById(R.id.sb_extraversion);
        sbAgreeableness = view.findViewById(R.id.sb_agreeableness);
        sbNeuroticism = view.findViewById(R.id.sb_neuroticism);
        tvOverall = view.findViewById(R.id.tv_overall);
        tvDetails = view.findViewById(R.id.tv_details);

        Button btnBaZiCalc = view.findViewById(R.id.btn_bazi_calc);
        Button btnConstellationCalc = view.findViewById(R.id.btn_constellation_calc);

        btnTabBaZi.setOnClickListener(v -> showSection(llBaZi));
        btnTabConstellation.setOnClickListener(v -> showSection(llConstellation));
        btnTabMbti.setOnClickListener(v -> showSection(llMbti));
        btnTabBigFive.setOnClickListener(v -> showSection(llBigFive));

        // Use MatchPlazaCombinedFragment for all sections (dynamic update support)
        btnBaZiCalc.setOnClickListener(v -> launchMatchPlazaCombined());
        btnConstellationCalc.setOnClickListener(v -> launchMatchPlazaCombined());
        
        showSection(llBaZi);
    }

    private void launchMatchPlazaCombined() {
        requireActivity().getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, new MatchPlazaCombinedFragment())
            .addToBackStack(null)
            .commit();
    }

    private void showSection(LinearLayout section) {
        llBaZi.setVisibility(View.GONE);
        llConstellation.setVisibility(View.GONE);
        llMbti.setVisibility(View.GONE);
        llBigFive.setVisibility(View.GONE);
        if (section != null) section.setVisibility(View.VISIBLE);
    }

    private int computeBaZiScore() {
        String birth = etBaZiBirth != null ? etBaZiBirth.getText().toString() : "";
        String time = etBaZiTime != null ? etBaZiTime.getText().toString() : "";
        String key = birth + "|" + time;
        int base = Math.abs(key.hashCode()) % 41;
        return 60 + base;
    }

    private int computeConstellationScore() {
        String birth = etBirthDateConstell != null ? etBirthDateConstell.getText().toString() : "";
        int year = 0;
        try { year = Integer.parseInt(birth.substring(0,4)); } catch (Exception ignored) {}
        int idx = Math.abs(year) % 12;
        return 60 + (idx * 3 % 40);
    }

    private int computeMbtiScore() {
        int total = 0, count = 0;
        if (spMbtiEI != null && spMbtiEI.getSelectedItem() != null) { total += 50; count++; }
        if (spMbtiSN != null && spMbtiSN.getSelectedItem() != null) { total += 50; count++; }
        if (spMbtiTF != null && spMbtiTF.getSelectedItem() != null) { total += 50; count++; }
        if (spMbtiJP != null && spMbtiJP.getSelectedItem() != null) { total += 50; count++; }
        if (count == 0) return 60;
        return total / count;
    }

    private int computeBigFiveScore() {
        int a = sbOpenness != null ? sbOpenness.getProgress() : 50;
        int b = sbConscientious != null ? sbConscientious.getProgress() : 50;
        int c = sbExtraversion != null ? sbExtraversion.getProgress() : 50;
        int d = sbAgreeableness != null ? sbAgreeableness.getProgress() : 50;
        int e = sbNeuroticism != null ? sbNeuroticism.getProgress() : 50;
        return (a + b + c + d + e) / 5;
    }

    private void computeAllAndPersist(View v) {
        int a = computeBaZiScore();
        int b = computeConstellationScore();
        int c = computeMbtiScore();
        int d = computeBigFiveScore();
        double other = 60.0;
        double overall = a * 0.20 + b * 0.15 + c * 0.25 + d * 0.30 + other * 0.10;
        MatchSquareResult result = new MatchSquareResult();
        result.baZiScore = a; result.zodiacScore = b; result.mbtiScore = c; result.bigFiveScore = d; result.otherScore = (int) other; result.overallScore = (int) Math.round(overall); result.timestamp = System.currentTimeMillis();
        result.dimensionScores.put("BaZi", a);
        result.dimensionScores.put("Zodiac", b);
        result.dimensionScores.put("MBTI", c);
        result.dimensionScores.put("BigFive", d);
        CertificationRecord rec = new CertificationRecord();
        rec.id = "match_square_" + result.timestamp;
        rec.category = "MATCH_SQUARE";
        rec.dataJson = gson.toJson(result);
        rec.timestamp = result.timestamp; rec.status = "completed";
        LocalJsonPersistence.saveCertificationRecord(getContext(), rec);
        if (tvOverall != null) tvOverall.setText("综合评分: " + result.overallScore);
        if (tvDetails != null) {
            tvDetails.setText("BaZi:"+a+" Zodiac:"+b+" MBTI:"+c+" BigFive:"+d);
        }
    }

    private void saveAllMatch() {
        // Trigger a recompute and persist
        computeAllAndPersist(null);
    }
}
