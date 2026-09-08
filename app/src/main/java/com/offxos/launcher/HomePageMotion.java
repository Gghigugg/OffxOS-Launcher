package com.offxos.launcher;

import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

/** Premium iOS-inspired Home Screen motion helpers for OffxOS. */
public final class HomePageMotion {
    private HomePageMotion() {}

    public static void enter(View view, int index, boolean liteMode) {
        if (view == null) return;
        view.animate().cancel();
        view.setAlpha(0f);
        view.setTranslationY(liteMode ? 4f : 14f);
        view.setScaleX(liteMode ? .98f : .92f);
        view.setScaleY(liteMode ? .98f : .92f);
        view.animate().alpha(1f).translationY(0f).scaleX(1f).scaleY(1f)
                .setStartDelay(liteMode ? 0 : Math.min(index * 16L, 110L))
                .setDuration(liteMode ? 100 : 260)
                .setInterpolator(new OvershootInterpolator(liteMode ? 1.15f : 1.6f))
                .start();
    }

    public static void press(View view, boolean liteMode) {
        if (view == null) return;
        view.animate().cancel();
        view.animate().scaleX(.92f).scaleY(.92f)
                .setDuration(liteMode ? 45 : 70)
                .withEndAction(() -> view.animate().scaleX(1f).scaleY(1f)
                        .setDuration(liteMode ? 90 : 180)
                        .setInterpolator(new OvershootInterpolator(2f)).start())
                .start();
    }

    public static void pageSwipe(View view, float dx, boolean liteMode) {
        if (view == null || liteMode) return;
        float progress = Math.max(-1f, Math.min(1f, dx / 420f));
        view.setTranslationX(progress * 16f);
        view.setScaleX(1f - Math.abs(progress) * .01f);
        view.setScaleY(1f - Math.abs(progress) * .01f);
    }

    public static void settle(View view, boolean liteMode) {
        if (view == null || liteMode) return;
        view.animate().translationX(0f).scaleX(1f).scaleY(1f)
                .setDuration(180).setInterpolator(new DecelerateInterpolator()).start();
    }
}
