package com.example.moodjournal.util;

import java.text.SimpleDateFormat;
import java.util.*;
public final class DateUtils {

    private static final SimpleDateFormat ISO = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat PRETTY = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());

    public static String todayIso() {
        return ISO.format(new Date());
    }

    public static String pretty(String iso) {
        try { return PRETTY.format(ISO.parse(iso)); } catch (Exception e) { return iso; }
    }

    /** Returns 7 iso dates (Mon..Sun) for the week containing 'today'. */
    public static List<String> weekIsoDatesAroundToday() {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);

        // Move to Monday of this week
        int diff = cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
        if (diff < 0) diff += 7;
        cal.add(Calendar.DAY_OF_MONTH, -diff);

        List<String> out = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            out.add(ISO.format(cal.getTime()));
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return out;
    }
}
