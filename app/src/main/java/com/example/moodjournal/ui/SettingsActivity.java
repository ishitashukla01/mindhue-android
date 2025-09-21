package com.example.moodjournal.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.example.moodjournal.R;
import com.example.moodjournal.data.SecPrefs;

public class SettingsActivity extends AppCompatActivity {

    private SecPrefs sec;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sec = new SecPrefs(this);

        MaterialSwitch lockSw = findViewById(R.id.lockSwitch);
        MaterialSwitch bioSw  = findViewById(R.id.bioSwitch);
        EditText pinNew       = findViewById(R.id.pinNew);
        Button savePin        = findViewById(R.id.savePinBtn);

        // Defensive: if any is null, bail with a friendly message
        if (lockSw == null || bioSw == null || pinNew == null || savePin == null) {
            Toast.makeText(this, "Settings layout mismatch. Check view IDs.", Toast.LENGTH_LONG).show();
            finish(); return;
        }

        lockSw.setChecked(sec.isLockEnabled());
        bioSw.setChecked(sec.isBioEnabled());

        lockSw.setOnCheckedChangeListener((buttonView, isChecked) -> sec.setLockEnabled(isChecked));
        bioSw.setOnCheckedChangeListener((buttonView, isChecked) -> sec.setBioEnabled(isChecked));

        savePin.setOnClickListener(v -> {
            String p = pinNew.getText().toString().trim();
            if (TextUtils.isEmpty(p) || p.length() < 4 || p.length() > 6) {
                Toast.makeText(this, "PIN must be 4–6 digits", Toast.LENGTH_SHORT).show();
                return;
            }
            sec.setPin(p);
            Toast.makeText(this, "PIN saved", Toast.LENGTH_SHORT).show();
            pinNew.setText("");
        });
    }
}
