package com.ailove.app.model;

import java.util.HashMap;
import java.util.Map;

public class MatchSquareResult {
    public String mode;
    public int score;
    public long timestamp;
    public int baZiScore;
    public int zodiacScore;
    public int mbtiScore;
    public int bigFiveScore;
    public int otherScore;
    public int overallScore;
    public Map<String, Integer> dimensionScores = new HashMap<>();
}
