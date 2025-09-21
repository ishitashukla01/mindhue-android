package com.example.moodjournal.model;

public class MoodEntry {

    public long id;       // System.currentTimeMillis()
    public String date;   // "yyyy-MM-dd" (one entry per day)
    public String mood;   // VERY_HAPPY | CALM | NEUTRAL | STRESSED | SAD
    public String note;
    public String activity;

    public float sentimentScore;       // -1.0 .. +1.0
    public String sentimentLabel;
}
