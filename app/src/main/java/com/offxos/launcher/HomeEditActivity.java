package com.offxos.launcher;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;

/** Liquid Glass Home Screen edit mode. */
public class HomeEditActivity extends Activity {
    ThemeStore theme; PerformanceStore performance;
    int dp(float n){return(int)(n*getResources().getDisplayMetrics().density+.5f);}
    TextView tv(String s,float z){TextView t=new TextView(this);t.setText(s);t.setTextColor(theme.isLight()?0xFF111318:Color.WHITE);t.setTextSize(z);t.setGravity(Gravity.CENTER_VERTICAL);return t;}
    GradientDrawable glass(){GradientDrawable g=new GradientDrawable();g.setColor(theme.isLight()?0xCCFFFFFF:0x35FFFFFF);g.setCornerRadius(dp(24));g.setStroke(dp(1),theme.isLight()?0x55FFFFFF:0x35FFFFFF);return g;}
    @Override public void onCreate(Bundle b){super.onCreate(b);theme=new ThemeStore(this);performance=new PerformanceStore(this);build();}
    void build(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(18),dp(34),dp(18),dp(18));root.setBackgroundColor(theme.isLight()?0xFFF4F6FA:0xFF08090D);
        LinearLayout head=new LinearLayout(this);head.setGravity(Gravity.CENTER_VERTICAL);
        TextView title=tv("Edit Home Screen",25);title.setTypeface(null,1);head.addView(title,new LinearLayout.LayoutParams(0,dp(52),1));
        TextView done=tv("Done",14);done.setGravity(Gravity.CENTER);done.setBackground(glass());head.addView(done,new LinearLayout.LayoutParams(dp(76),dp(48)));root.addView(head);
        TextView sub=tv("Liquid Glass • Arrange & personalize",12);sub.setTextColor(theme.isLight()?0x88111318:0xAAFFFFFF);root.addView(sub,new LinearLayout.LayoutParams(-1,dp(32)));
        TextView gridTitle=tv("Grid",13);gridTitle.setTextColor(theme.isLight()?0x88111318:0xAAFFFFFF);root.addView(gridTitle,new LinearLayout.LayoutParams(-1,dp(34)));
        RadioGroup rg=new RadioGroup(this);rg.setOrientation(RadioGroup.HORIZONTAL);String[] cols={"3","4","5"};int saved=getSharedPreferences("offx_home",0).getInt("columns",4);for(String s:cols){RadioButton r=new RadioButton(this);r.setText(s+" columns");r.setTextColor(theme.isLight()?0xFF111318:Color.WHITE);r.setTextSize(13);r.setTag(Integer.parseInt(s));r.setChecked((int)r.getTag()==saved);rg.addView(r,new RadioGroup.LayoutParams(0,dp(48),1));}rg.setOnCheckedChangeListener((g,id)->{RadioButton r=g.findViewById(id);if(r!=null)getSharedPreferences("offx_home",0).edit().putInt("columns",(int)r.getTag()).apply();});root.addView(rg,new LinearLayout.LayoutParams(-1,dp(54)));
        TextView iconTitle=tv("Icon Size",13);iconTitle.setTextColor(theme.isLight()?0x88111318:0xAAFFFFFF);root.addView(iconTitle,new LinearLayout.LayoutParams(-1,dp(34)));
        SeekBar size=new SeekBar(this);size.setMax(24);size.setProgress(getSharedPreferences("offx_home",0).getInt("icon_offset",4));size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar s,int p,boolean f){getSharedPreferences("offx_home",0).edit().putInt("icon_offset",p).apply();}public void onStartTrackingTouch(SeekBar s){}public void onStopTrackingTouch(SeekBar s){}});root.addView(size,new LinearLayout.LayoutParams(-1,dp(48)));
        TextView sizeHint=tv("Small   •   Standard   •   Large",11);sizeHint.setTextColor(theme.isLight()?0x88111318:0x88FFFFFF);root.addView(sizeHint,new LinearLayout.LayoutParams(-1,dp(28)));
        TextView widgets=tv("🧩  Manage Widgets",15);widgets.setBackground(glass());widgets.setPadding(dp(16),0,dp(16),0);widgets.setOnClickListener(v->{PremiumHomeMotion.press(v,performance.isLiteMode());startActivity(new Intent(this,WidgetsActivity.class));});root.addView(widgets,new LinearLayout.LayoutParams(-1,dp(58)));
        TextView motion=tv(performance.isLiteMode()?"⚡  Lite Motion ON":"✨  Liquid Glass Motion ON",15);motion.setBackground(glass());motion.setPadding(dp(16),0,dp(16),0);motion.setOnClickListener(v->{performance.setLiteMode(!performance.isLiteMode());motion.setText(performance.isLiteMode()?"⚡  Lite Motion ON":"✨  Liquid Glass Motion ON");});LinearLayout.LayoutParams mp=new LinearLayout.LayoutParams(-1,dp(58));mp.topMargin=dp(10);root.addView(motion,mp);
        TextView arrange=tv("↕  App & Folder arrangement is saved on Home",13);arrange.setTextColor(theme.isLight()?0x88111318:0x88FFFFFF);arrange.setPadding(dp(4),dp(12),0,0);root.addView(arrange,new LinearLayout.LayoutParams(-1,dp(46)));
        done.setOnClickListener(v->{PremiumHomeMotion.press(v,performance.isLiteMode());v.postDelayed(this::finish,performance.isLiteMode()?70L:130L);});
        setContentView(root);
    }
}
