package com.offxos.launcher;

import android.content.Context;

public final class WallpaperStore {
    private static final String PREFS = "offx_wallpaper";
    private static final String KEY = "selected";
    public static int get(Context c) { return c.getSharedPreferences(PREFS, 0).getInt(KEY, 0); }
    public static void set(Context c, int resId) { c.getSharedPreferences(PREFS, 0).edit().putInt(KEY, resId).apply(); }
    public static void clear(Context c) { c.getSharedPreferences(PREFS, 0).edit().remove(KEY).apply(); }
    private WallpaperStore() {}
}
