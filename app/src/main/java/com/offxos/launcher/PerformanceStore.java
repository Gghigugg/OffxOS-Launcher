package com.offxos.launcher;

import android.content.Context;

/** Lightweight persistent performance preference for low-end devices. */
public class PerformanceStore {
    private static final String PREF = "offxos_performance";
    private final android.content.SharedPreferences prefs;

    public PerformanceStore(Context context) {
        prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public boolean isLiteMode() {
        return prefs.getBoolean("lite", true);
    }

    public void setLiteMode(boolean enabled) {
        prefs.edit().putBoolean("lite", enabled).apply();
    }
}
