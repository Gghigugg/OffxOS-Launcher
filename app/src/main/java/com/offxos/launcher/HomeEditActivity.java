package com.offxos.launcher;

import android.app.*;import android.content.*;import android.graphics.Color;import android.graphics.drawable.GradientDrawable;import android.os.Bundle;import android.view.*;import android.widget.*;import java.util.*;

/** Real Home Screen edit mode: drag apps/folders and persist their order. */
public class HomeEditActivity extends Activity {
    ThemeStore theme; PerformanceStore performance; FolderStore folders; GridLayout grid; ArrayList<String> order=new ArrayList<>();
    int dp(float n){return(int)(n*getResources().getDisplayMetrics().density+.5f);}
    TextView tv(String s,float z){TextView t=new TextView(this);t.setText(s);t.setTextColor(theme.isLight()?0xFF111318:Color.WHITE);t.setTextSize(z);t.setGravity(Gravity.CENTER);return t;}
    GradientDrawable glass(){GradientDrawable g=new GradientDrawable();g.setColor(theme.isLight()?0xCCFFFFFF:0x35FFFFFF);g.setCornerRadius(dp(24));g.setStroke(dp(1),theme.isLight()?0x55FFFFFF:0x35FFFFFF);return g;}
    @Override public void onCreate(Bundle b){super.onCreate(b);theme=new ThemeStore(this);performance=new PerformanceStore(this);folders=new FolderStore(this);build();}
    void build(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(16),dp(30),dp(16),dp(16));root.setBackgroundColor(theme.isLight()?0xFFF4F6FA:0xFF08090D);
        LinearLayout head=new LinearLayout(this);head.setGravity(Gravity.CENTER_VERTICAL);
        TextView title=tv("Edit Home Screen",24);title.setTypeface(null,1);head.addView(title,new LinearLayout.LayoutParams(0,dp(52),1));
        TextView done=tv("Done",14);done.setBackground(glass());head.addView(done,new LinearLayout.LayoutParams(dp(78),dp(48)));root.addView(head);
        TextView hint=tv("Long press an icon or folder • Drag to reorder",12);hint.setTextColor(theme.isLight()?0x88111318:0xAAFFFFFF);root.addView(hint,new LinearLayout.LayoutParams(-1,dp(38)));
        grid=new GridLayout(this);grid.setAlignmentMode(GridLayout.ALIGN_BOUNDS);grid.setUseDefaultMargins(false);grid.setColumnCount(columns());grid.setOnDragListener((v,e)->handleDrop(e));
        root.addView(grid,new LinearLayout.LayoutParams(-1,0,1));
        done.setOnClickListener(v->{PremiumHomeMotion.press(v,performance.isLiteMode());v.postDelayed(this::finish,performance.isLiteMode()?70L:130L);});
        setContentView(root);reload();
    }
    int columns(){int c=getSharedPreferences("offx_home",0).getInt("columns",4);return c<3||c>5?4:c;}
    void reload(){ArrayList<String> valid=new ArrayList<>();PackageManager pm=getPackageManager();Intent q=new Intent(Intent.ACTION_MAIN);q.addCategory(Intent.CATEGORY_LAUNCHER);for(android.content.pm.ResolveInfo r:pm.queryIntentActivities(q,0)){if(getPackageName().equals(r.activityInfo.packageName))continue;valid.add("app:"+r.activityInfo.packageName);}for(FolderStore.Folder f:folders.getFolders())valid.add("folder:"+f.name);order=HomeLayoutStore.sync(this,valid);render();}
    void render(){grid.removeAllViews();grid.setColumnCount(columns());int width=getResources().getDisplayMetrics().widthPixels-dp(32);int cell=Math.max(dp(72),width/columns());int i=0;for(String key:order){View item=createItem(key,i);GridLayout.LayoutParams lp=new GridLayout.LayoutParams();lp.width=cell;lp.height=dp(104);lp.columnSpec=GridLayout.spec(i%columns());lp.rowSpec=GridLayout.spec(i/columns());grid.addView(item,lp);i++;}}
    View createItem(String key,int index){LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setGravity(Gravity.CENTER);box.setPadding(dp(4),dp(6),dp(4),dp(6));box.setBackground(glass());box.setTag(key);
        TextView icon;String label;
        if(key.startsWith("folder:")){icon=tv("▦",30);label=key.substring(7);}else{String pkg=key.substring(4);android.content.pm.ApplicationInfo ai=null;try{ai=getPackageManager().getApplicationInfo(pkg,0);}catch(Exception ignored){}if(ai!=null){ImageView iv=new ImageView(this);iv.setImageDrawable(getPackageManager().getApplicationIcon(ai));iv.setPadding(dp(3),dp(3),dp(3),dp(3));box.addView(iv,new LinearLayout.LayoutParams(dp(52),dp(52)));label=getPackageManager().getApplicationLabel(ai).toString();TextView name=tv(label,10);name.setSingleLine();box.addView(name,new LinearLayout.LayoutParams(-1,dp(26)));attachDrag(box,key,index);return box;}icon=tv("?",28);label=pkg;}
        box.addView(icon,new LinearLayout.LayoutParams(dp(52),dp(52)));TextView name=tv(label,10);name.setSingleLine();box.addView(name,new LinearLayout.LayoutParams(-1,dp(26)));attachDrag(box,key,index);return box;}
    void attachDrag(View v,String key,int index){v.setOnLongClickListener(view->{ClipData data=ClipData.newPlainText("offx-home-item",key);View.DragShadowBuilder shadow=new View.DragShadowBuilder(view);boolean started;if(android.os.Build.VERSION.SDK_INT>=24)started=view.startDragAndDrop(data,shadow,view,View.DRAG_FLAG_OPAQUE);else started=view.startDrag(data,shadow,view,0);if(started){view.setAlpha(.35f);view.setScaleX(.94f);view.setScaleY(.94f);}return true;});}
    boolean handleDrop(DragEvent e){if(e.getAction()==DragEvent.ACTION_DRAG_STARTED)return e.getClipDescription()!=null&&e.getClipDescription().hasMimeType("text/plain");if(e.getAction()==DragEvent.ACTION_DRAG_ENTERED){grid.setAlpha(.94f);return true;}if(e.getAction()==DragEvent.ACTION_DRAG_EXITED){grid.setAlpha(1f);return true;}if(e.getAction()==DragEvent.ACTION_DROP){String key=e.getClipData().getItemAt(0).getText().toString();int from=order.indexOf(key);if(from<0)return false;int target=nearestIndex(e.getX(),e.getY());order.remove(from);if(target>from)target--;if(target<0)target=0;if(target>order.size())target=order.size();order.add(target,key);HomeLayoutStore.save(this,order);grid.setAlpha(1f);render();return true;}if(e.getAction()==DragEvent.ACTION_DRAG_ENDED){grid.setAlpha(1f);return true;}return true;}
    int nearestIndex(float x,float y){int best=order.size();float dist=Float.MAX_VALUE;for(int i=0;i<grid.getChildCount();i++){View v=grid.getChildAt(i);float cx=v.getLeft()+v.getWidth()/2f,cy=v.getTop()+v.getHeight()/2f;float d=(cx-x)*(cx-x)+(cy-y)*(cy-y);if(d<dist){dist=d;best=i;}}return best;}
}
