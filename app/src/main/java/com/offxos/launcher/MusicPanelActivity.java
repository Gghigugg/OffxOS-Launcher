package com.offxos.launcher;

import android.app.*;import android.content.*;import android.graphics.Color;import android.graphics.drawable.GradientDrawable;import android.media.AudioManager;import android.os.Bundle;import android.view.Gravity;import android.widget.*;

public class MusicPanelActivity extends Activity{
 int dp(float n){return(int)(n*getResources().getDisplayMetrics().density+.5f);}
 TextView tv(String s,float z){TextView t=new TextView(this);t.setText(s);t.setTextColor(Color.WHITE);t.setTextSize(z);t.setGravity(Gravity.CENTER);return t;}
 GradientDrawable glass(){GradientDrawable g=new GradientDrawable();g.setColor(0x30FFFFFF);g.setCornerRadius(dp(28));g.setStroke(dp(1),0x45FFFFFF);return g;}
 public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(Color.TRANSPARENT);getWindow().setNavigationBarColor(Color.BLACK);
  LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(18),dp(42),dp(18),dp(18));root.setBackgroundColor(0xFF08090D);
  TextView h=tv("Music",28);h.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);h.setTypeface(null,1);root.addView(h,new LinearLayout.LayoutParams(-1,dp(55)));
  LinearLayout card=new LinearLayout(this);card.setOrientation(LinearLayout.VERTICAL);card.setGravity(Gravity.CENTER);card.setPadding(dp(18),dp(24),dp(18),dp(24));card.setBackground(glass());
  TextView art=tv("♫",64);art.setBackground(glass());card.addView(art,new LinearLayout.LayoutParams(dp(120),dp(120)));
  TextView title=tv("OffxOS Music",20);title.setTypeface(null,1);card.addView(title,new LinearLayout.LayoutParams(-1,dp(40)));
  TextView sub=tv("Choose your music app",13);sub.setTextColor(0xAAFFFFFF);card.addView(sub,new LinearLayout.LayoutParams(-1,dp(28)));
  Button open= new Button(this);open.setText("Open Music");open.setOnClickListener(v->{Intent i=new Intent(Intent.ACTION_MAIN);i.addCategory(Intent.CATEGORY_APP_MUSIC);try{startActivity(i);}catch(Exception e){Intent s=new Intent(Intent.ACTION_VIEW);s.setType("audio/*");startActivity(s);}});card.addView(open,new LinearLayout.LayoutParams(-1,dp(52)));
  root.addView(card,new LinearLayout.LayoutParams(-1,dp(310)));
  TextView vol=tv("Volume",14);vol.setGravity(Gravity.LEFT);root.addView(vol,new LinearLayout.LayoutParams(-1,dp(35)));
  SeekBar sb=new SeekBar(this);AudioManager am=(AudioManager)getSystemService(AUDIO_SERVICE);sb.setMax(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));sb.setProgress(am.getStreamVolume(AudioManager.STREAM_MUSIC));sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar s,int p,boolean f){am.setStreamVolume(AudioManager.STREAM_MUSIC,p,0);}public void onStartTrackingTouch(SeekBar s){}public void onStopTrackingTouch(SeekBar s){}});root.addView(sb);
  setContentView(root);
 }
}
