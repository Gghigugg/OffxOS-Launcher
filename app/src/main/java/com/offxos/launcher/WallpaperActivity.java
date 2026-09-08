package com.offxos.launcher;

import android.app.Activity;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class WallpaperActivity extends Activity {
    private ThemeStore theme;
    private PerformanceStore performance;

    private int dp(float n) {
        return (int) (n * getResources().getDisplayMetrics().density + 0.5f);
    }

    private TextView tv(String text, float size) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextColor(Color.WHITE);
        t.setTextSize(size);
        t.setGravity(Gravity.CENTER_VERTICAL);
        return t;
    }

    private GradientDrawable glass() {
        GradientDrawable g = new GradientDrawable();
        g.setColor(0x25FFFFFF);
        g.setCornerRadius(dp(24));
        g.setStroke(dp(1), 0x35FFFFFF);
        return g;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        theme = new ThemeStore(this);
        performance = new PerformanceStore(this);
        build();
    }

    private void build() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(36), dp(18), dp(18));
        root.setBackgroundColor(theme.isLight() ? 0xFFF4F6FA : 0xFF08090D);

        TextView title = tv("Wallpapers", 26);
        title.setTypeface(null, 1);
        root.addView(title, new LinearLayout.LayoutParams(-1, dp(54)));

        TextView sub = tv("Text-free Liquid Glass collection", 13);
        sub.setTextColor(theme.isLight() ? 0x88111318 : 0xAAFFFFFF);
        root.addView(sub, new LinearLayout.LayoutParams(-1, dp(34)));

        ScrollView scroll = new ScrollView(this);
        scroll.setClipToPadding(false);
        LinearLayout list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);

        String[] names = {
                "Aurora Glass",
                "Midnight Neon",
                "Cosmic Purple",
                "Ocean Glass",
                "Eclipse Glow"
        };

        for (int i = 0; i < names.length; i++) {
            final int index = i;
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(10), dp(10), dp(10), dp(10));
            card.setBackground(glass());

            ImageView preview = new ImageView(this);
            preview.setScaleType(ImageView.ScaleType.CENTER_CROP);
            preview.setImageBitmap(createWallpaper(index, 720, 1280));
            card.addView(preview, new LinearLayout.LayoutParams(-1, dp(260)));

            TextView label = tv(names[i], 15);
            label.setPadding(dp(4), 0, dp(4), 0);
            card.addView(label, new LinearLayout.LayoutParams(-1, dp(48)));

            TextView apply = tv("Apply Wallpaper", 14);
            apply.setGravity(Gravity.CENTER);
            apply.setBackground(glass());
            apply.setOnClickListener(v -> {
                PremiumHomeMotion.press(v, performance.isLiteMode());
                applyWallpaper(index);
            });
            card.addView(apply, new LinearLayout.LayoutParams(-1, dp(48)));

            LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(-1, dp(366));
            cp.bottomMargin = dp(14);
            list.addView(card, cp);
        }

        scroll.addView(list);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        TextView close = tv("Done", 15);
        close.setGravity(Gravity.CENTER);
        close.setBackground(glass());
        close.setOnClickListener(v -> finish());
        LinearLayout.LayoutParams closeLp = new LinearLayout.LayoutParams(-1, dp(56));
        closeLp.topMargin = dp(10);
        root.addView(close, closeLp);

        setContentView(root);
    }

    private Bitmap createWallpaper(int index, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        int[] colors;
        switch (index) {
            case 1:
                colors = new int[]{0xFF03050D, 0xFF17174A, 0xFF090E1D};
                break;
            case 2:
                colors = new int[]{0xFF07030E, 0xFF42165E, 0xFF130B35};
                break;
            case 3:
                colors = new int[]{0xFF020C14, 0xFF075A78, 0xFF121A5C};
                break;
            case 4:
                colors = new int[]{0xFF010207, 0xFF151021, 0xFF090B16};
                break;
            default:
                colors = new int[]{0xFF040712, 0xFF24115E, 0xFF4B174D};
                break;
        }

        paint.setShader(new LinearGradient(
                0, 0, width, height, colors, null, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, width, height, paint);

        // Soft glass-like light blooms. No text, logos or watermark are drawn.
        float[][] positions = {
                {0.18f, 0.20f, 0.46f},
                {0.82f, 0.30f, 0.40f},
                {0.36f, 0.78f, 0.52f},
                {0.84f, 0.82f, 0.44f}
        };
        int[] glowColors = {
                0x66A78BFF,
                0x55FF4FB3,
                0x4435D8FF,
                0x44FFFFFF
        };

        for (int i = 0; i < positions.length; i++) {
            float x = width * positions[i][0];
            float y = height * positions[i][1];
            float radius = width * positions[i][2];
            paint.setShader(new RadialGradient(
                    x, y, radius, glowColors[i], 0x00000000, Shader.TileMode.CLAMP));
            canvas.drawCircle(x, y, radius, paint);
        }

        paint.setShader(null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(width * 0.006f);
        paint.setColor(0x12FFFFFF);
        canvas.drawCircle(width * 0.18f, height * 0.55f, width * 0.30f, paint);
        canvas.drawCircle(width * 0.86f, height * 0.48f, width * 0.24f, paint);
        paint.setStyle(Paint.Style.FILL);

        return bitmap;
    }

    private void applyWallpaper(int index) {
        Bitmap bitmap = createWallpaper(index, 1080, 1920);
        try {
            WallpaperManager.getInstance(this).setBitmap(bitmap);
            Toast.makeText(this, "Wallpaper applied", Toast.LENGTH_SHORT).show();
        } catch (IOException | SecurityException e) {
            Toast.makeText(this, "Unable to apply wallpaper", Toast.LENGTH_SHORT).show();
        } finally {
            bitmap.recycle();
        }
    }
}
