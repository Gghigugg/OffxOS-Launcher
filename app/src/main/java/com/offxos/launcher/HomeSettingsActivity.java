package com.offxos.launcher;

import android.app.*;import android.content.*;import android.graphics.Color;import android.graphics.drawable.GradientDrawable;import android.os.Bundle;import android.view.Gravity;import android.widget.*;

public class HomeSettingsActivity extends Activity{
 int dp(float n){return(int)(n*getResources().getDisplayMetrics().density+.5f);}
 ThemeStore theme; PerformanceStore performance;
 TextView tv(String s,float z){TextView t=new TextView(this);t.setText(s);t.setTextColor(Color.WHITE);t.setTextSize(z);t.setGravity(Gravity.CENTER_VERTICAL);return t;}
 GradientDrawable glass(){GradientDrawable g=new GradientDrawable();g.setColor(0x25FFFFFF);g.setCornerRadius(dp(24));g.setStroke(dp(1),0x35FFFFFF);return g;}
 @Override public void onCreate(Bundle b){super.onCreate(b);theme=new ThemeStore(this);performance=new PerformanceStore(this);build();}
 void build(){LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(18),dp(40),dp(18),dp(18));root.setBackgroundColor(theme.isLight()?0xFFF4F6FA:0xFF08090D);
 TextView title=tv("Home Screen",26);title.setTypeface(null,1);root.addView(title,new LinearLayout.LayoutParams(-1,dp(54)));
 TextView sub=tv("Customize your OffxOS layout",13);sub.setTextColor(theme.isLight()?0x88111318:0xAAFFFFFF);root.addView(sub,new LinearLayout.LayoutParams(-1,dp(34)));
 root.addView(section("Grid Columns"));
 String[] cols={"3 Columns","4 Columns","5 Columns"};RadioGroup rg=new RadioGroup(this);rg.setOrientation(RadioGroup.VERTICAL);int saved=getSharedPreferences("offx_home",0).getInt("columns",4);for(String s:cols){RadioButton r=new RadioButton(this);r.setText(s);r.setTextColor(theme.isLight()?0xFF111318:Color.WHITE);r.setTextSize(15);r.setTag(Integer.parseInt(s.substring(0,1)));r.setChecked((int)r.getTag()==saved);rg.addView(r,new RadioGroup.LayoutParams(-1,dp(48)));}rg.setOnCheckedChangeListener((g,id)->{RadioButton r=g.findViewById(id);if(r!=null)getSharedPreferences("offx_home",0).edit().putInt("columns",(int)r.getTag()).apply();});root.addView(rg,new LinearLayout.LayoutParams(-1,dp(155)));
 root.addView(section("Icon Size"));SeekBar size=new SeekBar(this);size.setMax(24);size.setProgress(getSharedPreferences("offx_home",0).getInt("icon_offset",4));size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar s,int p,boolean f){getSharedPreferences("offx_home",0).edit().putInt("icon_offset",p).apply();}public void onStartTrackingTouch(SeekBar s){}public void onStopTrackingTouch(SeekBar s){}});root.addView(size,new LinearLayout.LayoutParams(-1,dp(50)));
 TextView hint=tv("Small  •  Standard  •  Large",12);hint.setTextColor(theme.isLight()?0x88111318:0x88FFFFFF);root.addView(hint,new LinearLayout.LayoutParams(-1,dp(30)));
 TextView lite=tv(performance.isLiteMode()?"⚡ Lite Mode: ON":"⚡ Lite Mode: OFF",15);lite.setBackground(glass());lite.setPadding(dp(16),0,dp(16),0);lite.setOnClickListener(v->{performance.setLiteMode(!performance.isLiteMode());lite.setText(performance.isLiteMode()?"⚡ Lite Mode: ON":"⚡ Lite Mode: OFF");});root.addView(lite,new LinearLayout.LayoutParams(-1,dp(58)));
 root.addView(section("Default Launcher"));
 TextView defaultLauncher=tv(DefaultLauncherHelper.isDefault(this)?"✓ OffxOS is Default Launcher":"Set OffxOS as Default Launcher",15);defaultLauncher.setBackground(glass());defaultLauncher.setPadding(dp(16),0,dp(16),0);defaultLauncher.setOnClickListener(v->{if(DefaultLauncherHelper.isDefault(this)){Toast.makeText(this,"OffxOS is already your default launcher",Toast.LENGTH_SHORT).show();return;}if(DefaultLauncherHelper.request(this)){Toast.makeText(this,"Choose OffxOS Launcher in the system prompt",Toast.LENGTH_SHORT).show();}else{Toast.makeText(this,"Default launcher selection is unavailable on this Android version",Toast.LENGTH_SHORT).show();}});root.addView(defaultLauncher,new LinearLayout.LayoutParams(-1,dp(58)));
 TextView close=tv("Done",15);close.setGravity(Gravity.CENTER);close.setBackground(glass());close.setOnClickListener(v->finish());LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,dp(58));cp.topMargin=dp(14);root.addView(close,cp);setContentView(root);}
 @Override protected void onResume(){super.onResume();if(DefaultLauncherHelper.isDefault(this)){TextView v=findDefaultButton();if(v!=null)v.setText("✓ OffxOS is Default Launcher");}}
 TextView findDefaultButton(){LinearLayout root=(LinearLayout)findViewById(android.R.id.content);if(root==null)return null;return null;}
 TextView section(String s){TextView t=tv(s,13);t.setTextColor(theme.isLight()?0x88111318:0xAAFFFFFF);t.setPadding(dp(4),dp(12),0,0);return t;}
}
