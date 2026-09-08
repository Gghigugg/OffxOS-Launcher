package com.offxos.launcher;

import android.content.Context;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

/** Ordered persistent widget IDs and Home placement used by OffxOS. */
public final class OffxHomeWidgetStore {
    private static final String PREFS = "offx_home_widgets";
    private static final String IDS = "ids_csv";
    private static final String LEGACY_IDS = "ids";
    private static final String POS_PREFIX = "pos_";
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
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .remove(POS_PREFIX + id).apply();
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
        String oldPos = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(POS_PREFIX + oldId, null);
        if (oldPos != null) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                    .remove(POS_PREFIX + oldId).putString(POS_PREFIX + newId, oldPos).apply();
        }
        save(context, next);
    }

    /** Save widget page/cell placement. spanX/spanY are the occupied cell size. */
    public static void savePosition(Context context, int id, int page, int column,
                                     int row, int spanX, int spanY) {
        String value = page + "," + column + "," + row + "," + spanX + "," + spanY;
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putString(POS_PREFIX + id, value).apply();
    }

    /** Returns {page, column, row, spanX, spanY}, or null if not stored. */
    public static int[] loadPosition(Context context, int id) {
        String value = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(POS_PREFIX + id, null);
        if (value == null) return null;
        String[] parts = value.split(",");
        if (parts.length < 5) return null;
        try {
            return new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4])};
        } catch (Exception ignored) {
            return null;
        }
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
