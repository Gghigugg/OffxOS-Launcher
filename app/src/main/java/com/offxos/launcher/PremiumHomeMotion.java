package com.offxos.launcher;

import android.view.View;
import android.view.animation.OvershootInterpolator;

/** Reusable Liquid Glass motion for Home Screen icons, folders and Dock items. */
public final class PremiumHomeMotion {
    private PremiumHomeMotion() {}

    public static void press(View view, boolean liteMode) {
        if (view == null) return;
        long down = liteMode ? 45L : 70L;
        long up = liteMode ? 100L : 190L;
        view.animate().cancel();
        view.setScaleX(1f);
        view.setScaleY(1f);
        view.animate()
                .scaleX(0.91f)
                .scaleY(0.91f)
                .setDuration(down)
                .withEndAction(() -> view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(up)
                        .setInterpolator(new OvershootInterpolator(2.2f))
                        .start())
                .start();
    }

    public static void entrance(View view, int index, boolean liteMode) {
        if (view == null) return;
        view.setAlpha(0f);
        view.setScaleX(liteMode ? 0.98f : 0.90f);
        view.setScaleY(liteMode ? 0.98f : 0.90f);
        long delay = liteMode ? 0L : Math.min(index * 18L, 120L);
        long duration = liteMode ? 110L : 240L;
        view.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setStartDelay(delay)
                .setDuration(duration)
                .setInterpolator(new OvershootInterpolator(liteMode ? 1.2f : 1.8f))
                .start();
    }
}
