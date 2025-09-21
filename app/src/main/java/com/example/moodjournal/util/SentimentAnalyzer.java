package com.example.moodjournal.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SentimentAnalyzer {

    private static final Set<String> positiveWords = new HashSet<>(Arrays.asList(
            "happy", "great", "good", "love", "amazing", "excellent", "fun", "smile", "excited", "joy"
    ));

    private static final Set<String> negativeWords = new HashSet<>(Arrays.asList(
            "sad", "bad", "tired", "angry", "hate", "terrible", "awful", "stress", "depressed", "cry"
    ));

    // Returns score between -1.0 (very negative) to +1.0 (very positive)
    public float analyze(String text) {
        if (text == null || text.isEmpty()) return 0f;

        String lower = text.toLowerCase();
        int pos = 0, neg = 0;

        for (String w : positiveWords) {
            if (lower.contains(w)) pos++;
        }
        for (String w : negativeWords) {
            if (lower.contains(w)) neg++;
        }

        int total = pos + neg;
        if (total == 0) return 0f;

        return (float)(pos - neg) / total; // -1 → 1
    }

    // Returns label
    public String label(float score) {
        if (score > 0.2f) return "POSITIVE";
        if (score < -0.2f) return "NEGATIVE";
        return "NEUTRAL";
    }
}
