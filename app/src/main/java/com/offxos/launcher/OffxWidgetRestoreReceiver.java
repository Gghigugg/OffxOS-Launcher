package com.offxos.launcher;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** Keeps OffxOS widget persistence aligned when Android restores host widget IDs. */
public final class OffxWidgetRestoreReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        if (!AppWidgetManager.ACTION_APPWIDGET_HOST_RESTORED.equals(intent.getAction())) return;
        if (intent.getIntExtra(AppWidgetManager.EXTRA_HOST_ID, -1) != OffxWidgetHost.HOST_ID) return;
        int[] oldIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_OLD_IDS);
        int[] newIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
        if (oldIds == null || newIds == null) return;
        int count = Math.min(oldIds.length, newIds.length);
        for (int i = 0; i < count; i++) {
            if (oldIds[i] == newIds[i]) continue;
            OffxHomeWidgetStore.remap(context, oldIds[i], newIds[i]);
            OffxHomeWidgetLayoutStore.remap(context, oldIds[i], newIds[i]);
            OffxHomeWidgetOrderStore.remap(context, oldIds[i], newIds[i]);
        }
    }
}
