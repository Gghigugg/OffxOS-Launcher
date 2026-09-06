package com.offxos.launcher;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
public class OffxNotificationService extends NotificationListenerService {
    public static int count=0;
    @Override public void onNotificationPosted(StatusBarNotification sbn){ count++; }
    @Override public void onNotificationRemoved(StatusBarNotification sbn){ if(count>0) count--; }
}
