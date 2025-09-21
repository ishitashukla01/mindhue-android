package com.example.moodjournal.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moodjournal.util.Streaks;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.moodjournal.R;
import com.example.moodjournal.data.PrefStore;
import com.example.moodjournal.model.MoodEntry;
import com.example.moodjournal.util.DateUtils;
import com.example.moodjournal.util.MoodColor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private LinearLayout paletteRow;
    private RecyclerView recycler;
    private FloatingActionButton fab;

    private PrefStore store;
    private MoodAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Views
        toolbar = findViewById(R.id.toolbar);
        paletteRow = findViewById(R.id.paletteRow);
        recycler = findViewById(R.id.recycler);
        fab = findViewById(R.id.fab);

        // Toolbar as ActionBar (needed for menu)
        setSupportActionBar(toolbar);

        // Data
        store = new PrefStore(this);

        // RecyclerView
        adapter = new MoodAdapter();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        // Item actions
        adapter.setOnItemLongClick(entry -> new AlertDialog.Builder(this)
                .setTitle("Delete entry?")
                .setMessage("Remove mood for " + DateUtils.pretty(entry.date) + "?")
                .setPositiveButton("Delete", (d, w) -> { store.deleteById(entry.id); refresh(); })
                .setNegativeButton("Cancel", null)
                .show());

        // Add new mood
        fab.setOnClickListener(v ->
                startActivity(new Intent(this, AddEditMoodActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        List<MoodEntry> all = store.getAll();  // newest first
        adapter.submitList(all);
        renderWeeklyPalette(all);
        TextView streakChip = findViewById(R.id.streakChip);
        Streaks.Result sr = Streaks.compute(all);
        List<String> badges = Streaks.badges(sr);
        String txt = "Daily streak: " + sr.dailyStreak + "  •  Positive: " + sr.positiveStreak;
        if (!badges.isEmpty()) txt += "\n" + String.join("  |  ", badges);
        streakChip.setText(txt);

    }

    /** Builds 7 blocks (Mon..Sun) colored by mood; grey if none. */
    private void renderWeeklyPalette(List<MoodEntry> all) {
        paletteRow.removeAllViews();

        Map<String, MoodEntry> byDate = new HashMap<>();
        for (MoodEntry e : all) byDate.put(e.date, e);

        List<String> weekDates = DateUtils.weekIsoDatesAroundToday();
        for (String iso : weekDates) {
            View block = new View(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
            lp.setMargins(8, 6, 8, 6);
            block.setLayoutParams(lp);

            MoodEntry e = byDate.get(iso);
            int color = (e == null) ? 0xFFB0BEC5 : MoodColor.forMood(e.mood); // default blue-grey
            block.setBackgroundColor(color);

            paletteRow.addView(block);
        }
    }

    // ===== Menu (Stats) =====
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_stats) {
            startActivity(new Intent(this, StatsActivity.class));
            return true;
        } else if (id == R.id.action_calendar) {
            startActivity(new Intent(this, CalendarActivity.class));
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
