package com.offxos.launcher;

import android.app.Activity;
import android.app.WallpaperManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;

public class WallpaperActivity extends Activity {
    int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
    TextView tv(String s,float z){TextView t=new TextView(this);t.setText(s);t.setTextColor(Color.WHITE);t.setTextSize(z);t.setGravity(Gravity.CENTER_VERTICAL);return t;}
    GradientDrawable glass(){GradientDrawable g=new GradientDrawable();g.setColor(0x25FFFFFF);g.setCornerRadius(dp(24));g.setStroke(dp(1),0x35FFFFFF);return g;}
    @Override public void onCreate(Bundle b){super.onCreate(b); build();}
    void build(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(18),dp(42),dp(18),dp(18));root.setBackgroundColor(0xFF08090D);
        TextView title=tv("Premium Wallpapers",26);title.setTypeface(null,1);root.addView(title,new LinearLayout.LayoutParams(-1,dp(54)));
        TextView sub=tv("8 OffxOS Liquid Glass designs • tap to apply",13);sub.setTextColor(0xAAFFFFFF);root.addView(sub,new LinearLayout.LayoutParams(-1,dp(34)));
        int[] ids={R.drawable.wallpaper_premium_01_aurora,R.drawable.wallpaper_premium_02_midnight,R.drawable.wallpaper_premium_03_violet,R.drawable.wallpaper_premium_04_ocean,R.drawable.wallpaper_premium_05_eclipse,R.drawable.wallpaper_premium_06_rose,R.drawable.wallpaper_premium_07_crystal,R.drawable.wallpaper_premium_08_nebula};
        String[] names={"Aurora Glass","Midnight Neon","Violet Prism","Ocean Glass","Eclipse Glow","Rose Glass","Crystal Frost","Nebula Glass"};
        for(int i=0;i<ids.length;i++) addWallpaper(root,ids[i],names[i]);
        TextView reset=tv("Reset to Dynamic OffxOS",15);reset.setGravity(Gravity.CENTER);reset.setBackground(glass());reset.setOnClickListener(v->{WallpaperStore.clear(this);finish();});LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,dp(58));rp.topMargin=dp(10);root.addView(reset,rp);
        setContentView(root);
    }
    void addWallpaper(LinearLayout root,int id,String name){
        LinearLayout card=new LinearLayout(this);card.setGravity(Gravity.CENTER_VERTICAL);card.setPadding(dp(10),dp(8),dp(12),dp(8));card.setBackground(glass());
        ImageView preview=new ImageView(this);preview.setImageResource(id);preview.setScaleType(ImageView.ScaleType.CENTER_CROP);card.addView(preview,new LinearLayout.LayoutParams(dp(92),dp(92)));
        TextView label=tv(name,16);label.setPadding(dp(16),0,0,0);card.addView(label,new LinearLayout.LayoutParams(0,dp(92),1));
        card.setOnClickListener(v->{WallpaperStore.set(this,id);try{WallpaperManager.getInstance(this).setResource(id);}catch(Exception ignored){}Toast.makeText(this,name+" applied",Toast.LENGTH_SHORT).show();finish();});
        LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,dp(108));cp.bottomMargin=dp(10);root.addView(card,cp);
    }
}
