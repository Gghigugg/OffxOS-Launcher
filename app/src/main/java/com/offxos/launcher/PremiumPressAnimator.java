package com.offxos.launcher;

import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

/** Reusable Liquid Glass press feedback for launcher surfaces. */
public final class PremiumPressAnimator {
    private PremiumPressAnimator() {}

    public static void press(View view, boolean lite, final Runnable onReleased) {
        if (view == null) return;
        int down = lite ? 45 : 70;
        int up = lite ? 95 : 210;
        view.animate().cancel();
        view.animate()
                .scaleX(.90f).scaleY(.90f)
                .setDuration(down)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> {
                    view.animate()
                            .scaleX(1f).scaleY(1f)
                            .setDuration(up)
                            .setInterpolator(new OvershootInterpolator(1.7f))
                            .withEndAction(() -> {
                                if (onReleased != null) onReleased.run();
                            })
                            .start();
                })
                .start();
    }

    public static void cancel(View view) {
        if (view == null) return;
        view.animate().cancel();
        view.setScaleX(1f);
        view.setScaleY(1f);
    }
}
