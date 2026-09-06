package com.offxos.launcher;

import android.content.Context;
import java.util.LinkedHashMap;
import java.util.Map;

/** Persists launcher-owned widget presentation state such as size and order. */
public final class OffxHomeWidgetLayoutStore {
    private static final String PREFS = "offx_home_widget_layout";
    private static final String STATE = "state";
    private OffxHomeWidgetLayoutStore() {}

    // 0 = compact, 1 = standard, 2 = large.
    public static int size(Context context, int id) {
        String raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(STATE, "");
        for (String entry : raw.split(",")) {
            String[] p = entry.split(":");
            if (p.length == 2) {
                try {
                    if (Integer.parseInt(p[0]) == id) return clamp(Integer.parseInt(p[1]));
                } catch (Exception ignored) {}
            }
        }
        return 1;
    }

    public static void setSize(Context context, int id, int value) {
        Map<Integer,Integer> map = load(context);
        map.put(id, clamp(value));
        save(context, map);
    }

    public static void remove(Context context, int id) {
        Map<Integer,Integer> map = load(context);
        map.remove(id);
        save(context, map);
    }

    private static Map<Integer,Integer> load(Context context) {
        Map<Integer,Integer> map = new LinkedHashMap<>();
        String raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(STATE, "");
        for (String entry : raw.split(",")) {
            String[] p = entry.split(":");
            if (p.length == 2) {
                try { map.put(Integer.parseInt(p[0]), clamp(Integer.parseInt(p[1]))); }
                catch (Exception ignored) {}
            }
        }
        return map;
    }

    private static void save(Context context, Map<Integer,Integer> map) {
        StringBuilder out = new StringBuilder();
        for (Map.Entry<Integer,Integer> e : map.entrySet()) {
            if (out.length() > 0) out.append(',');
            out.append(e.getKey()).append(':').append(clamp(e.getValue()));
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putString(STATE, out.toString()).apply();
    }

    private static int clamp(int value) { return Math.max(0, Math.min(2, value)); }
}
