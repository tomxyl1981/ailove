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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.ailove.app.R;
import com.ailove.app.model.MatchSquareCombined;
import com.ailove.app.storage.LocalJsonPersistence;
import com.google.gson.Gson;
import com.ailove.app.model.CertificationRecord;

public class MatchPlazaCombinedFragment extends Fragment {

    // UI 标签页和区块引用
    private LinearLayout llBaZi, llConstellation, llMbti, llBigFive, llOverall;
    
    // --- 八字匹配系统 ---
    private EditText etBaZiBirth;
    private RadioGroup rgBaZiGender;
    private Button btnBaZiCalc;
    private TextView tvBaZiScore, tvBaZiDetails;
    
    // --- 星座匹配系统 ---
    private EditText etConstellationBirth;
    private Spinner spSunSign, spMoonSign, spRisingSign;
    private Button btnConstellationCalc;
    private TextView tvConstellationScore, tvConstellationDetails;
    
    // --- MBTI 人格测试匹配 ---
    private Spinner spMbtiEI, spMbtiSN, spMbtiTF, spMbtiJP;
    private Button btnMbtiTest;
    private TextView tvMbtiScore, tvMbtiDetails;
    
    // --- 大五人格 OCEAN ---
    private SeekBar sbOpenness, sbConscientious, sbExtraversion, sbAgreeableness, sbNeuroticism;
    private Button btnBigFiveCalc;
    private TextView tvBigFiveScore, tvBigFiveDetails;
    
    // --- 综合评分显示 ---
    private TextView tvOverallScore, tvOverallDetails;
    
    // --- UI 标签页按钮 ---
    private Button btnTabBaZi, btnTabConstellation, btnTabMbti, btnTabBigFive, btnTabOverall;
    
    // --- Like/Dislike 反馈 ---
    private Button btnLike, btnDislike;
    
    // 当前激活的标签页
    private String currentTab = "overall";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_match_plaza_combined, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // 初始化 UI 引用（简化版，避免大量 findViewById）
        initUIRefs(view);
        
        // 设置标签页点击事件
        setupTabNavigation();
        
        // 设置 Like/Dislike 按钮
        setupLikeDislike(view);
        
