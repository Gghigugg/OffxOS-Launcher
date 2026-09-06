package com.offxos.launcher;

import android.app.*;import android.os.*;import android.provider.Settings;import android.content.*;import android.content.pm.*;import android.graphics.*;import android.graphics.drawable.GradientDrawable;import android.view.*;import android.view.inputmethod.InputMethodManager;import android.widget.*;import java.util.*;

public class MainActivity extends Activity {
    LinearLayout root, grid, dock; EditText search; ArrayList<AppInfo> apps=new ArrayList<>();
    int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
    TextView tv(String s,float size){ TextView t=new TextView(this);t.setText(s);t.setTextColor(Color.WHITE);t.setTextSize(size);t.setGravity(Gravity.CENTER);return t; }
    GradientDrawable bg(int color,float r){GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(dp(r));return g;}
    @Override public void onCreate(Bundle b){super.onCreate(b); getWindow().setStatusBarColor(Color.TRANSPARENT);getWindow().setNavigationBarColor(Color.BLACK);build();}
    void build(){
        root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(14),dp(22),dp(14),dp(12));root.setBackgroundColor(Color.rgb(9,9,15));
        LinearLayout top=new LinearLayout(this);top.setGravity(Gravity.CENTER_VERTICAL);TextView logo=tv("OFFXOS",22);logo.setTypeface(null,1);top.addView(logo,new LinearLayout.LayoutParams(0,dp(52),1));
        TextView cc=tv("◉",28);cc.setOnClickListener(v->controlCenter());top.addView(cc,new LinearLayout.LayoutParams(dp(52),dp(52)));root.addView(top);
        search=new EditText(this);search.setHint("Search apps");search.setHintTextColor(0x99FFFFFF);search.setTextColor(Color.WHITE);search.setSingleLine();search.setPadding(dp(18),0,dp(18),0);search.setBackground(bg(0x22FFFFFF,28));root.addView(search,new LinearLayout.LayoutParams(-1,dp(48)));
        grid=new LinearLayout(this);grid.setOrientation(LinearLayout.VERTICAL);root.addView(grid,new LinearLayout.LayoutParams(-1,0,1));
        dock=new LinearLayout(this);dock.setGravity(Gravity.CENTER);dock.setPadding(dp(8),dp(8),dp(8),dp(8));dock.setBackground(bg(0x28FFFFFF,30));root.addView(dock,new LinearLayout.LayoutParams(-1,dp(78)));
        setContentView(root);loadApps(); search.addTextChangedListener(new android.text.TextWatcher(){public void beforeTextChanged(CharSequence s,int a,int c,int d){}public void onTextChanged(CharSequence s,int a,int b,int c){render(s.toString());}public void afterTextChanged(android.text.Editable e){}});
    }
    static class AppInfo{String label;Drawable icon;Intent intent;AppInfo(String l,Drawable i,Intent x){label=l;icon=i;intent=x;}}
    void loadApps(){apps.clear();PackageManager pm=getPackageManager();Intent q=new Intent(Intent.ACTION_MAIN);q.addCategory(Intent.CATEGORY_LAUNCHER);List<ResolveInfo> list=pm.queryIntentActivities(q,0);for(ResolveInfo r:list){if(getPackageName().equals(r.activityInfo.packageName))continue;apps.add(new AppInfo(r.loadLabel(pm).toString(),r.loadIcon(pm),pm.getLaunchIntentForPackage(r.activityInfo.packageName)));}Collections.sort(apps,(a,b)->a.label.compareToIgnoreCase(b.label));render("");
        dock.removeAllViews();for(int i=0;i<Math.min(4,apps.size());i++) addApp(dock,apps.get(i),0);
    }
    void render(String filter){grid.removeAllViews();ArrayList<AppInfo> shown=new ArrayList<>();for(AppInfo a:apps)if(a.label.toLowerCase().contains(filter.toLowerCase()))shown.add(a);int cols=4;LinearLayout row=null;for(int i=0;i<shown.size();i++){if(i%cols==0){row=new LinearLayout(this);row.setGravity(Gravity.CENTER);grid.addView(row,new LinearLayout.LayoutParams(-1,dp(96)));}addApp(row,shown.get(i),1);}}
    void addApp(LinearLayout parent,AppInfo a,int mode){LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setGravity(Gravity.CENTER);ImageView iv=new ImageView(this);iv.setImageDrawable(a.icon);box.addView(iv,new LinearLayout.LayoutParams(dp(mode==0?48:50),dp(mode==0?48:50)));TextView name=tv(a.label,mode==0?9:10);name.setSingleLine();box.addView(name,new LinearLayout.LayoutParams(dp(76),dp(22)));box.setOnClickListener(v->{try{startActivity(a.intent);}catch(Exception e){}});parent.addView(box,new LinearLayout.LayoutParams(0,-1,1));}
    void controlCenter(){
        final Dialog d=new Dialog(this);LinearLayout p=new LinearLayout(this);p.setOrientation(LinearLayout.VERTICAL);p.setPadding(dp(18),dp(18),dp(18),dp(18));p.setBackground(bg(0xEE171722,30));
        TextView h=tv("OffxOS Control Center",22);h.setGravity(Gravity.LEFT);h.setTypeface(null,1);p.addView(h,new LinearLayout.LayoutParams(-1,dp(48)));
        LinearLayout r=new LinearLayout(this);String[] x={"Wi‑Fi","Bluetooth","Flashlight","Airplane"};for(String s:x){TextView b=tv(s,12);b.setBackground(bg(0x32FFFFFF,20));b.setOnClickListener(v->{if(s.equals("Flashlight"))toggleFlash();else if(s.equals("Wi‑Fi"))startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));else if(s.equals("Bluetooth"))startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));else startActivity(new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS));});r.addView(b,new LinearLayout.LayoutParams(0,dp(72),1));}p.addView(r);
        SeekBar vol=new SeekBar(this);vol.setMax(15);android.media.AudioManager am=(android.media.AudioManager)getSystemService(AUDIO_SERVICE);vol.setProgress(am.getStreamVolume(android.media.AudioManager.STREAM_MUSIC));vol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar b,int v,boolean f){am.setStreamVolume(android.media.AudioManager.STREAM_MUSIC,v,0);}public void onStartTrackingTouch(SeekBar b){}public void onStopTrackingTouch(SeekBar b){}});p.addView(tv("Volume",12));p.addView(vol);
        TextView n=tv("🔔  Notifications  •  "+OffxNotificationService.count,16);n.setGravity(Gravity.LEFT);n.setPadding(0,dp(14),0,dp(14));n.setOnClickListener(v->startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")));p.addView(n);d.setContentView(p);Window w=d.getWindow();if(w!=null){w.setBackgroundDrawableResource(android.R.color.transparent);w.setLayout(-1,-2);}d.show();w=d.getWindow();if(w!=null)w.setLayout(-1,-2);
    }
    void toggleFlash(){try{android.hardware.camera2.CameraManager cm=(android.hardware.camera2.CameraManager)getSystemService(CAMERA_SERVICE);String id=cm.getCameraIdList()[0];cm.setTorchMode(id,true);Toast.makeText(this,"Flashlight ON",Toast.LENGTH_SHORT).show();}catch(Exception e){Toast.makeText(this,"Flashlight unavailable",Toast.LENGTH_SHORT).show();}}
}
