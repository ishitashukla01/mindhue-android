package com.example.moodjournal.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moodjournal.R;
import com.example.moodjournal.data.PrefStore;
import com.example.moodjournal.model.MoodEntry;
import com.example.moodjournal.util.MoodColor;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);



        PieChart moodPie = findViewById(R.id.pieChart);
        PieChart sentimentPie = findViewById(R.id.sentimentPie);
        TextView summary = findViewById(R.id.summary);

        List<MoodEntry> all = new PrefStore(this).getAll();
        if (all.isEmpty()) {
            summary.setText("No data yet. Add some moods first.");
            return;
        }

        // ---------- Mood distribution ----------
        Map<String, Integer> moodCounts = new HashMap<>();
        moodCounts.put(MoodColor.VERY_HAPPY, 0);
        moodCounts.put(MoodColor.CALM, 0);
        moodCounts.put(MoodColor.NEUTRAL, 0);
        moodCounts.put(MoodColor.STRESSED, 0);
        moodCounts.put(MoodColor.SAD, 0);

        for (MoodEntry e : all) {
            if (e != null && e.mood != null && moodCounts.containsKey(e.mood)) {
                moodCounts.put(e.mood, moodCounts.get(e.mood) + 1);
            }
        }

        applyPieStyle(moodPie);
        moodPie.setData(buildMoodPieData(moodCounts));
        moodPie.invalidate();

        // ---------- Sentiment distribution ----------
        int pos = 0, neu = 0, neg = 0;
        float sumSent = 0f;
        int nSent = 0;

        for (MoodEntry e : all) {
            if (e == null || e.sentimentLabel == null) continue; // skip old entries
            switch (e.sentimentLabel) {
                case "POSITIVE": pos++; break;
                case "NEGATIVE": neg++; break;
                default: neu++; break;
            }
            sumSent += e.sentimentScore;
            nSent++;
        }

        applyPieStyle(sentimentPie);
        sentimentPie.setData(buildSentimentPieData(pos, neu, neg));
        sentimentPie.invalidate();

        // ---------- Summary ----------
        int totalDays = 0;
        for (int v : moodCounts.values()) totalDays += v;
        String topMood = topKey(moodCounts);
        float avgSent = (nSent == 0) ? 0f : (sumSent / nSent);

        StringBuilder sb = new StringBuilder();
        sb.append("Total days tracked: ").append(totalDays).append('\n');
        sb.append("Most frequent mood: ").append(pretty(topMood)).append('\n');

        sb.append("\nTop activity by mood:\n");
        sb.append("• very happy: ").append(topActivityFor(MoodColor.VERY_HAPPY, all)).append('\n');
        sb.append("• calm: ").append(topActivityFor(MoodColor.CALM, all)).append('\n');
        sb.append("• neutral: ").append(topActivityFor(MoodColor.NEUTRAL, all)).append('\n');
        sb.append("• stressed: ").append(topActivityFor(MoodColor.STRESSED, all)).append('\n');
        sb.append("• sad: ").append(topActivityFor(MoodColor.SAD, all)).append('\n');

        sb.append("\nSentiment summary:\n");
        sb.append("• positive: ").append(pos)
                .append(", neutral: ").append(neu)
                .append(", negative: ").append(neg).append('\n');
        sb.append("• average note sentiment: ")
                .append(String.format(Locale.getDefault(), "%.2f", avgSent));

        summary.setText(sb.toString());
    }

    // ===== Helpers =====

    private void applyPieStyle(PieChart pie) {
        pie.setUsePercentValues(true);
        pie.setDrawEntryLabels(false);
        pie.getDescription().setEnabled(false);
        Legend l = pie.getLegend();
        l.setWordWrapEnabled(true);
        l.setTextSize(12f);
    }

    private PieData buildMoodPieData(Map<String, Integer> counts) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        for (Map.Entry<String, Integer> e : counts.entrySet()) {
            if (e.getValue() > 0) {
                entries.add(new PieEntry(e.getValue(), pretty(e.getKey())));
                colors.add(MoodColor.forMood(e.getKey()));
            }
        }
        PieDataSet set = new PieDataSet(entries, "Moods");
        set.setSliceSpace(2f);
        set.setColors(colors);
        set.setValueTextSize(12f);
        return new PieData(set);
    }

    private PieData buildSentimentPieData(int pos, int neu, int neg) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        if (pos > 0) entries.add(new PieEntry(pos, "Positive"));
        if (neu > 0) entries.add(new PieEntry(neu, "Neutral"));
        if (neg > 0) entries.add(new PieEntry(neg, "Negative"));

        PieDataSet set = new PieDataSet(entries, "Sentiments");
        // positive, neutral, negative
        set.setColors(0xFFA5D6A7, 0xFFB0BEC5, 0xFFEF9A9A);
        set.setSliceSpace(2f);
        set.setValueTextSize(12f);
        return new PieData(set);
    }

    private String pretty(String moodKey) {
        if (moodKey == null) return "—";
        return moodKey.replace('_', ' ').toLowerCase();
    }

    private String topKey(Map<String, Integer> map) {
        String best = null; int max = -1;
        for (Map.Entry<String, Integer> e : map.entrySet()) {
            if (e.getValue() > max) { max = e.getValue(); best = e.getKey(); }
        }
        return best;
    }

    private String topActivityFor(String mood, List<MoodEntry> all) {
        Map<String, Integer> count = new HashMap<>();
        for (MoodEntry e : all) {
            if (e == null || e.mood == null || e.activity == null) continue;
            if (!e.mood.equals(mood)) continue;
            count.put(e.activity, count.getOrDefault(e.activity, 0) + 1);
        }
        String best = null; int max = 0;
        for (Map.Entry<String, Integer> en : count.entrySet()) {
            if (en.getValue() > max) { max = en.getValue(); best = en.getKey(); }
        }
        return best == null ? "—" : best + " (" + max + ")";
    }
}
