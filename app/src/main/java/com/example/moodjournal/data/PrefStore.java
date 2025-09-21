package com.example.moodjournal.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.moodjournal.model.MoodEntry;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PrefStore {
    private static final String FILE = "moods_pref";
    private static final String KEY  = "entries";
    private final SharedPreferences sp;
    private final Gson gson = new Gson();
    private final Type LIST_TYPE = new TypeToken<ArrayList<MoodEntry>>(){}.getType();

    public PrefStore(Context ctx) {
        sp = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    public ArrayList<MoodEntry> getAll() {
        String json = sp.getString(KEY, "[]");
        ArrayList<MoodEntry> list = gson.fromJson(json, LIST_TYPE);
        if (list == null) list = new ArrayList<>();
        // newest first by id (timestamp)
        Collections.sort(list, (a, b) -> Long.compare(b.id, a.id));
        return list;
    }

    public void saveAll(List<MoodEntry> entries) {
        sp.edit().putString(KEY, gson.toJson(entries)).apply();
    }

    /** Upsert by date: if an entry for the date exists, replace it; else add new at top. */
    public void upsertByDate(MoodEntry e) {
        ArrayList<MoodEntry> all = getAll();
        boolean replaced = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).date.equals(e.date)) {
                e.id = System.currentTimeMillis();
                all.set(i, e);
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            e.id = System.currentTimeMillis();
            all.add(0, e);
        }
        saveAll(all);
    }

    public void deleteById(long id) {
        ArrayList<MoodEntry> all = getAll();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).id == id) { all.remove(i); break; }
        }
        saveAll(all);
    }
}