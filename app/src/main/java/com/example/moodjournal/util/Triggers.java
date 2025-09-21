package com.example.moodjournal.util;

import com.example.moodjournal.model.MoodEntry;
import java.util.*;
import java.util.regex.Pattern;

public final class Triggers {
    private static final Pattern SPLIT = Pattern.compile("[^\\p{L}\\p{N}]+");
    private static final Set<String> STOP = new HashSet<>(Arrays.asList(
            "the","a","an","and","or","but","to","of","for","in","on","at","with",
            "is","am","are","was","were","be","been","being","this","that","it","my",
            "i","me","we","you","he","she","they","our","your","their","as","so","very",
            "today","yesterday","tomorrow","really","just","not","no"
    ));

    public static class Top {
        public final List<Map.Entry<String,Integer>> positive;
        public final List<Map.Entry<String,Integer>> negative;
        Top(List<Map.Entry<String,Integer>> p, List<Map.Entry<String,Integer>> n){ positive=p; negative=n; }
    }

    public static Top find(List<MoodEntry> entries, int k){
        Map<String,Integer> pos = new HashMap<>(), neg = new HashMap<>();
        for (MoodEntry e : entries){
            if (e == null || e.note == null || e.note.isEmpty() || e.sentimentLabel == null) continue;
            String[] toks = SPLIT.split(e.note.toLowerCase(Locale.getDefault()));
            Set<String> seen = new HashSet<>();
            for (String t : toks){
                if (t.length() < 3 || STOP.contains(t)) continue;
                if (!seen.add(t)) continue; // count once per note to reduce spam
                if ("POSITIVE".equals(e.sentimentLabel)) pos.put(t, pos.getOrDefault(t,0)+1);
                else if ("NEGATIVE".equals(e.sentimentLabel)) neg.put(t, neg.getOrDefault(t,0)+1);
            }
        }
        return new Top(topK(pos, k), topK(neg, k));
    }

    private static List<Map.Entry<String,Integer>> topK(Map<String,Integer> m, int k){
        List<Map.Entry<String,Integer>> list = new ArrayList<>(m.entrySet());
        list.sort((a,b)-> Integer.compare(b.getValue(), a.getValue()));
        if (list.size() > k) return list.subList(0, k);
        return list;
    }
}
