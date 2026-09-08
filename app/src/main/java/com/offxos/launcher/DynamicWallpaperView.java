package com.offxos.launcher;

import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.appwidget.AppWidgetHostView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

/** Dynamic Liquid Glass wallpaper plus the persistent Home widget layer. */
public class DynamicWallpaperView extends FrameLayout {
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final PerformanceStore performance;
    private final ThemeStore theme;
    private final OffxWidgetHost widgetHost;
    private final HorizontalScrollView widgetScroll;
    private final LinearLayout widgetLayer;
    private float phase=0f;
    private long lastFrame=0L;
    private boolean widgetsStarted=false;

    public DynamicWallpaperView(Context c){
        super(c); performance=new PerformanceStore(c); theme=new ThemeStore(c); widgetHost=new OffxWidgetHost(c);
        widgetScroll=new HorizontalScrollView(c); widgetScroll.setHorizontalScrollBarEnabled(false); widgetScroll.setClipChildren(false); widgetScroll.setClipToPadding(false); widgetScroll.setFadingEdgeLength(dp(22)); widgetScroll.setHorizontalFadingEdgeEnabled(true); widgetScroll.setPadding(dp(10),dp(4),dp(10),dp(4));
        widgetLayer=new LinearLayout(c); widgetLayer.setOrientation(LinearLayout.HORIZONTAL); widgetLayer.setGravity(Gravity.CENTER_VERTICAL); widgetLayer.setClipChildren(false); widgetLayer.setClipToPadding(false);
        widgetScroll.addView(widgetLayer,new HorizontalScrollView.LayoutParams(-2,-1)); addView(widgetScroll,new FrameLayout.LayoutParams(-1,dp(190),Gravity.BOTTOM));
        setLayerType(View.LAYER_TYPE_HARDWARE,null); setWillNotDraw(false);
    }
    int dp(float n){return(int)(n*getResources().getDisplayMetrics().density+.5f);}
    @Override protected void onAttachedToWindow(){super.onAttachedToWindow();startWidgets();}
    private void startWidgets(){if(widgetsStarted)return;try{widgetHost.startListening();widgetsStarted=true;restoreWidgets();}catch(Exception ignored){}}
    private void stopWidgets(){if(!widgetsStarted)return;try{widgetHost.stopListening();}catch(Exception ignored){}widgetsStarted=false;}
    private void restoreWidgets(){
        widgetLayer.removeAllViews();
        ArrayList<Integer> ids=OffxHomeWidgetStore.load(getContext());
        OffxHomeWidgetOrderStore.sync(getContext(),ids);
        ArrayList<Integer> ordered=OffxHomeWidgetOrderStore.load(getContext());
        AppWidgetManager manager=AppWidgetManager.getInstance(getContext());
        int index=0;
        for(int id:ordered){
            AppWidgetProviderInfo info=manager.getAppWidgetInfo(id);
            if(info==null){try{widgetHost.deleteAppWidgetId(id);}catch(Exception ignored){} OffxHomeWidgetStore.remove(getContext(),id); OffxHomeWidgetLayoutStore.remove(getContext(),id); OffxHomeWidgetOrderStore.remove(getContext(),id); continue;}
            try{addHomeWidget(id,info,index++);}catch(Exception ignored){}}
        widgetScroll.setVisibility(widgetLayer.getChildCount()==0?View.GONE:View.VISIBLE);
    }
    private void addHomeWidget(int id,AppWidgetProviderInfo info,int index){
        final int size=OffxHomeWidgetLayoutStore.size(getContext(),id); int height=size==0?118:(size==2?174:146); int width=(int)(getResources().getDisplayMetrics().widthPixels*.82f);
        LinearLayout card=new LinearLayout(getContext()); card.setOrientation(LinearLayout.VERTICAL); card.setPadding(dp(8),dp(8),dp(8),dp(8)); card.setBackground(widgetGlass()); card.setElevation(dp(8)); card.setOnClickListener(v->PremiumHomeMotion.press(card,performance.isLiteMode()));
        AppWidgetHostView view=widgetHost.createView(getContext(),id,info); view.setPadding(dp(2),dp(2),dp(2),dp(2)); view.setOnLongClickListener(v->{showWidgetEditor(id);return true;}); card.addView(view,new LinearLayout.LayoutParams(-1,0,1));
        TextView edit=widgetLabel("•••  Widget options  •••"); edit.setOnClickListener(v->showWidgetEditor(id)); card.addView(edit,new LinearLayout.LayoutParams(-1,dp(28)));
        LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(width,dp(height)); cp.setMargins(dp(6),dp(8),dp(6),dp(8)); widgetLayer.addView(card,cp); PremiumHomeMotion.entrance(card,index,performance.isLiteMode());
        if(android.os.Build.VERSION.SDK_INT>=31){try{java.util.ArrayList<android.util.SizeF> sizes=new java.util.ArrayList<>(); sizes.add(new android.util.SizeF(width/getResources().getDisplayMetrics().density,height/getResources().getDisplayMetrics().density)); view.updateAppWidgetSize(new android.os.Bundle(),sizes);}catch(Exception ignored){}}
        else{try{view.updateAppWidgetSize(new android.os.Bundle(),dp(120),dp(80),width,dp(190));}catch(Exception ignored){}}
    }
    private TextView widgetLabel(String text){TextView t=new TextView(getContext());t.setText(text);t.setTextColor(theme.isLight()?0x99000000:0xAAFFFFFF);t.setTextSize(10);t.setGravity(Gravity.CENTER);t.setAllCaps(false);return t;}
    private void showWidgetEditor(int id){
        final String[] items={"Move Left","Move Right","Compact widget","Standard widget","Large widget","Remove widget"};
        new AlertDialog.Builder(getContext()).setTitle("OffxOS Widget").setItems(items,(d,which)->{
            if(which==0){OffxHomeWidgetOrderStore.move(getContext(),id,-1);restoreWidgets();}
            else if(which==1){OffxHomeWidgetOrderStore.move(getContext(),id,1);restoreWidgets();}
            else if(which<=4){OffxHomeWidgetLayoutStore.setSize(getContext(),id,which-2);restoreWidgets();}
            else{try{widgetHost.deleteAppWidgetId(id);}catch(Exception ignored){} OffxHomeWidgetStore.remove(getContext(),id); OffxHomeWidgetLayoutStore.remove(getContext(),id); OffxHomeWidgetOrderStore.remove(getContext(),id); restoreWidgets();}
        }).show();
    }
    private android.graphics.drawable.GradientDrawable widgetGlass(){android.graphics.drawable.GradientDrawable g=new android.graphics.drawable.GradientDrawable();g.setColor(theme.isLight()?0xEAFBFCFF:0xCC151821);g.setCornerRadius(dp(26));g.setStroke(dp(1),theme.isLight()?0x66FFFFFF:0x45FFFFFF);return g;}
    @Override protected void onDraw(Canvas c){
        super.onDraw(c); int w=getWidth(),h=getHeight(); if(w<=0||h<=0)return; boolean light=theme.isLight();
        int selected=WallpaperStore.get(getContext());
        if(selected!=0){
            Drawable drawable=getResources().getDrawable(selected,null);
            drawable.setBounds(0,0,w,h);
            drawable.draw(c);
            return;
        }
        c.drawColor(light?0xFFF4F6FA:0xFF08090D);
        if(performance.isLiteMode()){paint.setShader(null);removeCallbacks(invalidateTask);return;} long now=System.currentTimeMillis(); if(lastFrame!=0L){long elapsed=now-lastFrame;phase+=Math.min(elapsed,120L)*0.000075f;}else phase+=0.006f; lastFrame=now;
        float x1=w*(0.30f+0.12f*(float)Math.sin(phase)); float y1=h*(0.28f+0.10f*(float)Math.cos(phase*1.2f)); float x2=w*(0.72f+0.10f*(float)Math.cos(phase*.8f)); float y2=h*(0.70f+0.10f*(float)Math.sin(phase));
        int c1=light?0x553C8DFF:0x663C8DFF; int c2=light?0x55FF2F92:0x66FF2F92; RadialGradient g1=new RadialGradient(x1,y1,w*.55f,new int[]{c1,light?0x182F6BFF:0x223C8DFF,0x00000000},new float[]{0f,.45f,1f},Shader.TileMode.CLAMP); paint.setShader(g1);c.drawRect(0,0,w,h,paint);
        RadialGradient g2=new RadialGradient(x2,y2,w*.50f,new int[]{c2,light?0x182F6BFF:0x222F6BFF,0x00000000},new float[]{0f,.45f,1f},Shader.TileMode.CLAMP); paint.setShader(g2);c.drawRect(0,0,w,h,paint);paint.setShader(null);postDelayed(invalidateTask,90);
    }
    private final Runnable invalidateTask=new Runnable(){@Override public void run(){invalidate();}};
    @Override protected void onDetachedFromWindow(){removeCallbacks(invalidateTask);lastFrame=0L;stopWidgets();super.onDetachedFromWindow();}
}
