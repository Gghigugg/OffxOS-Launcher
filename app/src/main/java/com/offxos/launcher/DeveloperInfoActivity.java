package com.offxos.launcher;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;

public class DeveloperInfoActivity extends Activity {
    int dp(float n){ return (int)(n*getResources().getDisplayMetrics().density+.5f); }
    TextView tv(String s,float z){ TextView t=new TextView(this); t.setText(s); t.setTextColor(Color.WHITE); t.setTextSize(z); t.setGravity(Gravity.CENTER_VERTICAL); return t; }
    GradientDrawable glass(){ GradientDrawable g=new GradientDrawable(); g.setColor(0x28FFFFFF); g.setCornerRadius(dp(24)); g.setStroke(dp(1),0x42FFFFFF); return g; }
    TextView card(String text){ TextView t=tv(text,15); t.setBackground(glass()); t.setPadding(dp(16),0,dp(16),0); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(58)); p.topMargin=dp(10); return t; }
    @Override public void onCreate(Bundle b){ super.onCreate(b); build(); }
    void build(){
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(18),dp(42),dp(18),dp(20)); root.setBackgroundColor(0xFF08090D);
        TextView back=tv("‹  Developer Info",22); back.setTypeface(null,1); back.setOnClickListener(v->finish()); root.addView(back,new LinearLayout.LayoutParams(-1,dp(60)));
        TextView logo=tv("◉",54); logo.setGravity(Gravity.CENTER); logo.setTextColor(0xFFE7D8FF); root.addView(logo,new LinearLayout.LayoutParams(-1,dp(100)));
        TextView name=tv("OffxOS Launcher",24); name.setGravity(Gravity.CENTER); name.setTypeface(null,1); root.addView(name,new LinearLayout.LayoutParams(-1,dp(42)));
        TextView sub=tv("Liquid Glass Android Launcher",13); sub.setGravity(Gravity.CENTER); sub.setTextColor(0xAAFFFFFF); root.addView(sub,new LinearLayout.LayoutParams(-1,dp(30)));
        TextView insta=card("◎  Instagram   @offx.somesh"); insta.setOnClickListener(v->open("https://www.instagram.com/offx.somesh/")); root.addView(insta);
        TextView mail=card("✉  Email   Someshkoli442288@gmail.com"); mail.setOnClickListener(v->{Intent i=new Intent(Intent.ACTION_SENDTO,Uri.parse("mailto:Someshkoli442288@gmail.com"));startActivity(i);}); root.addView(mail);
        TextView version=card("ⓘ  Version   1.0"); root.addView(version);
        TextView about=tv("Built with a premium Liquid Glass visual system.\nSmooth • Minimal • iOS-inspired",13); about.setGravity(Gravity.CENTER); about.setTextColor(0x99FFFFFF); LinearLayout.LayoutParams ap=new LinearLayout.LayoutParams(-1,dp(80));ap.topMargin=dp(18);root.addView(about,ap);
        setContentView(root);
    }
    void open(String u){try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(u)));}catch(Exception ignored){}}
}
