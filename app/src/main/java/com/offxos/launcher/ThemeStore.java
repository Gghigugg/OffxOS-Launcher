package com.offxos.launcher;

import android.content.Context;

public class ThemeStore {
    private static final String PREF="offxos_theme";
    private final android.content.SharedPreferences prefs;
    public ThemeStore(Context c){prefs=c.getSharedPreferences(PREF,Context.MODE_PRIVATE);}
    public boolean isLight(){return prefs.getBoolean("light",false);}
    public void setLight(boolean light){prefs.edit().putBoolean("light",light).apply();}
}
