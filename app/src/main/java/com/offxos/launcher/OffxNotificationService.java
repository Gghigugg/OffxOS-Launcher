package com.offxos.launcher;

import android.app.Notification;
import android.content.SharedPreferences;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import java.util.ArrayList;
import java.util.List;

public class OffxNotificationService extends NotificationListenerService {
    public static int count = 0;
    private static final ArrayList<Notice> notices = new ArrayList<>();
    private static final String PREFS = "offx_notifications";
    private static final String KEY_DATA = "data";

    public static class Notice {
        public final String key, title, text, app;
        public final long when;
        Notice(String key, String title, String text, String app, long when) {
            this.key = key; this.title = title; this.text = text; this.app = app; this.when = when;
        }
    }

    public static synchronized List<Notice> getNotices() { return new ArrayList<>(notices); }

    private void save() {
        StringBuilder b = new StringBuilder();
        for (Notice n : notices) {
            b.append(esc(n.key)).append('|').append(esc(n.title)).append('|').append(esc(n.text)).append('|').append(esc(n.app)).append('|').append(n.when).append('\n');
        }
        getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString(KEY_DATA, b.toString()).apply();
    }

    private static String esc(String s) { return s == null ? "" : s.replace("\\", "\\\\").replace("|", "\\p").replace("\n", "\\n"); }
    private static String[] split(String s) {
        ArrayList<String> out = new ArrayList<>(); StringBuilder b = new StringBuilder(); boolean e=false;
        for(char c:s.toCharArray()) { if(e){ if(c=='n') b.append('\n'); else if(c=='p') b.append('|'); else b.append(c); e=false; } else if(c=='\\') e=true; else if(c=='|'){out.add(b.toString());b.setLength(0);} else b.append(c); }
        out.add(b.toString()); return out.toArray(new String[0]);
    }

    private synchronized void loadSaved() {
        if (!notices.isEmpty()) { count = notices.size(); return; }
        String raw = getSharedPreferences(PREFS, MODE_PRIVATE).getString(KEY_DATA, "");
        if (raw == null || raw.isEmpty()) { count = 0; return; }
        for(String line:raw.split("\\n")) {
            if(line.isEmpty()) continue;
            String[] p=split(line);
            if(p.length>=5) try { notices.add(new Notice(p[0],p[1],p[2],p[3],Long.parseLong(p[4]))); } catch(Exception ignored) {}
        }
        count=notices.size();
    }

    @Override public void onListenerConnected() {
        synchronized(this) { notices.clear(); }
        loadSaved();
        try {
            StatusBarNotification[] active=getActiveNotifications();
            if(active!=null) for(StatusBarNotification sbn:active) addOrUpdate(sbn);
        } catch(Exception ignored) {}
    }

    private synchronized void addOrUpdate(StatusBarNotification sbn) {
        Notification n=sbn.getNotification();
        CharSequence t=n.extras.getCharSequence(Notification.EXTRA_TITLE);
        CharSequence x=n.extras.getCharSequence(Notification.EXTRA_TEXT);
        String key=sbn.getKey();
        for(int i=notices.size()-1;i>=0;i--) if(key.equals(notices.get(i).key)) notices.remove(i);
        notices.add(0,new Notice(key,t==null?"Notification":t.toString(),x==null?"":x.toString(),sbn.getPackageName(),System.currentTimeMillis()));
        if(notices.size()>40) notices.remove(notices.size()-1);
        count=notices.size(); save();
    }

    @Override public void onNotificationPosted(StatusBarNotification sbn) { addOrUpdate(sbn); }

    @Override public synchronized void onNotificationRemoved(StatusBarNotification sbn) {
        String key=sbn.getKey();
        for(int i=notices.size()-1;i>=0;i--) if(key.equals(notices.get(i).key)) { notices.remove(i); break; }
        count=notices.size(); save();
    }

    public synchronized void dismiss(String key) {
        for(int i=notices.size()-1;i>=0;i--) if(key.equals(notices.get(i).key)) { notices.remove(i); break; }
        count=notices.size(); save();
        try { cancelNotification(key); } catch(Exception ignored) {}
    }

    public static synchronized void dismissFromCenter(String key) {
        // Actual notification cancellation is performed when the listener is connected.
        for(int i=notices.size()-1;i>=0;i--) if(key.equals(notices.get(i).key)) notices.remove(i);
        count=notices.size();
    }
}
