package com.offxos.launcher;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Entry screen shown only when OffxOS is not the active Android Home app. */
public class SetupActivity extends Activity {
    int dp(float n) { return (int)(n * getResources().getDisplayMetrics().density + .5f); }

    GradientDrawable glass(float radius) {
        GradientDrawable g = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{0x665A35B8, 0x553E8CFF, 0x4434D9C6, 0x664D245F});
        g.setCornerRadius(dp(radius));
        g.setStroke(dp(1), 0x66FFFFFF);
        return g;
    }

    TextView text(String value, float size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER);
        return t;
    }

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.BLACK);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        showSetup();
    }

    @Override protected void onResume() {
        super.onResume();
        if (DefaultLauncherHelper.isDefault(this)) {
            openHome();
        }
    }

    void openHome() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    void showSetup() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(dp(28), dp(32), dp(28), dp(28));
        root.setBackgroundColor(0xFF080A12);

        TextView logo = text("♡", 64, Color.WHITE);
        logo.setBackground(glass(32));
        root.addView(logo, new LinearLayout.LayoutParams(dp(118), dp(118)));

        TextView title = text("OFFXOS", 30, Color.WHITE);
        title.setTypeface(null, 1);
        LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(-1, dp(48));
        tp.topMargin = dp(24);
        root.addView(title, tp);

        TextView sub = text("Liquid Glass Launcher", 15, 0xBFFFFFFF);
        root.addView(sub, new LinearLayout.LayoutParams(-1, dp(30)));

        TextView info = text("OffxOS will show your apps only after\nyou choose it as your Default Home app.", 14, 0x99FFFFFF);
        LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(-1, dp(64));
        ip.topMargin = dp(14);
        root.addView(info, ip);

        TextView button = text("Set OffxOS as Default Launcher", 15, Color.WHITE);
        button.setTypeface(null, 1);
        button.setBackground(glass(24));
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(-1, dp(58));
        bp.topMargin = dp(22);
        root.addView(button, bp);

        TextView settings = text("Open Home settings", 13, 0xCCFFFFFF);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(-1, dp(48));
        sp.topMargin = dp(8);
        root.addView(settings, sp);

        TextView status = text(DefaultLauncherHelper.isSupported()
                ? "Default Home is not OffxOS"
                : "Choose OffxOS from your device Home settings", 12, 0x88FFFFFF);
        LinearLayout.LayoutParams stp = new LinearLayout.LayoutParams(-1, dp(38));
        stp.topMargin = dp(12);
        root.addView(status, stp);

        button.setOnClickListener(v -> requestDefault());
        settings.setOnClickListener(v -> openHomeSettings());
        setContentView(root);
    }

    void requestDefault() {
        if (DefaultLauncherHelper.isDefault(this)) {
            openHome();
            return;
        }
        if (DefaultLauncherHelper.isSupported() && DefaultLauncherHelper.request(this)) return;
        openHomeSettings();
    }

    void openHomeSettings() {
        try {
            startActivity(new Intent(Settings.ACTION_HOME_SETTINGS));
        } catch (Exception ignored) {
            try { startActivity(new Intent(Settings.ACTION_SETTINGS)); } catch (Exception ignored2) {}
        }
    }
}
