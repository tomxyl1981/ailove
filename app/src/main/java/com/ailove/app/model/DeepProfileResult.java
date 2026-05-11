package com.ailove.app.model;

import java.util.HashMap;
import java.util.Map;

public class DeepProfileResult {
    public String id;
    public long timestamp;
    public String field;
    public Map<String, String> answers = new HashMap<>();

    public DeepProfileResult() {
        this.timestamp = System.currentTimeMillis();
        this.id = "deep_" + timestamp;
    }
}
