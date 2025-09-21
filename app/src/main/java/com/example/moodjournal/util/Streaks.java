package com.example.moodjournal.util;

import com.example.moodjournal.model.MoodEntry;
import java.text.SimpleDateFormat;
import java.util.*;

public final class Streaks {
    private static final SimpleDateFormat ISO = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public static class Result {
        public final int dailyStreak;         // consecutive days with any entry ending today
        public final int positiveStreak;      // consecutive POSITIVE days ending today
        public Result(int d, int p){ dailyStreak=d; positiveStreak=p; }
    }

    public static Result compute(List<MoodEntry> entries){
        if (entries == null || entries.isEmpty()) return new Result(0, 0);

        // Put by date for O(1) lookup
        Map<String, MoodEntry> byDate = new HashMap<>();
        for (MoodEntry e : entries) if (e.date != null) byDate.put(e.date, e);

        Calendar cur = Calendar.getInstance();            // today
        int daily = 0, pos = 0;

        while (true){
            String d = ISO.format(cur.getTime());
            MoodEntry e = byDate.get(d);
            if (e == null) break;                         // streak broken
            daily++;
            // positive if label says POSITIVE or mood is VERY_HAPPY/CALM
            boolean isPos = "POSITIVE".equals(e.sentimentLabel)
                    || MoodColor.VERY_HAPPY.equals(e.mood)
                    || MoodColor.CALM.equals(e.mood);
            if (isPos) pos++; else pos = 0;               // positive streak must be consecutive
            cur.add(Calendar.DAY_OF_MONTH, -1);
        }
        return new Result(daily, pos);
    }

    // Simple badge text
    public static List<String> badges(Result r){
        List<String> b = new ArrayList<>();
        if (r.dailyStreak >= 3)  b.add("🔥 3-day streak");
        if (r.dailyStreak >= 7)  b.add("🏅 7-day streak");
        if (r.dailyStreak >= 14) b.add("🏆 14-day streak");
        if (r.positiveStreak >= 3) b.add("🌟 3 positive days");
        if (r.positiveStreak >= 5) b.add("💚 5 positive days");
        return b;
    }
}
