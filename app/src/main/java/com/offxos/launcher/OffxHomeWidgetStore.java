package com.offxos.launcher;

import android.content.Context;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

/** Shared persistent widget IDs used by the OffxOS Home Screen and widget picker. */
public final class OffxHomeWidgetStore {
    private static final String PREFS = "offx_home_widgets";
    private static final String IDS = "ids";
    private OffxHomeWidgetStore() {}

    public static ArrayList<Integer> load(Context context) {
        ArrayList<Integer> out = new ArrayList<>();
        Set<String> saved = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getStringSet(IDS, new LinkedHashSet<>());
        for (String value : saved) {
            try { out.add(Integer.parseInt(value)); }
            catch (Exception ignored) {}
        }
        return out;
    }

    public static void add(Context context, int id) {
        Set<String> next = new LinkedHashSet<>(loadStrings(context));
        next.add(String.valueOf(id));
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putStringSet(IDS, next).apply();
    }

    public static void remove(Context context, int id) {
        Set<String> next = new LinkedHashSet<>(loadStrings(context));
        next.remove(String.valueOf(id));
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putStringSet(IDS, next).apply();
    }

    private static Set<String> loadStrings(Context context) {
        return new LinkedHashSet<>(context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getStringSet(IDS, new LinkedHashSet<>()));
    }
}
