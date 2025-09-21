package com.example.moodjournal.util;

import android.graphics.Color;
public final class MoodColor {
    public static final String VERY_HAPPY = "VERY_HAPPY";
    public static final String CALM       = "CALM";
    public static final String NEUTRAL    = "NEUTRAL";
    public static final String STRESSED   = "STRESSED";
    public static final String SAD        = "SAD";

    public static int forMood(String mood) {
        switch (mood) {
            case VERY_HAPPY: return Color.parseColor("#FFD54F"); // amber 300
            case CALM:       return Color.parseColor("#64B5F6"); // blue 300
            case NEUTRAL:    return Color.parseColor("#BDBDBD"); // grey 400
            case STRESSED:   return Color.parseColor("#EF9A9A"); // red 200
            case SAD:        return Color.parseColor("#90CAF9"); // blue 200
            default:         return Color.parseColor("#BDBDBD");
        }
    }

    public static String emoji(String mood){
        switch (mood){
            case VERY_HAPPY: return "😄";
            case CALM:       return "😌";
            case NEUTRAL:    return "😐";
            case STRESSED:   return "😣";
            case SAD:        return "😔";
            default:         return "🙂";
        }
    }

    public static String pretty(String mood){
        return mood.replace('_',' ').toLowerCase(); // e.g., "very happy"
    }
}
