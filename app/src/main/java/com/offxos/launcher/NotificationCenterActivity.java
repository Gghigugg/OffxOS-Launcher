package com.offxos.launcher;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationCenterActivity extends Activity {
    int dp(float n){return(int)(n*getResources().getDisplayMetrics().density+.5f);}

    GradientDrawable glass(){
        GradientDrawable g=new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{0x78FFFFFF,0x45B98CFF,0x35FF4FD8,0x28FFFFFF});
        g.setCornerRadius(dp(28));
        g.setStroke(dp(1),0x88FFFFFF);
        return g;
    }
    GradientDrawable chip(){
        GradientDrawable g=new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{0x66FFFFFF,0x28FFFFFF});
        g.setCornerRadius(dp(22)); g.setStroke(dp(1),0x66FFFFFF); return g;
    }
    TextView t(String s,float z){
        TextView v=new TextView(this); v.setText(s); v.setTextColor(Color.WHITE); v.setTextSize(z);
        v.setGravity(Gravity.CENTER_VERTICAL); return v;
    }

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        build();
    }

    void build(){
        FrameLayout scene=new FrameLayout(this);
        GradientDrawable bg=new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{0xFF060810,0xFF24113E,0xFF102746,0xFF080A10});
        scene.setBackground(bg);
        GlassBackdrop.soften(scene,dp(7));

        LinearLayout root=new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16),dp(42),dp(16),dp(14));
        GradientDrawable panel=new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{0x82FFFFFF,0x48202A48,0x343E235A,0x28FFFFFF});
        panel.setCornerRadius(dp(32)); panel.setStroke(dp(1),0x77FFFFFF);
        root.setBackground(panel); root.setElevation(dp(18));
        scene.addView(root,new FrameLayout.LayoutParams(-1,-1));

        LinearLayout head=new LinearLayout(this); head.setGravity(Gravity.CENTER_VERTICAL);
        TextView title=t("Notifications",28); title.setTypeface(null,1);
        head.addView(title,new LinearLayout.LayoutParams(0,dp(50),1));
        TextView count=t(String.valueOf(OffxNotificationService.getNotices().size()),13); count.setGravity(Gravity.CENTER); count.setBackground(chip());
        LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(dp(42),dp(42)); cp.setMargins(0,0,dp(7),0); head.addView(count,cp);
        TextView clear=t("Clear",13); clear.setGravity(Gravity.CENTER); clear.setBackground(chip());
        clear.setOnClickListener(v->{OffxNotificationService.clearFromCenter(this);build();});
        LinearLayout.LayoutParams clp=new LinearLayout.LayoutParams(dp(62),dp(42)); clp.setMargins(0,0,dp(7),0); head.addView(clear,clp);
        TextView close=t("✕",20); close.setGravity(Gravity.CENTER); close.setBackground(chip()); close.setOnClickListener(v->finish());
        head.addView(close,new LinearLayout.LayoutParams(dp(48),dp(48))); root.addView(head);

        TextView sub=t("OffxOS Liquid Glass  •  swipe left to dismiss",12); sub.setTextColor(0xCCFFFFFF); root.addView(sub,new LinearLayout.LayoutParams(-1,dp(30)));
        ScrollView scroll=new ScrollView(this); scroll.setClipToPadding(false);
        LinearLayout list=new LinearLayout(this); list.setOrientation(LinearLayout.VERTICAL); list.setPadding(0,dp(8),0,dp(18));
        List<OffxNotificationService.Notice> notes=OffxNotificationService.getNotices();
        if(notes.isEmpty()){
            TextView empty=t("No recent notifications\n\nEnable Notification Access in OffxOS Settings to receive live notifications here.",16);
            empty.setGravity(Gravity.CENTER); empty.setTextColor(0xCCFFFFFF); empty.setBackground(glass());
            list.addView(empty,new LinearLayout.LayoutParams(-1,dp(230)));
        } else for(OffxNotificationService.Notice n:notes)addCard(list,n);
        scroll.addView(list); root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        TextView hint=t("Swipe down from the top-left of Home for Notification Center  •  top-right for Control Center",11);
        hint.setGravity(Gravity.CENTER); hint.setTextColor(0xAAFFFFFF); root.addView(hint,new LinearLayout.LayoutParams(-1,dp(34)));
        setContentView(scene);
    }

    void addCard(LinearLayout list,OffxNotificationService.Notice n){
        LinearLayout card=new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16),dp(13),dp(16),dp(11)); card.setBackground(glass()); card.setElevation(dp(6));
        TextView a=t(n.title,16); a.setTypeface(null,1); card.addView(a,new LinearLayout.LayoutParams(-1,dp(27)));
        TextView body=t(n.text,13); body.setTextColor(0xEEFFFFFF); body.setMaxLines(3); card.addView(body,new LinearLayout.LayoutParams(-1,dp(45)));
        TextView app=t(n.app+"  •  "+new SimpleDateFormat("HH:mm",Locale.getDefault()).format(new Date(n.when)),10); app.setTextColor(0xB8FFFFFF); card.addView(app,new LinearLayout.LayoutParams(-1,dp(20)));
        final float[] down={0};
        card.setOnTouchListener((v,e)->{
            if(e.getAction()==MotionEvent.ACTION_DOWN){down[0]=e.getX();return true;}
            if(e.getAction()==MotionEvent.ACTION_MOVE){float dx=e.getX()-down[0];if(dx<0){v.setTranslationX(dx);v.setAlpha(Math.max(.25f,1f+dx/dp(220)));}return true;}
            if(e.getAction()==MotionEvent.ACTION_UP){float dx=e.getX()-down[0];if(dx<-dp(90))v.animate().translationX(-card.getWidth()).alpha(0).setDuration(180).withEndAction(()->{OffxNotificationService.dismissFromCenter(this,n.key);build();}).start();else v.animate().translationX(0).alpha(1).setDuration(160).start();return true;}
            if(e.getAction()==MotionEvent.ACTION_CANCEL){v.animate().translationX(0).alpha(1).setDuration(160).start();return true;} return true;
        });
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(110)); lp.setMargins(0,dp(7),0,dp(7)); list.addView(card,lp);
    }
}
