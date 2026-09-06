package com.offxos.launcher;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;

/** Premium OffxOS lock-screen style preview. */
public class LockScreenActivity extends Activity {
    int dp(float n){ return (int)(n*getResources().getDisplayMetrics().density+.5f); }
    TextView text(String s,float size){ TextView t=new TextView(this); t.setText(s); t.setTextColor(Color.WHITE); t.setTextSize(size); t.setGravity(Gravity.CENTER); return t; }
    GradientDrawable glass(){ GradientDrawable g=new GradientDrawable(); g.setColor(0x24FFFFFF); g.setCornerRadius(dp(28)); g.setStroke(dp(1),0x32FFFFFF); return g; }
    final Handler handler=new Handler();
    TextView clock,date;
    final Runnable ticker=new Runnable(){ public void run(){
        if(clock!=null) clock.setText(new SimpleDateFormat("HH:mm",Locale.getDefault()).format(new Date()));
        if(date!=null) date.setText(new SimpleDateFormat("EEEE, d MMMM",Locale.getDefault()).format(new Date()));
        handler.postDelayed(this,1000L);
    }};

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        FrameLayout scene=new FrameLayout(this);
        DynamicWallpaperView wallpaper=new DynamicWallpaperView(this);
        scene.addView(wallpaper,new FrameLayout.LayoutParams(-1,-1));

        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setGravity(Gravity.CENTER_HORIZONTAL); root.setPadding(dp(20),dp(58),dp(20),dp(20));
        root.setBackgroundColor(0x1807080C);
        date=text("",17); date.setAlpha(.9f); root.addView(date,new LinearLayout.LayoutParams(-1,dp(32)));
        clock=text("",78); clock.setTypeface(null,1); root.addView(clock,new LinearLayout.LayoutParams(-1,dp(105)));
        TextView status=text("🔒  OffxOS",13); status.setAlpha(.75f); root.addView(status,new LinearLayout.LayoutParams(-1,dp(35)));
        Space sp=new Space(this); root.addView(sp,new LinearLayout.LayoutParams(1,0,1));

        TextView hint=text("Swipe up to unlock",14); hint.setAlpha(.78f); root.addView(hint,new LinearLayout.LayoutParams(-1,dp(42)));
        LinearLayout bottom=new LinearLayout(this); bottom.setGravity(Gravity.CENTER);
        TextView flash=text("◉",25), camera=text("▣",25);
        for(TextView x:new TextView[]{flash,camera}){ x.setTextColor(Color.WHITE); x.setGravity(Gravity.CENTER); x.setBackground(glass()); x.setContentDescription(x==flash?"Flashlight":"Camera"); x.setOnClickListener(v->PremiumHomeMotion.press(v,false)); LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(dp(58),dp(58)); lp.setMargins(dp(30),0,dp(30),0); bottom.addView(x,lp); }
        root.addView(bottom);
        scene.addView(root,new FrameLayout.LayoutParams(-1,-1));
        setContentView(scene);
        ticker.run();

        final float[] down={0}; root.setOnTouchListener((v,e)->{ if(e.getAction()==MotionEvent.ACTION_DOWN){down[0]=e.getY();return true;} if(e.getAction()==MotionEvent.ACTION_UP){ if(down[0]-e.getY()>120) finish(); return true;} return true; });
    }
    @Override protected void onDestroy(){ handler.removeCallbacks(ticker); super.onDestroy(); }
}
