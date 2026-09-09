package com.offxos.launcher;

import android.os.Build;
import android.graphics.Shader;
import android.graphics.RenderEffect;
import android.view.View;

/** Small compatibility helper for Android 8-16 glass surfaces. */
public final class GlassBackdrop {
    private GlassBackdrop() {}
    public static void soften(View view, float radiusPx) {
        if (Build.VERSION.SDK_INT >= 31) {
            view.setRenderEffect(RenderEffect.createBlurEffect(radiusPx, radiusPx, Shader.TileMode.CLAMP));
        }
    }
}
