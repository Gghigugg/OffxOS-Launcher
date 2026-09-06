package com.offxos.launcher;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class LockScreenActivity extends Activity {
    int dp(float n){ return (int)(n*getResources().getDisplayMetrics().density+.5f); }
    TextView text(String s,float size){ TextView t=new TextView(this); t.setText(s); t.setTextColor(Color.WHITE); t.setTextSize(size); t.setGravity(Gravity.CENTER); return t; }
    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setGravity(Gravity.CENTER_HORIZONTAL); root.setPadding(dp(20),dp(58),dp(20),dp(20));
        root.setBackgroundColor(0xFF07080C);
        TextView date=text(new SimpleDateFormat("EEEE, d MMMM",Locale.getDefault()).format(new Date()),17); date.setAlpha(.9f); root.addView(date,new LinearLayout.LayoutParams(-1,dp(32)));
        TextView clock=text(new SimpleDateFormat("HH:mm",Locale.getDefault()).format(new Date()),78); clock.setTypeface(null,1); root.addView(clock,new LinearLayout.LayoutParams(-1,dp(105)));
        TextView status=text("🔒  OffxOS",13); status.setAlpha(.75f); root.addView(status,new LinearLayout.LayoutParams(-1,dp(35)));
        Space sp=new Space(this); root.addView(sp,new LinearLayout.LayoutParams(1,0,1));
        TextView hint=text("Swipe up to unlock",14); hint.setAlpha(.75f); root.addView(hint,new LinearLayout.LayoutParams(-1,dp(42)));
        LinearLayout bottom=new LinearLayout(this); bottom.setGravity(Gravity.CENTER); TextView flash=text("◉",25); TextView camera=text("▣",25); for(TextView x:new TextView[]{flash,camera}){x.setTextColor(Color.WHITE);x.setGravity(Gravity.CENTER);x.setBackgroundColor(0x25FFFFFF);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(dp(58),dp(58));lp.setMargins(dp(30),0,dp(30),0);bottom.addView(x,lp);} root.addView(bottom);
        setContentView(root);
        final float[] down={0}; root.setOnTouchListener((v,e)->{if(e.getAction()==MotionEvent.ACTION_DOWN){down[0]=e.getY();return true;} if(e.getAction()==MotionEvent.ACTION_UP){if(down[0]-e.getY()>120)finish();return true;} return true;});
    }
}
