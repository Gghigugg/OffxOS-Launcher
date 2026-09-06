package com.offxos.launcher;

import android.app.Notification;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import java.util.ArrayList;
import java.util.List;

public class OffxNotificationService extends NotificationListenerService {
    public static int count = 0;
    private static final ArrayList<Notice> notices = new ArrayList<>();

    public static class Notice {
        public final String title, text, app;
        public final long when;
        Notice(String title, String text, String app, long when) {
            this.title = title; this.text = text; this.app = app; this.when = when;
        }
    }

    public static synchronized List<Notice> getNotices() {
        return new ArrayList<>(notices);
    }

    @Override public synchronized void onNotificationPosted(StatusBarNotification sbn) {
        Notification n = sbn.getNotification();
        CharSequence t = n.extras.getCharSequence(Notification.EXTRA_TITLE);
        CharSequence x = n.extras.getCharSequence(Notification.EXTRA_TEXT);
        notices.add(0, new Notice(t == null ? "Notification" : t.toString(), x == null ? "" : x.toString(), sbn.getPackageName(), System.currentTimeMillis()));
        if (notices.size() > 40) notices.remove(notices.size() - 1);
        count = notices.size();
    }

    @Override public synchronized void onNotificationRemoved(StatusBarNotification sbn) {
        if (!notices.isEmpty()) notices.remove(notices.size() - 1);
        count = notices.size();
    }
}
