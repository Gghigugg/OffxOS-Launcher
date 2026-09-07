package com.offxos.launcher;

import android.content.Context;
import java.util.*;

/** Persists the Home Screen order for apps and folders. */
public final class HomeLayoutStore {
    private static final String PREF = "offx_home_layout";
    private static final String KEY = "order";
    private HomeLayoutStore() {}

    public static ArrayList<String> load(Context c){
        String raw=c.getSharedPreferences(PREF,Context.MODE_PRIVATE).getString(KEY,"");
        ArrayList<String> out=new ArrayList<>();
        if(raw.isEmpty()) return out;
        for(String s:raw.split("\\n")) if(!s.trim().isEmpty()) out.add(s.trim());
        return out;
    }

    public static void save(Context c,List<String> items){
        StringBuilder b=new StringBuilder();
        for(String s:items) if(s!=null&&!s.trim().isEmpty()) b.append(s.trim()).append('\n');
        c.getSharedPreferences(PREF,Context.MODE_PRIVATE).edit().putString(KEY,b.toString()).apply();
    }

    public static ArrayList<String> sync(Context c,List<String> valid){
        ArrayList<String> old=load(c), out=new ArrayList<>();
        HashSet<String> allowed=new HashSet<>(valid);
        HashSet<String> seen=new HashSet<>();
        for(String s:old) if(allowed.contains(s)&&seen.add(s)) out.add(s);
        for(String s:valid) if(seen.add(s)) out.add(s);
        save(c,out); return out;
    }
}
