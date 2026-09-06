package com.offxos.launcher;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.util.List;

public class NotificationCenterActivity extends Activity {
    int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
    GradientDrawable glass(){GradientDrawable g=new GradientDrawable();g.setColor(0x30FFFFFF);g.setCornerRadius(dp(24));g.setStroke(dp(1),0x40FFFFFF);return g;}
    TextView t(String s,float z){TextView v=new TextView(this);v.setText(s);v.setTextColor(Color.WHITE);v.setTextSize(z);v.setGravity(Gravity.CENTER_VERTICAL);return v;}
    @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(Color.TRANSPARENT);getWindow().setNavigationBarColor(Color.BLACK);
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(16),dp(38),dp(16),dp(12));root.setBackgroundColor(0xFF08090D);
        LinearLayout head=new LinearLayout(this);head.setGravity(Gravity.CENTER_VERTICAL);TextView title=t("Notifications",28);title.setTypeface(null,1);head.addView(title,new LinearLayout.LayoutParams(0,dp(50),1));TextView close=t("✕",20);close.setGravity(Gravity.CENTER);close.setBackground(glass());close.setOnClickListener(v->finish());head.addView(close,new LinearLayout.LayoutParams(dp(48),dp(48)));root.addView(head);
        TextView sub=t("OffxOS Notification Center",13);sub.setTextColor(0xAAFFFFFF);root.addView(sub,new LinearLayout.LayoutParams(-1,dp(28)));
        ScrollView scroll=new ScrollView(this);LinearLayout list=new LinearLayout(this);list.setOrientation(LinearLayout.VERTICAL);list.setPadding(0,dp(8),0,dp(8));
        List<OffxNotificationService.Notice> notes=OffxNotificationService.getNotices();
        if(notes.isEmpty()){TextView empty=t("No recent notifications",16);empty.setGravity(Gravity.CENTER);list.addView(empty,new LinearLayout.LayoutParams(-1,dp(180)));}
        else for(OffxNotificationService.Notice n:notes){LinearLayout card=new LinearLayout(this);card.setOrientation(LinearLayout.VERTICAL);card.setPadding(dp(16),dp(12),dp(16),dp(12));card.setBackground(glass());TextView a=t(n.title,16);a.setTypeface(null,1);card.addView(a,new LinearLayout.LayoutParams(-1,dp(28)));TextView body=t(n.text,13);body.setTextColor(0xCCFFFFFF);card.addView(body,new LinearLayout.LayoutParams(-1,dp(34)));TextView app=t(n.app,10);app.setTextColor(0x88FFFFFF);card.addView(app,new LinearLayout.LayoutParams(-1,dp(20)));LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,dp(96));cp.setMargins(0,dp(6),0,dp(6));list.addView(card,cp);}
        scroll.addView(list);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));setContentView(root);
    }
}
