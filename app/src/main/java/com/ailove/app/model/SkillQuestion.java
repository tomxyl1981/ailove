package com.ailove.app.model;

import java.util.List;
public class SkillQuestion {
    public String id;
    public String text;
    // 题目类型：text（文本）、single（单选）、multi（多选）
    public String type;
    // 选项（仅对单选、多选有效）
    public List<String> options;
    // 条件跳转：如果前一个问题的答案等于 dependsOnValue，则显示此题
    public String dependsOnQuestionId;
    public String dependsOnValue;
}
