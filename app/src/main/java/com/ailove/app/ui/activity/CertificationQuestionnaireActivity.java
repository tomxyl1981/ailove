package com.ailove.app.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ailove.app.R;
import com.ailove.app.model.SkillQuestion;
import com.ailove.app.model.CertificationRecord;
import com.ailove.app.storage.LocalJsonPersistence;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CertificationQuestionnaireActivity extends AppCompatActivity {
    private List<SkillQuestion> questions = new ArrayList<>();
    private int index = 0;
    private Map<String, String> answers = new HashMap<>();
    private TextView tvQuestion;
    private EditText etAnswer;
    private LinearLayout layoutInput;
    private com.google.gson.Gson gson = new com.google.gson.Gson();
    private Button btnNext;
    private RadioGroup dynamicRadioGroup;
    private LinearLayout dynamicMultiLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certification_questionnaire);
        tvQuestion = findViewById(R.id.tv_question);
        layoutInput = findViewById(R.id.layout_input);
        btnNext = findViewById(R.id.btn_next);
        loadQuestionsFromRaw();
        showQuestion(index);
        loadPartialIfExists();
        btnNext.setOnClickListener(v -> {
            String ans = etAnswer.getText().toString();
            answers.put(questions.get(index).id, ans);
            // Persist partial questionnaire progress
            CertificationRecord rec = new CertificationRecord();
            rec.id = "questionnaire_partial_" + System.currentTimeMillis();
            rec.category = "QUESTIONNAIRE_PARTIAL";
            rec.timestamp = System.currentTimeMillis();
            Gson gson = new Gson();
            rec.dataJson = gson.toJson(answers);
            rec.status = "in_progress";
            LocalJsonPersistence.saveCertificationRecord(this, rec);
            index++;
            if (index < questions.size()) {
                showQuestion(index);
                etAnswer.setText(answers.get(questions.get(index).id) != null ? answers.get(questions.get(index).id) : "");
            } else {
                // 保存到本地 JSON
                CertificationRecord newRec = new CertificationRecord();
                newRec.id = "questionnaire_" + System.currentTimeMillis();
                newRec.category = "QUESTIONNAIRE";
                newRec.timestamp = System.currentTimeMillis();
                Gson newGson = new Gson();
                newRec.dataJson = gson.toJson(answers);
                newRec.status = "completed";
                LocalJsonPersistence.saveCertificationRecord(this, newRec);
                Toast.makeText(this, "问卷已保存", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void loadQuestionsFromRaw() {
        // 简化实现：直接使用一个默认的文本题，未来可扩展为动态加载题库
        questions = new ArrayList<>();
        SkillQuestion q1 = new SkillQuestion();
        q1.id = "q1"; q1.text = "请简单自我介绍，你愿意公开的信息有哪些？"; q1.type = "text";
        q1.options = null;
        questions.add(q1);
    }

    private void loadPartialIfExists() {
        // 尝试恢复未完成的问卷答案
        java.util.List<com.ailove.app.model.CertificationRecord> records = LocalJsonPersistence.loadCertificationRecords(this);
        com.google.gson.Gson gson = new Gson();
        for (com.ailove.app.model.CertificationRecord rec : records) {
            if ("QUESTIONNAIRE_PARTIAL".equals(rec.category)) {
                try {
                    java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<java.util.HashMap<String, String>>(){}.getType();
                    answers = gson.fromJson(rec.dataJson, type);
                    // 设置当前题序号为下一个未填项
                    for (int i = 0; i < questions.size(); i++) {
                        if (!answers.containsKey(questions.get(i).id)) {
                            index = i;
                            break;
                        }
                        index = questions.size();
                    }
                    if (index < questions.size()) {
                        showQuestion(index);
                        String prev = answers.get(questions.get(index).id);
                        if (prev != null) etAnswer.setText(prev);
                    }
                    break;
                } catch (Exception ignored) {}
            }
        }
    }

    private void showQuestion(int idx) {
        SkillQuestion q = questions.get(idx);
        tvQuestion.setText(q.text);
        renderQuestion(q);
        String existing = answers.get(q.id);
        if (q.type.equals("text")) {
            if (etAnswer != null) etAnswer.setText(existing != null ? existing : "");
        } else {
            // For single/multi, existing answers are loaded via renderQuestion
        }
    }

    private void renderQuestion(SkillQuestion q) {
        layoutInput.removeAllViews();
        if (q.type == null) {
            // fallback to simple text input
            EditText et = new EditText(this);
            et.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            et.setHint("请输入答案");
            layoutInput.addView(et);
            etAnswer = et;
            return;
        }
        if ("text".equals(q.type)) {
            etAnswer = new EditText(this);
            etAnswer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            etAnswer.setHint("请输入答案");
            layoutInput.addView(etAnswer);
        } else if ("single".equals(q.type) && q.options != null) {
            dynamicRadioGroup = new RadioGroup(this);
            for (String opt : q.options) {
                RadioButton rb = new RadioButton(this);
                rb.setText(opt);
                dynamicRadioGroup.addView(rb);
            }
            layoutInput.addView(dynamicRadioGroup);
            dynamicRadioGroup.setOnCheckedChangeListener((grp, id) -> {
                RadioButton rb = grp.findViewById(id);
                if (rb != null) {
                    answers.put(q.id, rb.getText().toString());
                }
            });
        } else if ("multi".equals(q.type) && q.options != null) {
            dynamicMultiLayout = new LinearLayout(this);
            dynamicMultiLayout.setOrientation(LinearLayout.VERTICAL);
            for (String opt : q.options) {
                CheckBox cb = new CheckBox(this);
                cb.setText(opt);
                cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    String current = answers.get(q.id);
                    StringBuilder sb = new StringBuilder(current == null ? "" : current);
                    // rebuild from existing props
                    if (isChecked) {
                        if (sb.length() > 0 && sb.charAt(sb.length()-1) != ',') sb.append(",");
                        sb.append(opt);
                    } else {
                        String s = sb.toString();
                        List<String> parts = new ArrayList<>();
                        for (String p : s.split(",")) {
                            if (!p.equals(opt) && p.length() > 0) parts.add(p);
                        }
                        sb = new StringBuilder();
                        for (int i=0;i<parts.size();i++) {
                            if (i>0) sb.append(",");
                            sb.append(parts.get(i));
                        }
                    }
                    answers.put(q.id, sb.toString());
                });
                dynamicMultiLayout.addView(cb);
            }
            layoutInput.addView(dynamicMultiLayout);
        }
    }

    private String readAnswerFromInput(SkillQuestion q) {
        if ("text".equals(q.type)) {
            return etAnswer != null ? etAnswer.getText().toString() : "";
        } else if ("single".equals(q.type) && dynamicRadioGroup != null) {
            int checkedId = dynamicRadioGroup.getCheckedRadioButtonId();
            RadioButton rb = dynamicRadioGroup.findViewById(checkedId);
            return rb != null ? rb.getText().toString() : "";
        } else if ("multi".equals(q.type) && dynamicMultiLayout != null) {
            return answers.get(q.id) != null ? answers.get(q.id) : "";
        }
        return "";
    }
}
