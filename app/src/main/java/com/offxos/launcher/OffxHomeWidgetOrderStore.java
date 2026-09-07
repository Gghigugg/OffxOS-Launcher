package com.offxos.launcher;

import android.content.Context;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

/** Persists the user's Home widget order. */
public final class OffxHomeWidgetOrderStore {
    private static final String PREFS="offx_home_widget_order"; private static final String IDS="ids";
    private OffxHomeWidgetOrderStore(){}
    public static ArrayList<Integer> load(Context context){ArrayList<Integer> out=new ArrayList<>();Set<String> saved=context.getSharedPreferences(PREFS,Context.MODE_PRIVATE).getStringSet(IDS,new LinkedHashSet<>());for(String value:saved)try{out.add(Integer.parseInt(value));}catch(Exception ignored){}return out;}
    public static void sync(Context context,ArrayList<Integer> ids){Set<String> next=new LinkedHashSet<>();ArrayList<Integer> current=load(context);for(Integer id:current)if(ids.contains(id))next.add(String.valueOf(id));for(Integer id:ids)if(!next.contains(String.valueOf(id)))next.add(String.valueOf(id));save(context,next);}
    public static void move(Context context,int id,int direction){ArrayList<Integer> list=load(context);int at=list.indexOf(id),to=at+direction;if(at<0||to<0||to>=list.size())return;Integer other=list.get(to);list.set(at,other);list.set(to,id);Set<String> next=new LinkedHashSet<>();for(Integer value:list)next.add(String.valueOf(value));save(context,next);}
    public static void remove(Context context,int id){ArrayList<Integer> list=load(context);list.remove(Integer.valueOf(id));Set<String> next=new LinkedHashSet<>();for(Integer value:list)next.add(String.valueOf(value));save(context,next);}
    public static void remap(Context context,int oldId,int newId){ArrayList<Integer> list=load(context);int at=list.indexOf(oldId);if(at<0)return;list.set(at,newId);Set<String> next=new LinkedHashSet<>();for(Integer value:list)next.add(String.valueOf(value));save(context,next);}
    private static void save(Context context,Set<String> ids){context.getSharedPreferences(PREFS,Context.MODE_PRIVATE).edit().putStringSet(IDS,ids).apply();}
}
