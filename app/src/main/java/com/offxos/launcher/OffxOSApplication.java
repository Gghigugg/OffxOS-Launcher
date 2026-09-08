package com.offxos.launcher;

import android.app.Activity;
import android.app.Application;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.lang.reflect.Field;

/** Global premium polish layer for OffxOS activities and the persistent dock. */
public class OffxOSApplication extends Application {
    @Override public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override public void onActivityCreated(Activity activity, android.os.Bundle state) {
                activity.getWindow().setNavigationBarColor(Color.BLACK);
                if (activity instanceof MainActivity) {
                    activity.getWindow().getDecorView().post(() -> polishHome((MainActivity) activity));
                }
            }
            @Override public void onActivityStarted(Activity a) {}
            @Override public void onActivityResumed(Activity a) {}
            @Override public void onActivityPaused(Activity a) {}
            @Override public void onActivityStopped(Activity a) {}
            @Override public void onActivitySaveInstanceState(Activity a, android.os.Bundle b) {}
            @Override public void onActivityDestroyed(Activity a) {}
        });
    }

    private void polishHome(MainActivity activity) {
        try {
            Field field = MainActivity.class.getDeclaredField("dock");
            field.setAccessible(true);
            LinearLayout dock = (LinearLayout) field.get(activity);
            if (dock == null) return;

            GradientDrawable glass = new GradientDrawable();
            glass.setShape(GradientDrawable.RECTANGLE);
            glass.setCornerRadius(dp(activity, 30));
            glass.setColor(Color.argb(58, 255, 255, 255));
            glass.setStroke(dp(activity, 1), Color.argb(90, 255, 255, 255));
            dock.setBackground(glass);
            dock.setElevation(dp(activity, 10));
            dock.setClipToPadding(false);
            dock.setPadding(dp(activity, 12), dp(activity, 9), dp(activity, 12), dp(activity, 9));

            for (int i = 0; i < dock.getChildCount(); i++) {
                View child = dock.getChildAt(i);
                GradientDrawable tile = new GradientDrawable();
                tile.setShape(GradientDrawable.RECTANGLE);
                tile.setCornerRadius(dp(activity, 20));
                tile.setColor(Color.argb(26, 255, 255, 255));
                tile.setStroke(dp(activity, 1), Color.argb(42, 255, 255, 255));
                child.setBackground(tile);
                child.setElevation(dp(activity, 2));
                child.setScaleX(.92f);
                child.setScaleY(.92f);
                child.animate().scaleX(1f).scaleY(1f).alpha(1f)
                        .setStartDelay(Math.min(i * 45L, 180L)).setDuration(260L).start();
            }

            dock.setAlpha(0f);
            dock.setTranslationY(dp(activity, 18));
            dock.animate().alpha(1f).translationY(0f).setDuration(360L).start();
        } catch (Throwable ignored) {}
    }

    private int dp(Activity a, float n) {
        return (int) (n * a.getResources().getDisplayMetrics().density + .5f);
    }
}
