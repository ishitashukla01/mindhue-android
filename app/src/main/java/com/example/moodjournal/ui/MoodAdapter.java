package com.example.moodjournal.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moodjournal.R;
import com.example.moodjournal.model.MoodEntry;
import com.example.moodjournal.util.DateUtils;
import com.example.moodjournal.util.MoodColor;

import java.util.Objects;

public class MoodAdapter extends ListAdapter<MoodEntry, MoodAdapter.VH> {

    public interface OnItemClick { void onClick(MoodEntry e); }
    public interface OnItemLongClick { void onLongClick(MoodEntry e); }

    private OnItemClick clickCb;
    private OnItemLongClick longClickCb;

    public MoodAdapter() { super(DIFF); }

    public void setOnItemClick(OnItemClick cb) { this.clickCb = cb; }
    public void setOnItemLongClick(OnItemLongClick cb) { this.longClickCb = cb; }

    private static final DiffUtil.ItemCallback<MoodEntry> DIFF =
            new DiffUtil.ItemCallback<MoodEntry>() {
                @Override public boolean areItemsTheSame(@NonNull MoodEntry a, @NonNull MoodEntry b) {
                    return a.id == b.id;
                }
                @Override public boolean areContentsTheSame(@NonNull MoodEntry a, @NonNull MoodEntry b) {
                    return Objects.equals(a.date, b.date) &&
                            Objects.equals(a.mood, b.mood) &&
                            Objects.equals(a.note, b.note) &&
                            Objects.equals(a.activity, b.activity);
                }
            };

    static class VH extends RecyclerView.ViewHolder {
        View colorStrip;
        TextView title, date, note, activityChip;
        TextView sentimentChip;

        VH(@NonNull View v) {
            super(v);
            colorStrip = v.findViewById(R.id.colorStrip);
            title = v.findViewById(R.id.title);
            date = v.findViewById(R.id.date);
            note = v.findViewById(R.id.note);
            activityChip = v.findViewById(R.id.activityChip);
            sentimentChip = v.findViewById(R.id.sentimentChip);
        }
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mood, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        MoodEntry m = getItem(position);

        // color strip
        h.colorStrip.setBackgroundColor(MoodColor.forMood(m.mood));

        // title: "😄 very happy"
        String emoji = MoodColor.emoji(m.mood);
        String pretty = MoodColor.pretty(m.mood);
        h.title.setText(emoji + " " + pretty);

        // date pretty format
        h.date.setText(DateUtils.pretty(m.date));

        // note (optional)
        h.note.setText(m.note == null ? "" : m.note);

        // activity chip (optional)
        if (m.activity == null || m.activity.trim().isEmpty()) {
            h.activityChip.setVisibility(View.GONE);
        } else {
            h.activityChip.setVisibility(View.VISIBLE);
            h.activityChip.setText(m.activity.trim());
        }

        // callbacks
        h.itemView.setOnClickListener(v -> { if (clickCb != null) clickCb.onClick(m); });
        h.itemView.setOnLongClickListener(v -> {
            if (longClickCb != null) longClickCb.onLongClick(m);
            return true;
        });
        if (m.sentimentLabel == null) {
            h.sentimentChip.setVisibility(View.GONE);
        } else {
            h.sentimentChip.setVisibility(View.VISIBLE);
            String face = "😐";
            int bg = 0xFFB0BEC5; // blue-grey
            if ("POSITIVE".equals(m.sentimentLabel)) { face = "😊"; bg = 0xFFA5D6A7; } // green-200
            if ("NEGATIVE".equals(m.sentimentLabel)) { face = "🙁"; bg = 0xFFEF9A9A; } // red-200
            h.sentimentChip.setText(face + " " + m.sentimentLabel.toLowerCase());
            h.sentimentChip.setBackgroundColor(bg);
        }
    }
}
