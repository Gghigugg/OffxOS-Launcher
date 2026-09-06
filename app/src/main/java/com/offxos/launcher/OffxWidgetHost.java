package com.offxos.launcher;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.content.Context;

/** OffxOS widget host with a dedicated stable host id. */
public final class OffxWidgetHost extends AppWidgetHost {
    public static final int HOST_ID = 4101;

    public OffxWidgetHost(Context context) {
        super(context, HOST_ID);
    }

    @Override
    protected AppWidgetHostView onCreateView(Context context, int appWidgetId,
                                               android.appwidget.AppWidgetProviderInfo info) {
        AppWidgetHostView view = new AppWidgetHostView(context);
        view.setAppWidget(appWidgetId, info);
        return view;
    }
}
