package com.offxos.launcher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import java.util.*;

public class AppDrawerActivity extends Activity {
    LinearLayout grid;
    EditText search;
    ArrayList<AppInfo> apps=new ArrayList<>();
    int dp(float n){return(int)(n*getResources().getDisplayMetrics().density+.5f);}
    TextView tv(String s,float z){TextView t=new TextView(this);t.setText(s);t.setTextColor(Color.WHITE);t.setTextSize(z);t.setGravity(Gravity.CENTER);return t;}
    GradientDrawable glass(){GradientDrawable g=new GradientDrawable();g.setColor(0x30FFFFFF);g.setCornerRadius(dp(24));g.setStroke(dp(1),0x35FFFFFF);return g;}
    @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(Color.TRANSPARENT);getWindow().setNavigationBarColor(Color.BLACK);build();}
    void build(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(16),dp(30),dp(16),dp(12));root.setBackgroundColor(0xFF08090D);
        LinearLayout head=new LinearLayout(this);head.setGravity(Gravity.CENTER_VERTICAL);
        TextView title=tv("App Library",27);title.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);title.setTypeface(null,1);head.addView(title,new LinearLayout.LayoutParams(0,dp(48),1));
        TextView close=tv("✕",20);close.setBackground(glass());close.setOnClickListener(v->finish());head.addView(close,new LinearLayout.LayoutParams(dp(48),dp(48)));root.addView(head);
        search=new EditText(this);search.setHint("⌕  Search apps");search.setHintTextColor(0xAAFFFFFF);search.setTextColor(Color.WHITE);search.setTextSize(15);search.setSingleLine();search.setPadding(dp(18),0,dp(18),0);search.setBackground(glass());root.addView(search,new LinearLayout.LayoutParams(-1,dp(52)));
        TextView hint=tv("Swipe down to return  •  All installed apps",11);hint.setTextColor(0x88FFFFFF);hint.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);root.addView(hint,new LinearLayout.LayoutParams(-1,dp(30)));
        ScrollView scroll=new ScrollView(this);grid=new LinearLayout(this);grid.setOrientation(LinearLayout.VERTICAL);grid.setPadding(0,dp(6),0,dp(20));scroll.addView(grid);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));setContentView(root);
        loadApps();search.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int a,int c,int d){}public void onTextChanged(CharSequence s,int a,int b,int c){render(s.toString());}public void afterTextChanged(Editable e){}});installSwipe(root);
    }
    void loadApps(){apps.clear();PackageManager pm=getPackageManager();Intent q=new Intent(Intent.ACTION_MAIN);q.addCategory(Intent.CATEGORY_LAUNCHER);List<ResolveInfo> list=pm.queryIntentActivities(q,0);for(ResolveInfo r:list){if(getPackageName().equals(r.activityInfo.packageName))continue;Intent i=pm.getLaunchIntentForPackage(r.activityInfo.packageName);if(i!=null)apps.add(new AppInfo(r.loadLabel(pm).toString(),r.activityInfo.packageName,r.loadIcon(pm),i));}Collections.sort(apps,(a,b)->a.label.compareToIgnoreCase(b.label));render("");}
    void render(String filter){grid.removeAllViews();String f=filter==null?"":filter.toLowerCase(Locale.getDefault());LinearLayout row=null;int shown=0;for(AppInfo a:apps){if(!a.label.toLowerCase(Locale.getDefault()).contains(f))continue;if(shown%4==0){row=new LinearLayout(this);row.setGravity(Gravity.CENTER);grid.addView(row,new LinearLayout.LayoutParams(-1,dp(96)));}addApp(row,a);shown++;}if(shown==0){TextView e=tv("No apps found",15);e.setTextColor(0xAAFFFFFF);grid.addView(e,new LinearLayout.LayoutParams(-1,dp(100)));}}
    void addApp(LinearLayout parent,AppInfo a){LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setGravity(Gravity.CENTER);ImageView iv=new ImageView(this);iv.setImageDrawable(a.icon);iv.setPadding(dp(2),dp(2),dp(2),dp(2));box.addView(iv,new LinearLayout.LayoutParams(dp(54),dp(54)));TextView name=tv(a.label,10);name.setSingleLine();box.addView(name,new LinearLayout.LayoutParams(dp(78),dp(22)));box.setOnClickListener(v->{v.animate().scaleX(.9f).scaleY(.9f).setDuration(70).withEndAction(()->{v.animate().scaleX(1f).scaleY(1f).setDuration(180).start();try{startActivity(a.intent);}catch(Exception ignored){}}).start();});parent.addView(box,new LinearLayout.LayoutParams(0,-1,1));}
    void installSwipe(View target){final float[] sy={0};target.setOnTouchListener((v,e)->{if(e.getAction()==MotionEvent.ACTION_DOWN){sy[0]=e.getY();return false;}if(e.getAction()==MotionEvent.ACTION_UP&&e.getY()-sy[0]>dp(100)){finish();return true;}return false;});}
    static class AppInfo{String label,pkg;Drawable icon;Intent intent;AppInfo(String l,String p,Drawable i,Intent x){label=l;pkg=p;icon=i;intent=x;}}
}
