package com.offxos.launcher;

import android.content.Context;

public class OffxThemeStore {
    private static final String PREF="offxos_theme";
    private final android.content.SharedPreferences p;
    public OffxThemeStore(Context c){p=c.getSharedPreferences(PREF,Context.MODE_PRIVATE);}
    public boolean isLight(){return p.getBoolean("light",false);}
    public void setLight(boolean light){p.edit().putBoolean("light",light).apply();}
}
