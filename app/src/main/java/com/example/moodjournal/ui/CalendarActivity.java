package com.example.moodjournal.ui;

import com.example.moodjournal.R;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moodjournal.data.PrefStore;
import com.example.moodjournal.model.MoodEntry;
import com.example.moodjournal.ui.calendar.EventDecorator;
import com.example.moodjournal.util.MoodColor;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CalendarActivity extends AppCompatActivity {

    private final SimpleDateFormat ISO = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        MaterialCalendarView cal = findViewById(R.id.calendarView);
        if (cal == null) {
            Toast.makeText(this, "Calendar view not found in layout", Toast.LENGTH_LONG).show();
            finish(); return;
        }

        // 1) Smoke test: mark today so we know the control works
        cal.addDecorator(new EventDecorator(0xFF607D8B, Collections.singletonList(CalendarDay.today())));

        // 2) Load entries OFF the UI thread, then decorate
        new Thread(() -> {
            List<MoodEntry> all;
            try {
                all = new PrefStore(this).getAll();
            } catch (Throwable t) {
                Log.e("CAL", "Failed to load entries", t);
                all = Collections.emptyList();
            }

            final Map<String, List<CalendarDay>> byMood = groupByMood(all);

            runOnUiThread(() -> {
                try {
                    // remove the smoke dot to avoid confusion
                    cal.removeDecorators();

                    addMoodDecorator(cal, MoodColor.VERY_HAPPY, byMood.get(MoodColor.VERY_HAPPY));
                    addMoodDecorator(cal, MoodColor.CALM,       byMood.get(MoodColor.CALM));
                    addMoodDecorator(cal, MoodColor.NEUTRAL,    byMood.get(MoodColor.NEUTRAL));
                    addMoodDecorator(cal, MoodColor.STRESSED,   byMood.get(MoodColor.STRESSED));
                    addMoodDecorator(cal, MoodColor.SAD,        byMood.get(MoodColor.SAD));

                    cal.invalidateDecorators();
                } catch (Throwable t) {
                    Log.e("CAL", "Decorator apply crash", t);
                    Toast.makeText(this, "Calendar render error", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void addMoodDecorator(MaterialCalendarView cal, String mood, List<CalendarDay> days) {
        if (days == null || days.isEmpty()) return;
        cal.addDecorator(new EventDecorator(MoodColor.forMood(mood), days));
    }

    private Map<String, List<CalendarDay>> groupByMood(List<MoodEntry> all) {
        Map<String, List<CalendarDay>> byMood = new HashMap<>();
        byMood.put(MoodColor.VERY_HAPPY, new ArrayList<>());
        byMood.put(MoodColor.CALM,       new ArrayList<>());
        byMood.put(MoodColor.NEUTRAL,    new ArrayList<>());
        byMood.put(MoodColor.STRESSED,   new ArrayList<>());
        byMood.put(MoodColor.SAD,        new ArrayList<>());

        for (MoodEntry e : all) {
            if (e == null || e.date == null || e.mood == null) continue;
            CalendarDay day = toDaySafe(e.date);
            if (day != null && byMood.containsKey(e.mood)) {
                byMood.get(e.mood).add(day);
            }
        }
        Log.d("CAL", "entries=" + all.size() + " VH=" + byMood.get(MoodColor.VERY_HAPPY).size());
        return byMood;
    }

    private CalendarDay toDaySafe(String iso) {
        try {
            Date d = ISO.parse(iso);
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            return CalendarDay.from(c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH) + 1,  // +1 is IMPORTANT (Calendar is 0-based)
                    c.get(Calendar.DAY_OF_MONTH));
        } catch (ParseException ex) {
            Log.w("CAL", "Bad date: " + iso);
            return null;
        }
    }
}
