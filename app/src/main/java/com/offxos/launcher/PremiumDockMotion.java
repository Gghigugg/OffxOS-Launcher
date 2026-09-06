package com.offxos.launcher;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.animation.OvershootInterpolator;

/** Lightweight Liquid Glass motion for the persistent Home dock. */
public final class PremiumDockMotion {
    private PremiumDockMotion() {}

    public static void entrance(View view, int index, boolean liteMode) {
        if (view == null) return;
        view.setAlpha(0f);
        view.setTranslationY(liteMode ? 6f : 18f);
        view.setScaleX(liteMode ? .98f : .94f);
        view.setScaleY(liteMode ? .98f : .94f);
        long delay = liteMode ? 0L : Math.min(index * 35L, 140L);
        view.animate().alpha(1f).translationY(0f).scaleX(1f).scaleY(1f)
                .setStartDelay(delay)
                .setDuration(liteMode ? 120L : 280L)
                .setInterpolator(new OvershootInterpolator(liteMode ? 1.1f : 1.5f))
                .start();
    }

    public static void press(View view, boolean liteMode) {
        if (view == null) return;
        view.animate().cancel();
        view.animate().scaleX(.90f).scaleY(.90f)
                .setDuration(liteMode ? 45L : 70L)
                .withEndAction(() -> view.animate()
                        .scaleX(1f).scaleY(1f)
                        .setDuration(liteMode ? 100L : 190L)
                        .setInterpolator(new OvershootInterpolator(2f))
                        .start())
                .start();
    }

    public static void selected(View view, boolean selected, boolean liteMode) {
        if (view == null) return;
        if (selected) {
            GradientDrawable glow = new GradientDrawable();
            glow.setShape(GradientDrawable.RECTANGLE);
            glow.setCornerRadius(22f);
            glow.setColor(Color.argb(liteMode ? 18 : 30, 255, 255, 255));
            view.setBackground(glow);
            view.animate().scaleX(liteMode ? 1.01f : 1.04f)
                    .scaleY(liteMode ? 1.01f : 1.04f)
                    .setDuration(liteMode ? 90L : 150L)
                    .start();
        } else {
            view.setBackground(null);
            view.animate().scaleX(1f).scaleY(1f)
                    .setDuration(liteMode ? 70L : 120L)
                    .start();
        }
    }
}
