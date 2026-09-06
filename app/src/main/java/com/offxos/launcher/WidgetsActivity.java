package com.offxos.launcher;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.util.*;

/** OffxOS widget picker/host screen. */
public class WidgetsActivity extends Activity {
    static final int REQ_PICK = 7001;
    static final int REQ_BIND = 7002;
    static final int REQ_CONFIG = 7003;
    OffxWidgetHost host;
    LinearLayout list;
    int pendingId = AppWidgetManager.INVALID_APPWIDGET_ID;

    int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
    GradientDrawable glass(){GradientDrawable g=new GradientDrawable();g.setColor(0x25FFFFFF);g.setCornerRadius(dp(24));g.setStroke(dp(1),0x35FFFFFF);return g;}
    TextView tv(String s,float z){TextView t=new TextView(this);t.setText(s);t.setTextColor(Color.WHITE);t.setTextSize(z);t.setGravity(Gravity.CENTER_VERTICAL);return t;}

    @Override public void onCreate(Bundle b){super.onCreate(b);host=new OffxWidgetHost(this);build();host.startListening();restoreWidgets();}
    void build(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(18),dp(36),dp(18),dp(18));root.setBackgroundColor(0xFF08090D);
        LinearLayout bar=new LinearLayout(this);bar.setGravity(Gravity.CENTER_VERTICAL);
        TextView title=tv("Widgets",25);title.setTypeface(null,1);bar.addView(title,new LinearLayout.LayoutParams(0,dp(52),1));
        TextView add=tv("＋",30);add.setGravity(Gravity.CENTER);add.setBackground(glass());add.setOnClickListener(v->pickWidget());bar.addView(add,new LinearLayout.LayoutParams(dp(52),dp(52)));
        root.addView(bar);
        TextView hint=tv("Add Android widgets to your OffxOS Home",13);hint.setTextColor(0xAAFFFFFF);root.addView(hint,new LinearLayout.LayoutParams(-1,dp(40)));
        ScrollView scroll=new ScrollView(this);list=new LinearLayout(this);list.setOrientation(LinearLayout.VERTICAL);scroll.addView(list);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        TextView done=tv("Done",15);done.setGravity(Gravity.CENTER);done.setBackground(glass());done.setOnClickListener(v->finish());root.addView(done,new LinearLayout.LayoutParams(-1,dp(58)));
        setContentView(root);
    }
    void pickWidget(){
        Intent i=new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pendingId=host.allocateAppWidgetId();i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,pendingId);
        startActivityForResult(i,REQ_PICK);
    }
    @Override protected void onActivityResult(int requestCode,int resultCode,Intent data){super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==REQ_PICK){
            if(resultCode!=RESULT_OK){deletePending();return;}
            AppWidgetManager m=AppWidgetManager.getInstance(this);AppWidgetProviderInfo info=m.getAppWidgetInfo(pendingId);
            if(info==null){deletePending();return;}
            if(m.bindAppWidgetIdIfAllowed(pendingId,info.provider))
                finishBinding(info);
            else {Intent bind=new Intent(AppWidgetManager.ACTION_APPWIDGET_BIND);bind.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,pendingId);bind.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER,info.provider);startActivityForResult(bind,REQ_BIND);}
        } else if(requestCode==REQ_BIND){
            if(resultCode==RESULT_OK){AppWidgetProviderInfo info=AppWidgetManager.getInstance(this).getAppWidgetInfo(pendingId);if(info!=null)finishBinding(info);else deletePending();}
            else deletePending();
        } else if(requestCode==REQ_CONFIG){
            if(resultCode==RESULT_OK)showWidget(pendingId);
            else {host.deleteAppWidgetId(pendingId);removeSaved(pendingId);}
            pendingId=AppWidgetManager.INVALID_APPWIDGET_ID;
        }
    }
    void finishBinding(AppWidgetProviderInfo info){
        if(info.configure!=null){Intent c=new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);c.setComponent(info.configure);c.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,pendingId);startActivityForResult(c,REQ_CONFIG);}
        else showWidget(pendingId);
    }
    void showWidget(int id){
        AppWidgetProviderInfo info=AppWidgetManager.getInstance(this).getAppWidgetInfo(id);if(info==null){deletePending();return;}
        AppWidgetHostViewHolder holder=new AppWidgetHostViewHolder(this,id,info);list.addView(holder.view,new LinearLayout.LayoutParams(-1,dp(170)));
        saveId(id);pendingId=AppWidgetManager.INVALID_APPWIDGET_ID;
    }
    void restoreWidgets(){for(int id:loadIds()){AppWidgetProviderInfo info=AppWidgetManager.getInstance(this).getAppWidgetInfo(id);if(info!=null)showRestored(id,info);else host.deleteAppWidgetId(id);}}
    void showRestored(int id,AppWidgetProviderInfo info){android.appwidget.AppWidgetHostView v=host.createView(this,id,info);v.setPadding(dp(8),dp(8),dp(8),dp(8));v.setBackground(glass());TextView remove=tv("Remove widget",12);remove.setGravity(Gravity.CENTER);remove.setOnClickListener(x->{host.deleteAppWidgetId(id);removeSaved(id);list.removeView((View)x.getTag());});LinearLayout wrap=new LinearLayout(this);wrap.setOrientation(LinearLayout.VERTICAL);wrap.addView(v,new LinearLayout.LayoutParams(-1,0,1));remove.setTag(wrap);wrap.addView(remove,new LinearLayout.LayoutParams(-1,dp(34)));list.addView(wrap,new LinearLayout.LayoutParams(-1,dp(210)));}
    void deletePending(){if(pendingId!=AppWidgetManager.INVALID_APPWIDGET_ID)host.deleteAppWidgetId(pendingId);pendingId=AppWidgetManager.INVALID_APPWIDGET_ID;}
    void saveId(int id){Set<String> s=getPreferences(0).getStringSet("ids",new LinkedHashSet<>());Set<String> n=new LinkedHashSet<>(s);n.add(String.valueOf(id));getPreferences(0).edit().putStringSet("ids",n).apply();}
    void removeSaved(int id){Set<String> n=new LinkedHashSet<>(getPreferences(0).getStringSet("ids",new LinkedHashSet<>()));n.remove(String.valueOf(id));getPreferences(0).edit().putStringSet("ids",n).apply();}
    ArrayList<Integer> loadIds(){ArrayList<Integer> out=new ArrayList<>();for(String s:getPreferences(0).getStringSet("ids",new LinkedHashSet<>()))try{out.add(Integer.parseInt(s));}catch(Exception ignored){}return out;}
    @Override protected void onDestroy(){try{host.stopListening();}catch(Exception ignored){}super.onDestroy();}
    static final class AppWidgetHostViewHolder { android.appwidget.AppWidgetHostView view; AppWidgetHostViewHolder(WidgetsActivity a,int id,AppWidgetProviderInfo info){view=a.host.createView(a,id,info);view.setPadding(a.dp(8),a.dp(8),a.dp(8),a.dp(8));view.setBackground(a.glass());} }
}
