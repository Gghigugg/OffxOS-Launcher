package com.offxos.launcher;

import android.content.Context;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

/** Ordered persistent widget IDs used by the OffxOS Home Screen and widget picker. */
public final class OffxHomeWidgetStore {
    private static final String PREFS = "offx_home_widgets";
    private static final String IDS = "ids_csv";
    private static final String LEGACY_IDS = "ids";
    private OffxHomeWidgetStore() {}

    public static ArrayList<Integer> load(Context context) {
        ArrayList<Integer> out = new ArrayList<>();
        String csv = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(IDS, "");
        if (!csv.isEmpty()) {
            for (String value : csv.split(",")) {
                try { out.add(Integer.parseInt(value)); } catch (Exception ignored) {}
            }
            return out;
        }
        Set<String> legacy = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getStringSet(LEGACY_IDS, new LinkedHashSet<>());
        for (String value : legacy) {
            try { out.add(Integer.parseInt(value)); } catch (Exception ignored) {}
        }
        if (!out.isEmpty()) save(context, out);
        return out;
    }

    public static void add(Context context, int id) {
        ArrayList<Integer> next = load(context);
        if (!next.contains(id)) next.add(id);
        save(context, next);
    }

    public static void remove(Context context, int id) {
        ArrayList<Integer> next = load(context);
        next.remove(Integer.valueOf(id));
        save(context, next);
    }

    public static void move(Context context, int id, int targetIndex) {
        ArrayList<Integer> next = load(context);
        int from = next.indexOf(id);
        if (from < 0) return;
        next.remove(from);
        targetIndex = Math.max(0, Math.min(targetIndex, next.size()));
        next.add(targetIndex, id);
        save(context, next);
    }

    public static void remap(Context context, int oldId, int newId) {
        ArrayList<Integer> next = load(context);
        int index = next.indexOf(oldId);
        if (index >= 0) next.set(index, newId);
        save(context, next);
    }

    private static void save(Context context, ArrayList<Integer> ids) {
        StringBuilder csv = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) csv.append(',');
            csv.append(ids.get(i));
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putString(IDS, csv.toString()).apply();
    }
}
