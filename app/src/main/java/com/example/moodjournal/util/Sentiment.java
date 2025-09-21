package com.example.moodjournal.util;

import java.util.*;
import java.util.regex.Pattern;

public final class Sentiment {

    private static final Set<String> POS = set(
            "good","great","awesome","amazing","happy","love","like","joy","fun",
            "excellent","nice","cool","best","beautiful","relaxed","calm","peace",
            "win","progress","improved","enjoy","fantastic","wonderful","excited",
            "smile","smiling","thanks","grateful","productive","proud","motivated"
    );
    private static final Set<String> NEG = set(
            "bad","terrible","awful","sad","angry","hate","dislike","stress",
            "stressed","anxious","anxiety","tired","exhausted","worried","worry",
            "pain","sick","ill","down","cry","crying","bored","failed","failure",
            "worst","broken","late","frustrated","annoyed","upset","lonely"
    );

    // Intensifiers & dampeners
    private static final Set<String> BOOST = set("very","really","so","super","extremely","too");
    private static final Set<String> DAMP  = set("slightly","somewhat","kinda","a_little");

    // Simple negation flip for next 1-2 tokens
    private static final Set<String> NEGATORS = set("not","no","never","hardly","barely","don't","didn't","isn't","can't","won't");

    // Emoji hints
    private static final String[] POS_EMOJI = {"😊","😀","😄","🥳","👍","❤️","✨","😌"};
    private static final String[] NEG_EMOJI = {"😞","😔","😢","😠","💔","👎","😫","😭"};
    private static final Pattern SPLIT = Pattern.compile("[^\\p{L}\\p{N}]+"); // words/nums

    public static class Result {
        public final float score;        // -1..+1
        public final String label;       // POSITIVE/NEUTRAL/NEGATIVE
        public Result(float s, String l){ score=s; label=l; }
    }

    public static Result analyze(String text) {
        if (text == null || text.trim().isEmpty())
            return new Result(0f, "NEUTRAL");

        String t = normalize(text);

        // Emoji bonus
        float emoji = 0f;
        for (String e : POS_EMOJI) if (t.contains(e)) emoji += 0.5f;
        for (String e : NEG_EMOJI) if (t.contains(e)) emoji -= 0.5f;

        String[] toks = SPLIT.split(t.toLowerCase(Locale.getDefault()).replace("a little","a_little"));
        float score = 0f;
        boolean flipNext = false;
        float boost = 1.0f;

        for (int i = 0; i < toks.length; i++) {
            String w = toks[i];
            if (w.isEmpty()) continue;

            if (NEGATORS.contains(w)) { flipNext = true; continue; }
            if (BOOST.contains(w))   { boost = Math.min(1.6f, boost + 0.3f); continue; }
            if (DAMP.contains(w))    { boost = Math.max(0.6f, boost - 0.2f); continue; }

            float delta = 0f;
            if (POS.contains(w)) delta = +1f;
            if (NEG.contains(w)) delta = -1f;

            if (delta != 0f) {
                if (flipNext) { delta = -delta; flipNext = false; }
                score += delta * boost;
                boost = 1.0f; // reset after a hit
            }
        }

        score += emoji;

        // squash to -1..+1
        float norm = clamp(score / 5f, -1f, 1f);
        String label = (norm > 0.15f) ? "POSITIVE" : (norm < -0.15f ? "NEGATIVE" : "NEUTRAL");
        return new Result(norm, label);
    }

    private static float clamp(float v, float lo, float hi){ return Math.max(lo, Math.min(hi, v)); }

    private static String normalize(String s){
        // lowercasing handled above; keep as hook for future (e.g., remove urls)
        return s;
    }

    private static Set<String> set(String... xs){
        return new HashSet<>(Arrays.asList(xs));
    }
}