        // 显示综合评分默认界面
        showOverallSection();
    }

    private void initUIRefs(View view) {
        llBaZi = view.findViewById(R.id.ll_bazi);
        llConstellation = view.findViewById(R.id.ll_constellation);
        llMbti = view.findViewById(R.id.ll_mbti);
        llBigFive = view.findViewById(R.id.ll_bigfive);
        llOverall = view.findViewById(R.id.ll_overall);

        etBaZiBirth = view.findViewById(R.id.et_bazi_birth);
        rgBaZiGender = view.findViewById(R.id.rg_bazi_gender);
        btnBaZiCalc = view.findViewById(R.id.btn_bazi_calc);
        tvBaZiScore = view.findViewById(R.id.tv_bazi_score);
        tvBaZiDetails = view.findViewById(R.id.tv_bazi_details);

        etConstellationBirth = view.findViewById(R.id.et_constellation_birth);
        spSunSign = view.findViewById(R.id.sp_sun_sign);
        spMoonSign = view.findViewById(R.id.sp_moon_sign);
        spRisingSign = view.findViewById(R.id.sp_rising_sign);
        btnConstellationCalc = view.findViewById(R.id.btn_constellation_calc);
        tvConstellationScore = view.findViewById(R.id.tv_constellation_score);
        tvConstellationDetails = view.findViewById(R.id.tv_constellation_details);

        spMbtiEI = view.findViewById(R.id.sp_mbti_ei);
        spMbtiSN = view.findViewById(R.id.sp_mbti_sn);
        spMbtiTF = view.findViewById(R.id.sp_mbti_tf);
        spMbtiJP = view.findViewById(R.id.sp_mbti_jp);
        btnMbtiTest = view.findViewById(R.id.btn_mbti_test);
        tvMbtiScore = view.findViewById(R.id.tv_mbti_score);
        tvMbtiDetails = view.findViewById(R.id.tv_mbti_details);

        sbOpenness = view.findViewById(R.id.sb_openness);
        sbConscientious = view.findViewById(R.id.sb_conscientious);
        sbExtraversion = view.findViewById(R.id.sb_extraversion);
        sbAgreeableness = view.findViewById(R.id.sb_agreeableness);
        sbNeuroticism = view.findViewById(R.id.sb_neuroticism);
        btnBigFiveCalc = view.findViewById(R.id.btn_bigfive_calc);
        tvBigFiveScore = view.findViewById(R.id.tv_bigfive_score);
        tvBigFiveDetails = view.findViewById(R.id.tv_bigfive_details);

        tvOverallScore = view.findViewById(R.id.tv_overall_score);
        tvOverallDetails = view.findViewById(R.id.tv_overall_details);

        btnTabBaZi = view.findViewById(R.id.btn_tab_bazi);
        btnTabConstellation = view.findViewById(R.id.btn_tab_constellation);
        btnTabMbti = view.findViewById(R.id.btn_tab_mbti);
        btnTabBigFive = view.findViewById(R.id.btn_tab_bigfive);
        btnTabOverall = view.findViewById(R.id.btn_tab_overall);

        btnLike = view.findViewById(R.id.btn_like);
        btnDislike = view.findViewById(R.id.btn_dislike);
    }

    private void setupTabNavigation() {
        btnBaZiCalc.setOnClickListener(v -> computeSection("bazi"));
        btnConstellationCalc.setOnClickListener(v -> computeSection("zodiac"));
        btnMbtiTest.setOnClickListener(v -> computeSection("mbti"));
        btnBigFiveCalc.setOnClickListener(v -> computeSection("bigfive"));

        btnTabOverall.setOnClickListener(v -> showOverallSection());
        btnTabBaZi.setOnClickListener(v -> showBaZiSection());
        btnTabConstellation.setOnClickListener(v -> showConstellationSection());
        btnTabMbti.setOnClickListener(v -> showMbtiSection());
        btnTabBigFive.setOnClickListener(v -> showBigFiveSection());
    }

    private void setupLikeDislike(View view) {
        // 初始化当前评分
        tvOverallScore.setText("综合评分：0");
        
        btnLike.setOnClickListener(v -> handleLike());
        btnDislike.setOnClickListener(v -> handleDislike());
    }

    private void showBaZiSection() {
        currentTab = "bazi";
        updateActiveTab(btnTabBaZi);
        llBaZi.setVisibility(View.VISIBLE);
        llConstellation.setVisibility(View.GONE);
        llMbti.setVisibility(View.GONE);
        llBigFive.setVisibility(View.GONE);
        llOverall.setVisibility(View.GONE);
    }

    private void showConstellationSection() {
        currentTab = "zodiac";
        updateActiveTab(btnTabConstellation);
        llBaZi.setVisibility(View.GONE);
        llConstellation.setVisibility(View.VISIBLE);
        llMbti.setVisibility(View.GONE);
        llBigFive.setVisibility(View.GONE);
        llOverall.setVisibility(View.GONE);
    }

    private void showMbtiSection() {
        currentTab = "mbti";
        updateActiveTab(btnTabMbti);
        llBaZi.setVisibility(View.GONE);
        llConstellation.setVisibility(View.GONE);
        llMbti.setVisibility(View.VISIBLE);
        llBigFive.setVisibility(View.GONE);
        llOverall.setVisibility(View.GONE);
    }

    private void showBigFiveSection() {
        currentTab = "bigfive";
        updateActiveTab(btnTabBigFive);
        llBaZi.setVisibility(View.GONE);
        llConstellation.setVisibility(View.GONE);
        llMbti.setVisibility(View.GONE);
        llBigFive.setVisibility(View.VISIBLE);
        llOverall.setVisibility(View.GONE);
    }

    private void showOverallSection() {
        currentTab = "overall";
        updateActiveTab(btnTabOverall);
        llBaZi.setVisibility(View.GONE);
        llConstellation.setVisibility(View.GONE);
        llMbti.setVisibility(View.GONE);
        llBigFive.setVisibility(View.GONE);
        llOverall.setVisibility(View.VISIBLE);
    }

    private void updateActiveTab(Button activeBtn) {
        btnTabBaZi.setEnabled(false);
        btnTabConstellation.setEnabled(false);
        btnTabMbti.setEnabled(false);
        btnTabBigFive.setEnabled(false);
        btnTabOverall.setEnabled(false);
        
        if (activeBtn != null) {
            activeBtn.setEnabled(true);
        }
    }

    private void computeSection(String sectionType) {
        MatchSquareCombined result = new MatchSquareCombined();
        
        switch (sectionType) {
            case "bazi":
                result.baziScore = computeBaZiScore();
                result.baziDetails = generateBaZiAnalysis(result.baziScore);
                tvBaZiScore.setText("合婚评分：" + result.baziScore);
                if (tvBaZiDetails != null) {
                    tvBaZiDetails.setText(result.baziDetails);
                }
                break;
            case "zodiac":
                result.zodiacScore = computeZodiacScore();
                result.sunSign = getSelectedText(spSunSign, "");
                result.moonSign = getSelectedText(spMoonSign, "");
                result.risingSign = getSelectedText(spRisingSign, "");
                result.zodiacDetails = generateZodiacAnalysis(result.zodiacScore);
                tvConstellationScore.setText("星座匹配：" + result.zodiacScore);
                if (tvConstellationDetails != null) {
                    tvConstellationDetails.setText(result.zodiacDetails);
                }
                break;
            case "mbti":
                result.mbtiScore = computeMbtiScore();
                String mbtiType = generateMBTIType();
                result.mbtiType = mbtiType;
                result.mbtiDetails = generateMBTIDetails(mbtiType);
                tvMbtiScore.setText("官配契合：" + result.mbtiScore);
                if (tvMbtiDetails != null) {
                    tvMbtiDetails.setText(result.mbtiDetails);
                }
                break;
            case "bigfive":
                int o = sbOpenness != null ? sbOpenness.getProgress() : 50;
                int c = sbConscientious != null ? sbConscientious.getProgress() : 50;
                int e = sbExtraversion != null ? sbExtraversion.getProgress() : 50;
                int a = sbAgreeableness != null ? sbAgreeableness.getProgress() : 50;
                int n = sbNeuroticism != null ? sbNeuroticism.getProgress() : 50;
                
                result.opennessScore = o;
                result.conscientiousnessScore = c;
                result.extraversionScore = e;
                result.agreeablenessScore = a;
                result.neuroticismScore = n;
                
                result.bigFiveDetails = generateBigFiveAnalysis(o, c, e, a, n);
                tvBigFiveScore.setText("大五人格评分：" + computeOverallFromBigFive(o, c, e, a, n));
                if (tvBigFiveDetails != null) {
                    tvBigFiveDetails.setText(result.bigFiveDetails);
                }
                
                // 更新综合评分（权重：20% BaZi + 15% Zodiac + 25% MBTI + 30% BigFive）
                result.overallScore = (int) Math.round((result.baziScore * 0.20) + (result.zodiacScore * 0.15) 
                                   + (result.mbtiScore * 0.25) + ((o+c+e+a+n)/5) * 0.30);
                break;
        }
        
        tvOverallScore.setText("综合评分：" + result.overallScore);
        if (tvOverallDetails != null) {
            tvOverallDetails.setText(generateCombinedAnalysis(result));
        }
        
        // Save the match result to local storage for persistence
        saveMatchResult(result);
    }

    private void saveMatchResult(MatchSquareCombined result) {
        CertificationRecord rec = new CertificationRecord();
        Gson gson = new Gson();
        rec.id = "match_square_" + System.currentTimeMillis();
        rec.category = "MATCH_SQUARE";
        rec.dataJson = gson.toJson(result);
        rec.timestamp = System.currentTimeMillis();
        rec.status = "completed";
        LocalJsonPersistence.saveCertificationRecord(getContext(), rec);
    }

    private int computeBaZiScore() {
        String birth = getSelectedText(etBaZiBirth, "");
        if (TextUtils.isEmpty(birth)) return 60; // 默认分数
        
        // 基于出生日期计算八字合婚评分（简化算法）
        int year = 0;
        try { year = Integer.parseInt(birth.substring(0,4)); } catch(Exception ignored) {}
        
        // 生肖匹配度（简化：鼠龙猴三合，虎马狗三合等）
        int zodiacIdx = Math.abs(year) % 12;
        int baseScore = 60 + (zodiacIdx * 3 % 40);
        
        // Gender matching bonus (simplified - assuming gender is already selected)
        return Math.min(baseScore, 95);
    }

    private int computeZodiacScore() {
        String birth = getSelectedText(etConstellationBirth, "");
        if (TextUtils.isEmpty(birth)) return 60;
        
        // 解析星座年份，简化计算相位契合度
        int year = 0;
        try { year = Integer.parseInt(birth.substring(0,4)); } catch(Exception ignored) {}
        
        int sunIdx = Math.abs(year) % 12; // 太阳星座索引
        
        // 月亮/上升星座随机分配（简化）
        int moonIdx = (sunIdx + 3) % 12;
        int risingIdx = (moonIdx + 4) % 12;
        
        // 相位契合度计算（简化：同元素加成，相位角度匹配）
        int compatibility = 0;
        
        // 太阳-月亮兼容性（基于元素）
        String sunElem = getSignElement(sunIdx);
        String moonElem = getSignElement(moonIdx);
        if (sunElem.equals(moonElem)) {
            compatibility += 15;
        } else if (isCompatibleElements(sunElem, moonElem)) {
            compatibility += 8;
        }
        
        // 太阳 - 上升兼容性
        String risingElem = getSignElement(risingIdx);
        if (sunElem.equals(risingElem)) {
            compatibility += 15;
        } else if (isCompatibleElements(sunElem, risingElem)) {
            compatibility += 8;
        }
        
        // 月亮 - 上升兼容性
        if (moonElem.equals(risingElem)) {
            compatibility += 15;
        } else if (isCompatibleElements(moonElem, risingElem)) {
            compatibility += 8;
        }
        
        return Math.min(compatibility + 60, 95);
    }

    private int computeMbtiScore() {
        String ei = getSelectedText(spMbtiEI, "");
        String sn = getSelectedText(spMbtiSN, "");
        String tf = getSelectedText(spMbtiTF, "");
        String jp = getSelectedText(spMbtiJP, "");
        
        if (ei == null || TextUtils.isEmpty(ei)) ei = "";
        if (sn == null || TextUtils.isEmpty(sn)) sn = "";
        if (tf == null || TextUtils.isEmpty(tf)) tf = "";
        if (jp == null || TextUtils.isEmpty(jp)) jp = "";
        
        // 生成 MBTI 类型
        String type = ei + sn + tf + jp;
        
        // 基于性格维度计算匹配度（简化：平衡型得分高）
        int score = 60;
        
        // E-I 维度平衡加成
        if (ei.length() == 1 && !"I".equals(ei)) {
            score += 5;
        } else if ("E".equals(ei) || "I".equals(ei)) {
            score += 3;
        }
        
        // N-S 维度平衡加成
        if (sn.length() == 1 && !"S".equals(sn)) {
            score += 5;
        } else if ("N".equals(sn) || "S".equals(sn)) {
            score += 3;
        }
        
        // T-F 维度平衡加成
        if (tf.length() == 1 && !"F".equals(tf)) {
            score += 5;
        } else if ("T".equals(tf) || "F".equals(tf)) {
            score += 3;
        }
        
        // J-P 维度平衡加成
        if (jp.length() == 1 && !"P".equals(jp)) {
            score += 5;
        } else if ("J".equals(jp) || "P".equals(jp)) {
            score += 3;
        }
        
        return Math.min(score, 90);
    }

    private int computeOverallFromBigFive(int o, int c, int e, int a, int n) {
        // OCEAN 综合评分（考虑平衡性）
        double avg = (o + c + e + a + n) / 5.0;
        
        // 理想范围加权
        if (c >= 60 && c <= 80) avg += 5;   // 尽责性适中加分
        if (n >= 20 && n <= 40) avg += 3;   // 神经质较低加分
        if (a >= 50 && a <= 70) avg += 4;   // 宜人性适中加分
        
        return Math.min((int)Math.round(avg), 90);
    }

    private String generateBaZiAnalysis(int score) {
        StringBuilder sb = new StringBuilder();
        
        switch (score / 20) {
            case 0: // 60-70
                sb.append("八字合婚评分：").append(score).append("\n");
                sb.append("五行属性：平衡度良好，无明显偏颇\n");
                sb.append("阴阳平衡：阴阳调和，气场和谐\n");
                sb.append("生肖夫妻宫：三合或六合格局，缘分深厚\n");
                break;
            case 1: // 70-80
                sb.append("八字合婚评分：").append(score).append("\n");
                sb.append("五行属性：相生为主，流通顺畅\n");
                sb.append("阴阳平衡：阴阳互补，相得益彰\n");
                sb.append("生肖夫妻宫：天合地合，良缘天赐\n");
                break;
            case 2: // 80-95
                sb.append("八字合婚评分：").append(score).append("\n");
                sb.append("五行属性：金木水火土俱全，格局圆满\n");
                sb.append("阴阳平衡：阴中有阳，阳中有阴，完美融合\n");
                sb.append("生肖夫妻宫：天作之合，百年好合\n");
                break;
            default:
                sb.append("八字合婚评分：").append(score).append("\n");
                sb.append("五行属性：需进一步分析\n");
                sb.append("阴阳平衡：建议咨询专业人士\n");
                sb.append("生肖夫妻宫：基础良好\n");
                break;
        }
        
        // 添加建议
        sb.append("\n【婚姻建议】\n");
        if (score >= 80) {
            sb.append("恭喜！这是一段上等姻缘，双方五行互补，性情相投。\n");
            sb.append("宜早结良缘，白头偕老。婚后宜共同修身养性，培养共同爱好。\n");
        } else if (score >= 70) {
            sb.append("中等偏上的缘分，相处融洽但需注意细节磨合。\n");
            sb.append("建议多沟通、包容对方，经营好小家庭。\n");
        } else {
            sb.append("基础缘分尚好，但需付出更多努力维系感情。\n");
            sb.append("注意避免争吵，学会换位思考。\n");
        }
        
        return sb.toString();
    }

    private String generateZodiacAnalysis(int score) {
        StringBuilder sb = new StringBuilder();
        
        // 获取太阳星座信息（简化）
        String sunName = getSignName(getSunIndex());
        if (sunName != null && !"".equals(sunName)) {
            sb.append("太阳星座：").append(sunName).append("\n");
            sb.append("核心特质：阳光、积极、热情\n");
        }
        
        // 月亮/上升星座
        String moonName = getSignName(getMoonIndex());
        if (moonName != null && !"".equals(moonName)) {
            sb.append("月亮星座：").append(moonName).append("\n");
            sb.append("情感需求：追求理解与接纳\n");
        }
        
        String risingName = getSignName(getRisingIndex());
        if (risingName != null && !"".equals(risingName)) {
            sb.append("上升星座：").append(risingName).append("\n");
            sb.append("第一印象：亲和力强，人缘好\n");
        }
        
        // 合盘分析
        sb.append("\n【合盘分析】\n");
        if (score >= 80) {
            sb.append("相位契合度极高！\n");
            sb.append("元素相生相克关系和谐，能量流动顺畅。\n");
            sb.append("互补性强，能互相激发对方的潜能。").append("\n");
        } else if (score >= 70) {
            sb.append("相位较为和谐。\n");
            sb.append("虽有差异但能相互包容理解。\n");
            sb.append("在关系中能找到平衡点。").append("\n");
        } else {
            sb.append("相位存在挑战，需要更多磨合。\n");
            sb.append("注意沟通方式，避免误解冲突。\n");
            sb.append("寻找共同话题和兴趣是维系关系的关键。").append("\n");
        }
        
        // 恋爱预测（简化）
        sb.append("\n【恋爱预测】\n");
        if (score >= 80) {
            sb.append("短期激情度：★★★★★\n");
            sb.append("中期稳定性：★★★★☆\n");
            sb.append("长期契合度趋势：上升\n");
            sb.append("这是一段有望修成正果的恋情。").append("\n");
        } else if (score >= 70) {
            sb.append("短期激情度：★★★☆☆\n");
            sb.append("中期稳定性：★★★☆☆\n");
            sb.append("长期契合度趋势：平稳\n");
            sb.append("需要用心经营才能白头偕老。").append("\n");
        } else {
            sb.append("短期激情度：★★☆☆☆\n");
            sb.append("中期稳定性：★★★☆☆\n");
            sb.append("长期契合度趋势：波动\n");
            sb.append("建议多花时间了解彼此，寻找共同点。").append("\n");
        }
        
        return sb.toString();
    }

    private String generateMBTIDetails(String type) {
        StringBuilder sb = new StringBuilder();
        
        // 16 型人格核心特质（简化版）
        if (type.equals("ISTJ")) {
            sb.append(type).append(": 调查员\n");
            sb.append("【核心特质】\n");
            sb.append("内向、实感、思考、判断\n");
            sb.append("注重事实与细节，逻辑严谨，执行力强。\n");
            sb.append("做事有条理，喜欢按计划行事。").append("\n");
        } else if (type.equals("ISFJ")) {
            sb.append(type).append(": 守卫者\n");
            sb.append("【核心特质】\n");
            sb.append("内向、实感、情感、判断\n");
            sb.append("善良体贴，善解人意，重视责任与承诺。\n");
            sb.append("是值得信赖的伙伴和朋友。").append("\n");
        } else if (type.equals("INFJ")) {
            sb.append(type).append(": 咨询师\n");
            sb.append("【核心特质】\n");
            sb.append("内向、直觉、情感、判断\n");
            sb.append("富有洞察力，追求意义与深度。\n");
            sb.append("是温暖而有智慧的灵魂。").append("\n");
        } else if (type.equals("INTJ")) {
            sb.append(type).append(": 建筑师\n");
            sb.append("【核心特质】\n");
            sb.append("内向、直觉、思考、判断\n");
            sb.append("战略思维，追求创新与卓越。\n");
            sb.append("是独立的思想家。").append("\n");
        } else if (type.equals("ISTP")) {
            sb.append(type).append(": 鉴赏家\n");
            sb.append("【核心特质】\n");
            sb.append("内向、实感、思考、感知\n");
            sb.append("动手能力强，喜欢探索与分析。\n");
            sb.append("是灵活的解决问题者。").append("\n");
        } else if (type.equals("ISFP")) {
            sb.append(type).append(": 探险家\n");
            sb.append("【核心特质】\n");
            sb.append("内向、实感、情感、感知\n");
            sb.append("敏感细腻，追求美感与和谐。\n");
            sb.append("是富有创造力的艺术家。").append("\n");
        } else if (type.equals("INFP")) {
            sb.append(type).append(": 调停者\n");
            sb.append("【核心特质】\n");
            sb.append("内向、直觉、情感、感知\n");
            sb.append("理想主义，富有同情心。\n");
            sb.append("是真诚的梦想家。").append("\n");
        } else if (type.equals("INTP")) {
            sb.append(type).append(": 逻辑学家\n");
            sb.append("【核心特质】\n");
            sb.append("内向、直觉、思考、感知\n");
            sb.append("热爱知识，追求真理。\n");
            sb.append("是深度的思想家。").append("\n");
        } else if (type.equals("ESTP")) {
            sb.append(type).append(": 企业家\n");
            sb.append("【核心特质】\n");
            sb.append("外向、实感、思考、感知\n");
            sb.append("行动力强，善于把握机会。\n");
            sb.append("是务实的实干家。").append("\n");
        } else if (type.equals("ESFP")) {
            sb.append(type).append(": 表演者\n");
            sb.append("【核心特质】\n");
            sb.append("外向、实感、情感、感知\n");
            sb.append("活泼热情，善于交际。\n");
            sb.append("是天生的社交达人。").append("\n");
        } else if (type.equals("ENFP")) {
            sb.append(type).append(": 竞选者\n");
            sb.append("【核心特质】\n");
            sb.append("外向、直觉、情感、感知\n");
            sb.append("富有感染力，充满创意。\n");
            sb.append("是灵感的源泉。").append("\n");
        } else if (type.equals("ENTP")) {
            sb.append(type).append(": 辩论家\n");
            sb.append("【核心特质】\n");
            sb.append("外向、直觉、思考、感知\n");
            sb.append("思维敏捷，善于辩论。\n");
            sb.append("是聪明的挑战者。").append("\n");
        } else if (type.equals("ESTJ")) {
            sb.append(type).append(": 总经理\n");
            sb.append("【核心特质】\n");
            sb.append("外向、实感、思考、判断\n");
            sb.append("领导力强，注重效率。\n");
            sb.append("是出色的管理者。").append("\n");
        } else if (type.equals("ESFJ")) {
            sb.append(type).append(": 执政官\n");
            sb.append("【核心特质】\n");
            sb.append("外向、实感、情感、判断\n");
            sb.append("热心助人，重视和谐。\n");
            sb.append("是贴心的照顾者。").append("\n");
        } else if (type.equals("ENFJ")) {
            sb.append(type).append(": 教育家\n");
            sb.append("【核心特质】\n");
            sb.append("外向、直觉、情感、判断\n");
            sb.append("鼓舞人心，善于引导。\n");
            sb.append("是温暖的领导者。").append("\n");
        } else if (type.equals("ENTJ")) {
            sb.append(type).append(": 指挥官\n");
            sb.append("【核心特质】\n");
            sb.append("外向、直觉、思考、判断\n");
            sb.append("目标明确，行动果断。\n");
            sb.append("是高效的决策者。").append("\n");
        } else {
            sb.append(type).append(": 类型分析中...\n");
        }
        
        // 维度解读
        sb.append("\n【性格维度】\n");
        
        if (type.startsWith("E")) {
            sb.append("外向性：能量来源于外部世界，喜欢社交互动。\n");
        } else {
            sb.append("内向性：能量来源于内心思考，偏好独处充电。\n");
        }
        
        if (type.indexOf("S") != -1 && type.length() > 2) {
            sb.append("感觉型：关注具体事实和细节\n");
        } else if (type.length() > 3 && type.charAt(3) == 'N') {
            sb.append("直觉型：关注抽象模式和可能性\n");
        }
        
        if (type.indexOf("T") != -1 && type.length() > 4) {
            sb.append("思考型：决策基于逻辑分析\n");
        } else if (type.length() > 5 && type.charAt(5) == 'F') {
            sb.append("情感型：决策基于价值判断\n");
        }
        
        if (type.indexOf("J") != -1 && type.length() > 6) {
            sb.append("判断型：偏好计划与组织\n");
        } else if (type.length() > 7 && type.charAt(7) == 'P') {
            sb.append("感知型：偏好灵活与开放\n");
        }
        
        return sb.toString();
    }

    private String generateBigFiveAnalysis(int o, int c, int e, int a, int n) {
        StringBuilder sb = new StringBuilder();
        
        // OCEAN 详细解读
        sb.append("【开放性】").append(o).append("\n");
        if (o >= 70) {
            sb.append("想象力丰富，审美敏锐，求知欲强，富有创新精神。\n");
        } else if (o <= 30) {
            sb.append("注重实际，偏好传统和熟悉的事物。\n");
        } else {
            sb.append("平衡开放与保守的态度。").append("\n");
        }
        
        sb.append("\n【尽责性】").append(c).append("\n");
        if (c >= 70) {
            sb.append("自律性强，组织有序，责任感重，目标导向。\n");
        } else if (c <= 30) {
            sb.append("随性自由，不喜欢被约束。\n");
        } else {
            sb.append("灵活而有分寸的处事风格。").append("\n");
        }
        
        sb.append("\n【外向性】").append(e).append("\n");
        if (e >= 70) {
            sb.append("社交活跃，乐观向上，精力充沛。\n");
        } else if (e <= 30) {
            sb.append("内敛安静，享受独处时光。\n");
        } else {
            sb.append("适度的社交与内省平衡。").append("\n");
        }
        
        sb.append("\n【宜人性】").append(a).append("\n");
        if (a >= 70) {
            sb.append("善于合作，信任他人，富有同情心。\n");
        } else if (a <= 30) {
            sb.append("直言不讳，注重个人边界。\n");
        } else {
            sb.append("在友好与坚持自我间找到平衡。").append("\n");
        }
        
        sb.append("\n【神经质】").append(n).append("\n");
        if (n >= 70) {
            sb.append("情绪敏感，易焦虑，对压力反应强烈。\n");
        } else if (n <= 30) {
            sb.append("情绪稳定，抗压能力强。\n");
        } else {
            sb.append("适度的情绪波动是正常现象。").append("\n");
        }
        
        // 稳定性预测
        double avg = (o + c + e + a + n) / 5.0;
        int stabilityScore = computeOverallFromBigFive(o, c, e, a, n);
        
        sb.append("\n【性格稳定性预测】\n");
        if (stabilityScore >= 75) {
            sb.append("关系稳定性预测：★★★★★\n");
            sb.append("双方性格特质互补，长期关系发展走向良好。\n");
            sb.append("在压力情境下能相互支持。").append("\n");
        } else if (stabilityScore >= 65) {
            sb.append("关系稳定性预测：★★★★☆\n");
            sb.append("有建立稳定关系的潜力，需要共同经营。\n");
            sb.append("注意在冲突时保持沟通。").append("\n");
        } else {
            sb.append("关系稳定性预测：★★★☆☆\n");
            sb.append("性格差异可能带来挑战。\n");
            sb.append("建议学习有效沟通和情绪管理技巧。").append("\n");
        }
        
        // 潜在冲突预警
        sb.append("\n【潜在冲突点】\n");
        if (Math.abs(o - c) > 30) {
            sb.append("- 开放性与尽责性差异较大，可能在计划与 spontaneity 上产生分歧\n");
        }
        if (Math.abs(e - a) > 40) {
            sb.append("- 外向性与宜人性差异明显，社交偏好不同\n");
        }
        if (n < 35 && c >= 70) {
            sb.append("- 神经质较低与高尽责性组合，可能对对方过于严格感到压力\n");
        }
        
        return sb.toString();
    }

    private String generateCombinedAnalysis(MatchSquareCombined result) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("综合评分：").append(result.overallScore).append("\n");
        
        if (result.overallScore >= 85) {
            sb.append("【深度契合】\n");
            sb.append("价值观高度一致，性格完美互补。\n");
            sb.append("这是一段难得的良缘，值得用心经营。").append("\n\n");
        } else if (result.overallScore >= 70) {
            sb.append("【良好匹配】\n");
            sb.append("性格有较好的互补性，价值观接近。\n");
            sb.append("具有发展潜力，需要持续投入维系关系。").append("\n\n");
        } else if (result.overallScore >= 60) {
            sb.append("【潜在匹配】\n");
            sb.append("基础条件匹配，但存在差异点。\n");
            sb.append("建议进一步了解沟通，寻找共同话题。").append("\n\n");
        } else {
            sb.append("【需进一步了解】\n");
            sb.append("各维度得分不一，建议全面了解双方个性。\n");
            sb.append("真诚交流是建立关系的基础。").append("\n\n");
        }
        
        // 详细解读
        sb.append("【详细解读】\n");
        if (!"".equals(result.baziDetails)) {
            sb.append(result.baziDetails).append("\n\n");
        }
        if (!"".equals(result.zodiacDetails)) {
            sb.append(result.zodiacDetails).append("\n\n");
        }
        if (!"".equals(result.mbtiDetails)) {
            sb.append(result.mbtiDetails).append("\n\n");
        }
        if (!"".equals(result.bigFiveDetails)) {
            sb.append(result.bigFiveDetails);
        }
        
        return sb.toString();
    }

    private void handleLike() {
        MatchSquareCombined result = new MatchSquareCombined();
        tvOverallScore.setText("综合评分：" + (result.overallScore + 10)); // 点赞加分
        saveMatchResult(result);
    }

    private void handleDislike() {
        MatchSquareCombined result = new MatchSquareCombined();
        tvOverallScore.setText("综合评分：" + Math.max(35, result.overallScore - 10)); // 不喜欢减分
        saveMatchResult(result);
    }

    private String getSelectedText(View view, String defaultValue) {
        if (view == null || ((TextView) view).getText() == null) return defaultValue;
        return ((TextView) view).getText().toString();
    }

    private int getSunIndex() {
        Spinner sp = spSunSign;
        if (sp != null && sp.getSelectedItemPosition() > 0) {
            return sp.getSelectedItemPosition() % 12;
        }
        return Math.abs(2024) % 12; // 默认年份
    }

    private int getMoonIndex() {
        Spinner sp = spMoonSign;
        if (sp != null && sp.getSelectedItemPosition() > 0) {
            return sp.getSelectedItemPosition() % 12;
        }
        return (getSunIndex() + 3) % 12; // 默认月亮星座（太阳后约 2-3 个星座）
    }

    private int getRisingIndex() {
        Spinner sp = spRisingSign;
        if (sp != null && sp.getSelectedItemPosition() > 0) {
            return sp.getSelectedItemPosition() % 12;
        }
        return (getMoonIndex() + 4) % 12; // 默认上升星座（月亮后约 2-3 个星座）
    }

    private String getSignName(int idx) {
        String[] signs = {"水瓶座", "双鱼座", "白羊座", "金牛座", 
                         "双子座", "巨蟹座", "狮子座", "处女座",
                         "天秤座", "天蝎座", "射手座", "摩羯座"};
        return signs[idx];
    }

    private String getSignElement(int idx) {
        // 星座元素（简化：按 3 个一组循环）
        int elemIdx = (idx / 2) % 4;
        if (elemIdx == 0) return "火";
        if (elemIdx == 1) return "土";
        if (elemIdx == 2) return "风";
        return "水";
    }

    private boolean isCompatibleElements(String e1, String e2) {
        // 元素兼容性（简化：相生相克）
        return e1.equals(e2) || 
               (e1.equals("火") && e2.equals("土")) || // 火生土
               (e2.equals("火") && e1.equals("木"));    // 木生火
    }

    private String getSelectedText(Spinner sp, String defaultValue) {
        if (sp == null || sp.getSelectedItemPosition() <= 0) return defaultValue;
        int pos = sp.getSelectedItemPosition();
    
    // 星座选择项（简化，每个元素选几个代表性星座）
    String[] signs = {"白羊座", "金牛座", "双子座", "巨蟹座", 
                      "狮子座", "处女座", "天秤座", "天蝎座",
                      "射手座", "摩羯座", "水瓶座", "双鱼座"};
    
    String result = "";
    int idx = pos - 1; // convert 0-based index to 1-based for zodiac array
    
    if (sp.getTag() != null) {
        String tag = sp.getTag().toString();
        if ("sun".equals(tag)) {
            if (idx >= 0 && idx < signs.length) {
                result = signs[idx];
            } else {
                result = "天蝎座"; // default
            }
        } else if ("moon".equals(tag)) {
            int moonIdx = (pos - 1) % 8;
            if (moonIdx == 0 || moonIdx == 2 || moonIdx == 4) {
                result = "巨蟹座";
            } else if (moonIdx == 1 || moonIdx == 3 || moonIdx == 5) {
                result = "天蝎座";
            } else if (moonIdx == 6 || moonIdx == 7) {
                result = "双鱼座";
            } else {
                result = "处女座";
            }
        } else if ("rising".equals(tag)) {
            int risingIdx = (pos - 1) % 8;
            if (risingIdx == 0 || risingIdx == 2 || risingIdx == 4) {
                result = "水瓶座";
            } else if (risingIdx == 1 || risingIdx == 3 || risingIdx == 5) {
                result = "双子座";
            } else if (risingIdx == 6 || risingIdx == 7) {
                result = "天秤座";
            } else {
                result = "射手座";
            }
        } else if ("mbti_ei".equals(tag)) {
            result = pos == 0 ? "E (外向型)" : "I (内向型)";
        } else if ("mbti_sn".equals(tag)) {
            result = pos == 0 ? "S (实感型)" : "N (直觉型)";
        } else if ("mbti_tf".equals(tag)) {
            result = pos == 0 ? "T (思考型)" : "F (情感型)";
        } else if ("mbti_jp".equals(tag)) {
            result = pos == 0 ? "J (判断型)" : "P (感知型)";
        }
    } else {
        result = defaultValue;
    }
        
        return result;
    }

    private String generateMBTIType() {
        // 生成 MBTI 类型（基于选择）
        String ei = getSelectedText(spMbtiEI, "");
        String sn = getSelectedText(spMbtiSN, "");
        String tf = getSelectedText(spMbtiTF, "");
        String jp = getSelectedText(spMbtiJP, "");
        
        if (ei == null || TextUtils.isEmpty(ei)) ei = "I";
        if (sn == null || TextUtils.isEmpty(sn)) sn = "S";
        if (tf == null || TextUtils.isEmpty(tf)) tf = "T";
        if (jp == null || TextUtils.isEmpty(jp)) jp = "J";
        
        return ei + sn + tf + jp;
    }

    @Override
    public void onDetach() { super.onDetach(); } // 简化清理，避免内存泄漏

}