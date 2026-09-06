package com.offxos.launcher;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
    LinearLayout grid,chips;
    EditText search;
    ArrayList<AppInfo> apps=new ArrayList<>();
    String category="All";
    SharedPreferences recent;
    int dp(float n){return(int)(n*getResources().getDisplayMetrics().density+.5f);}
    TextView tv(String s,float z){TextView t=new TextView(this);t.setText(s);t.setTextColor(Color.WHITE);t.setTextSize(z);t.setGravity(Gravity.CENTER);return t;}
    GradientDrawable glass(){GradientDrawable g=new GradientDrawable();g.setColor(0x30FFFFFF);g.setCornerRadius(dp(24));g.setStroke(dp(1),0x35FFFFFF);return g;}
    GradientDrawable chipBg(boolean selected){GradientDrawable g=new GradientDrawable();g.setColor(selected?0x66FFFFFF:0x22FFFFFF);g.setCornerRadius(dp(22));g.setStroke(dp(1),selected?0x70FFFFFF:0x25FFFFFF);return g;}
    @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(Color.TRANSPARENT);getWindow().setNavigationBarColor(Color.BLACK);recent=getSharedPreferences("offx_recent",MODE_PRIVATE);build();}
    void build(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(16),dp(30),dp(16),dp(12));root.setBackgroundColor(0xFF08090D);
        LinearLayout head=new LinearLayout(this);head.setGravity(Gravity.CENTER_VERTICAL);
        TextView title=tv("App Library",27);title.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);title.setTypeface(null,1);head.addView(title,new LinearLayout.LayoutParams(0,dp(48),1));
        TextView close=tv("✕",20);close.setBackground(glass());close.setOnClickListener(v->finish());head.addView(close,new LinearLayout.LayoutParams(dp(48),dp(48)));root.addView(head);
        search=new EditText(this);search.setHint("⌕  Search apps");search.setHintTextColor(0xAAFFFFFF);search.setTextColor(Color.WHITE);search.setTextSize(15);search.setSingleLine();search.setPadding(dp(18),0,dp(18),0);search.setBackground(glass());root.addView(search,new LinearLayout.LayoutParams(-1,dp(52)));
        HorizontalScrollView hs=new HorizontalScrollView(this);hs.setHorizontalScrollBarEnabled(false);chips=new LinearLayout(this);chips.setGravity(Gravity.CENTER_VERTICAL);chips.setPadding(0,dp(6),0,dp(4));hs.addView(chips,new HorizontalScrollView.LayoutParams(-2,dp(48)));root.addView(hs,new LinearLayout.LayoutParams(-1,dp(54)));
        TextView hint=tv("Swipe down to return  •  Smart App Library",11);hint.setTextColor(0x88FFFFFF);hint.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);root.addView(hint,new LinearLayout.LayoutParams(-1,dp(30)));
        ScrollView scroll=new ScrollView(this);grid=new LinearLayout(this);grid.setOrientation(LinearLayout.VERTICAL);grid.setPadding(0,dp(6),0,dp(20));scroll.addView(grid);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));setContentView(root);
        buildChips();loadApps();
        search.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int a,int c,int d){}public void onTextChanged(CharSequence s,int a,int b,int c){render(s.toString());}public void afterTextChanged(Editable e){}});
        installSwipe(root);
    }
    void buildChips(){chips.removeAllViews();String[] cs={"All","Recent","Social","Media","Tools","Games","System"};for(String c:cs){TextView b=tv(c,12);b.setPadding(dp(16),0,dp(16),0);b.setBackground(chipBg(c.equals(category)));b.setOnClickListener(v->{category=c;buildChips();render(search.getText().toString());});LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-2,dp(40));lp.setMargins(0,dp(2),dp(8),dp(2));chips.addView(b,lp);}}
    void loadApps(){apps.clear();PackageManager pm=getPackageManager();Intent q=new Intent(Intent.ACTION_MAIN);q.addCategory(Intent.CATEGORY_LAUNCHER);List<ResolveInfo> list=pm.queryIntentActivities(q,0);for(ResolveInfo r:list){if(getPackageName().equals(r.activityInfo.packageName))continue;Intent i=pm.getLaunchIntentForPackage(r.activityInfo.packageName);if(i!=null)apps.add(new AppInfo(r.loadLabel(pm).toString(),r.activityInfo.packageName,r.loadIcon(pm),i));}Collections.sort(apps,(a,b)->a.label.compareToIgnoreCase(b.label));render("");}
    boolean matches(AppInfo a,String f){if(!a.label.toLowerCase(Locale.getDefault()).contains(f))return false;if(category.equals("All"))return true;if(category.equals("Recent"))return recent.getLong(a.pkg,0)>0;if(category.equals("Social"))return cat(a).equals("Social");if(category.equals("Media"))return cat(a).equals("Media");if(category.equals("Tools"))return cat(a).equals("Tools");if(category.equals("Games"))return cat(a).equals("Games");return cat(a).equals("System");}
    String cat(AppInfo a){String s=(a.label+" "+a.pkg).toLowerCase(Locale.getDefault());if(s.matches(".*(whatsapp|instagram|facebook|messenger|telegram|snapchat|twitter|x[.]com|discord|reddit|linkedin).*"))return "Social";if(s.matches(".*(youtube|spotify|music|video|mx player|netflix|prime video|gallery|photos|camera).*"))return "Media";if(s.matches(".*(game|play games|pubg|free fire|bgmi|minecraft|roblox|candy|clash|asphalt).*"))return "Games";if(s.matches(".*(settings|phone|contacts|messages|clock|calendar|calculator|files|file manager|security|browser|chrome).*"))return "System";return "Tools";}
    ArrayList<AppInfo> filtered(String f){ArrayList<AppInfo> out=new ArrayList<>();for(AppInfo a:apps)if(matches(a,f))out.add(a);if(category.equals("Recent"))Collections.sort(out,(a,b)->Long.compare(recent.getLong(b.pkg,0),recent.getLong(a.pkg,0)));return out;}
    void render(String filter){grid.removeAllViews();String f=filter==null?"":filter.toLowerCase(Locale.getDefault());ArrayList<AppInfo> shown=filtered(f);if(shown.isEmpty()){TextView e=tv(category.equals("Recent")?"No recently opened apps":"No apps found",15);e.setTextColor(0xAAFFFFFF);grid.addView(e,new LinearLayout.LayoutParams(-1,dp(100)));return;}if(category.equals("Recent")&&f.isEmpty()){TextView t=tv("Recently opened",13);t.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);t.setTextColor(0x88FFFFFF);grid.addView(t,new LinearLayout.LayoutParams(-1,dp(30)));}LinearLayout row=null;for(int i=0;i<shown.size();i++){if(i%4==0){row=new LinearLayout(this);row.setGravity(Gravity.CENTER);grid.addView(row,new LinearLayout.LayoutParams(-1,dp(96)));}addApp(row,shown.get(i));}}
    void addApp(LinearLayout parent,AppInfo a){LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setGravity(Gravity.CENTER);ImageView iv=new ImageView(this);iv.setImageDrawable(a.icon);iv.setPadding(dp(2),dp(2),dp(2),dp(2));box.addView(iv,new LinearLayout.LayoutParams(dp(54),dp(54)));TextView name=tv(a.label,10);name.setSingleLine();box.addView(name,new LinearLayout.LayoutParams(dp(78),dp(22)));box.setOnClickListener(v->{recent.edit().putLong(a.pkg,System.currentTimeMillis()).apply();v.animate().scaleX(.9f).scaleY(.9f).setDuration(70).withEndAction(()->{v.animate().scaleX(1f).scaleY(1f).setDuration(180).start();try{startActivity(a.intent);}catch(Exception ignored){}}).start();});parent.addView(box,new LinearLayout.LayoutParams(0,-1,1));}
    void installSwipe(View target){final float[] sy={0};target.setOnTouchListener((v,e)->{if(e.getAction()==MotionEvent.ACTION_DOWN){sy[0]=e.getY();return false;}if(e.getAction()==MotionEvent.ACTION_UP&&e.getY()-sy[0]>dp(100)){finish();return true;}return false;});}
    static class AppInfo{String label,pkg;Drawable icon;Intent intent;AppInfo(String l,String p,Drawable i,Intent x){label=l;pkg=p;icon=i;intent=x;}}
}
