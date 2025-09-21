package com.example.moodjournal.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moodjournal.R;
import com.example.moodjournal.data.PrefStore;
import com.example.moodjournal.model.MoodEntry;
import com.example.moodjournal.util.DateUtils;
import com.example.moodjournal.util.MoodColor;
import com.example.moodjournal.util.SentimentAnalyzer; // <-- make sure this file exists

public class AddEditMoodActivity extends AppCompatActivity {

    private RadioGroup moodGroup;
    private EditText noteEt;
    private Spinner activitySpinner;
    private Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        // Bind views
        moodGroup = findViewById(R.id.moodGroup);
        noteEt = findViewById(R.id.note);
        activitySpinner = findViewById(R.id.activitySpinner);
        saveBtn = findViewById(R.id.saveBtn);

        // Default mood = Neutral
        ((RadioButton) findViewById(R.id.moodNeutral)).setChecked(true);

        // Activities spinner (uses res/values/strings.xml -> string-array activities_array)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.activities_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activitySpinner.setAdapter(adapter);

        // Save click
        saveBtn.setOnClickListener(v -> {
            String mood = selectedMood();
            String note = noteEt.getText().toString().trim();
            String activity = activitySpinner.getSelectedItem() != null
                    ? activitySpinner.getSelectedItem().toString()
                    : null;
            if ("None".equals(activity)) activity = null;

            // --- Sentiment analysis (offline) ---
            SentimentAnalyzer sa = new SentimentAnalyzer();
            float score = sa.analyze(note);
            String label = sa.label(score);

            // Build entry for *today* and save/replace by date
            MoodEntry e = new MoodEntry();
            e.date = DateUtils.todayIso();     // yyyy-MM-dd
            e.mood = mood;
            e.note = note;
            e.activity = activity;
            e.sentimentScore = score;
            e.sentimentLabel = label;

            new PrefStore(this).upsertByDate(e);
            finish();
        });
    }

    private String selectedMood() {
        int id = moodGroup.getCheckedRadioButtonId();
        if (id == R.id.moodVeryHappy) return MoodColor.VERY_HAPPY;
        if (id == R.id.moodCalm)       return MoodColor.CALM;
        if (id == R.id.moodNeutral)    return MoodColor.NEUTRAL;
        if (id == R.id.moodStressed)   return MoodColor.STRESSED;
        if (id == R.id.moodSad)        return MoodColor.SAD;
        return MoodColor.NEUTRAL;
    }
}
