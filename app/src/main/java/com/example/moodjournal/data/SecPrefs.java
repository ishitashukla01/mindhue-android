package com.example.moodjournal.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.security.MessageDigest;

public class SecPrefs {
    private static final String FILE = "sec_prefs";
    private static final String KEY_LOCK_ENABLED = "lock_enabled";
    private static final String KEY_BIO_ENABLED  = "bio_enabled";
    private static final String KEY_PIN_HASH     = "pin_hash";

    private final SharedPreferences sp;

    public SecPrefs(Context ctx) {
        sp = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    public boolean isLockEnabled() { return sp.getBoolean(KEY_LOCK_ENABLED, false); }
    public void setLockEnabled(boolean b) { sp.edit().putBoolean(KEY_LOCK_ENABLED, b).apply(); }

    public boolean isBioEnabled() { return sp.getBoolean(KEY_BIO_ENABLED, false); }
    public void setBioEnabled(boolean b) { sp.edit().putBoolean(KEY_BIO_ENABLED, b).apply(); }

    public void setPin(String pin) { sp.edit().putString(KEY_PIN_HASH, hash(pin)).apply(); }
    public boolean hasPin() { return sp.contains(KEY_PIN_HASH); }

    public boolean verifyPin(String pin) {
        String saved = sp.getString(KEY_PIN_HASH, null);
        return saved != null && saved.equals(hash(pin));
    }

    private String hash(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] out = md.digest(s.getBytes());
            return Base64.encodeToString(out, Base64.NO_WRAP);
        } catch (Exception e) {
            return s; // fallback (not ideal, but won’t crash)
        }
    }
}
