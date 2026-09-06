package com.offxos.launcher;

import android.view.View;
import android.view.animation.OvershootInterpolator;

/** Lightweight Liquid Glass motion for the persistent Home dock. */
public final class PremiumDockMotion {
    private PremiumDockMotion() {}

    public static void entrance(View view, int index, boolean liteMode) {
        if (view == null) return;
        view.setAlpha(0f);
        view.setTranslationY(liteMode ? 6f : 18f);
        long delay = liteMode ? 0L : Math.min(index * 35L, 140L);
        view.animate().alpha(1f).translationY(0f)
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
}
