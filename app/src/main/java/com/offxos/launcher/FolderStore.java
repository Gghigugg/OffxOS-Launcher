package com.offxos.launcher;

import android.content.Context;
import java.util.*;

public class FolderStore {
    private static final String PREF = "offxos_folders";
    private static final String KEY = "data";
    private final android.content.SharedPreferences prefs;

    public static class Folder {
        public String name;
        public final ArrayList<String> packages = new ArrayList<>();
        Folder(String n){ name=n; }
    }

    public FolderStore(Context c){ prefs=c.getSharedPreferences(PREF, Context.MODE_PRIVATE); }

    public synchronized ArrayList<Folder> getFolders(){
        ArrayList<Folder> out=new ArrayList<>();
        String raw=prefs.getString(KEY, "");
        if(raw.isEmpty()) return out;
        for(String line:raw.split("\\n")){
            if(line.trim().isEmpty()) continue;
            String[] p=line.split("\\|",2);
            Folder f=new Folder(p[0]);
            if(p.length>1 && !p[1].isEmpty()) for(String pkg:p[1].split(",")) if(!pkg.isEmpty()) f.packages.add(pkg);
            out.add(f);
        }
        return out;
    }

    private synchronized void save(ArrayList<Folder> fs){
        StringBuilder b=new StringBuilder();
        for(Folder f:fs){
            String name=f.name.replace("|"," ").replace("\n"," ").trim();
            if(name.isEmpty()) name="Folder";
            b.append(name).append('|');
            for(int i=0;i<f.packages.size();i++){ if(i>0)b.append(','); b.append(f.packages.get(i)); }
            b.append('\n');
        }
        prefs.edit().putString(KEY,b.toString()).apply();
    }

    public synchronized boolean create(String name){
        if(name==null || name.trim().isEmpty()) return false;
        ArrayList<Folder> fs=getFolders();
        for(Folder f:fs) if(f.name.equalsIgnoreCase(name.trim())) return false;
        fs.add(new Folder(name.trim())); save(fs); return true;
    }

    public synchronized boolean addToFolder(String name,String pkg){
        ArrayList<Folder> fs=getFolders();
        for(Folder f:fs) if(f.name.equals(name)){
            if(!f.packages.contains(pkg)) f.packages.add(pkg); else return false;
            save(fs); return true;
        }
        return false;
    }

    public synchronized void removePackage(String pkg){
        ArrayList<Folder> fs=getFolders(); boolean changed=false;
        for(Folder f:fs) changed|=f.packages.remove(pkg);
        if(changed) save(fs);
    }
}
