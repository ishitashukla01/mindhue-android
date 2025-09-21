package com.example.moodjournal.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.moodjournal.R;
import com.example.moodjournal.data.SecPrefs;

import java.util.concurrent.Executor;

public class LockActivity extends AppCompatActivity {

    private SecPrefs sec;
    private EditText pinEt;
    private Button unlockBtn, bioBtn;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sec = new SecPrefs(this);

        // If lock not enabled, go straight in
        if (!sec.isLockEnabled() || !sec.hasPin()) {
            openMain(); return;
        }

        setContentView(R.layout.activity_lock);
        pinEt = findViewById(R.id.pinEt);
        unlockBtn = findViewById(R.id.unlockBtn);
        bioBtn = findViewById(R.id.bioBtn);

        unlockBtn.setOnClickListener(v -> {
            String pin = pinEt.getText().toString().trim();
            if (sec.verifyPin(pin)) openMain();
            else Toast.makeText(this, "Wrong PIN", Toast.LENGTH_SHORT).show();
        });

        // Show fingerprint button only if enabled and available
        boolean canBio = sec.isBioEnabled() &&
                BiometricManager.from(this).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                        == BiometricManager.BIOMETRIC_SUCCESS;
        bioBtn.setVisibility(canBio ? View.VISIBLE : View.GONE);
        bioBtn.setOnClickListener(v -> promptBiometric());
    }

    private void promptBiometric() {
        Executor ex = ContextCompat.getMainExecutor(this);
        BiometricPrompt p = new BiometricPrompt(this, ex, new BiometricPrompt.AuthenticationCallback() {
            @Override public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) { openMain(); }
            @Override public void onAuthenticationError(int code, CharSequence err) {
                Toast.makeText(LockActivity.this, String.valueOf(err), Toast.LENGTH_SHORT).show();
            }
        });
        BiometricPrompt.PromptInfo info = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Unlock Mood Journal")
                .setSubtitle("Use your fingerprint")
                .setNegativeButtonText("Cancel")
                .build();
        p.authenticate(info);
    }

    private void openMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
