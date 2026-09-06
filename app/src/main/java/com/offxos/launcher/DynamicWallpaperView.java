package com.offxos.launcher;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.graphics.*;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.appwidget.AppWidgetHostView;
import java.util.ArrayList;

/** Dynamic Liquid Glass wallpaper plus the persistent Home widget layer. */
public class DynamicWallpaperView extends FrameLayout {
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final PerformanceStore performance;
    private final ThemeStore theme;
    private final OffxWidgetHost widgetHost;
    private final FrameLayout widgetLayer;
    private float phase=0f;
    private long lastFrame=0L;
    private boolean widgetsStarted=false;

    public DynamicWallpaperView(Context c){
        super(c);
        performance=new PerformanceStore(c);
        theme=new ThemeStore(c);
        widgetHost=new OffxWidgetHost(c);
        widgetLayer=new FrameLayout(c);
        widgetLayer.setClipChildren(false);
        widgetLayer.setClipToPadding(false);
        widgetLayer.setPadding(dp(14),dp(6),dp(14),dp(6));
        addView(widgetLayer,new FrameLayout.LayoutParams(-1,dp(190),Gravity.BOTTOM));
        setLayerType(View.LAYER_TYPE_HARDWARE,null);
        setWillNotDraw(false);
    }

    int dp(float n){return(int)(n*getResources().getDisplayMetrics().density+.5f);}

    @Override protected void onAttachedToWindow(){
        super.onAttachedToWindow();
        startWidgets();
    }

    private void startWidgets(){
        if(widgetsStarted)return;
        try{widgetHost.startListening();widgetsStarted=true;restoreWidgets();}catch(Exception ignored){}
    }

    private void stopWidgets(){
        if(!widgetsStarted)return;
        try{widgetHost.stopListening();}catch(Exception ignored){}
        widgetsStarted=false;
    }

    private void restoreWidgets(){
        widgetLayer.removeAllViews();
        ArrayList<Integer> ids=OffxHomeWidgetStore.load(getContext());
        AppWidgetManager manager=AppWidgetManager.getInstance(getContext());
        int shown=0;
        for(int id:ids){
            AppWidgetProviderInfo info=manager.getAppWidgetInfo(id);
            if(info==null){
                try{widgetHost.deleteAppWidgetId(id);}catch(Exception ignored){}
                OffxHomeWidgetStore.remove(getContext(),id);
                continue;
            }
            try{
                AppWidgetHostView view=widgetHost.createView(getContext(),id,info);
                view.setPadding(dp(8),dp(8),dp(8),dp(8));
                view.setBackground(widgetGlass());
                FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(-1,dp(174));
                lp.setMargins(0,dp(6),0,dp(6));
                widgetLayer.addView(view,lp);
                shown++;
                if(shown>=1)break;
            }catch(Exception ignored){}
        }
        widgetLayer.setVisibility(shown==0?View.GONE:View.VISIBLE);
    }

    private android.graphics.drawable.GradientDrawable widgetGlass(){
        android.graphics.drawable.GradientDrawable g=new android.graphics.drawable.GradientDrawable();
        g.setColor(theme.isLight()?0xEAFBFCFF:0xCC151821);
        g.setCornerRadius(dp(26));
        g.setStroke(dp(1),theme.isLight()?0x55FFFFFF:0x35FFFFFF);
        return g;
    }

    @Override protected void onDraw(Canvas c){
        super.onDraw(c);
        int w=getWidth(),h=getHeight();
        if(w<=0||h<=0)return;
        boolean light=theme.isLight();
        c.drawColor(light?0xFFF4F6FA:0xFF08090D);
        if(performance.isLiteMode()){
            paint.setShader(null);
            removeCallbacks(invalidateTask);
            return;
        }
        long now=System.currentTimeMillis();
        if(lastFrame!=0L){
            long elapsed=now-lastFrame;
            phase+=Math.min(elapsed,120L)*0.000075f;
        }else{
            phase+=0.006f;
        }
        lastFrame=now;
        float x1=w*(0.30f+0.12f*(float)Math.sin(phase));
        float y1=h*(0.28f+0.10f*(float)Math.cos(phase*1.2f));
        float x2=w*(0.72f+0.10f*(float)Math.cos(phase*.8f));
        float y2=h*(0.70f+0.10f*(float)Math.sin(phase));
        int c1=light?0x553C8DFF:0x663C8DFF;
        int c2=light?0x55FF2F92:0x66FF2F92;
        RadialGradient g1=new RadialGradient(x1,y1,w*.55f,new int[]{c1,light?0x182F6BFF:0x223C8DFF,0x00000000},new float[]{0f,.45f,1f},Shader.TileMode.CLAMP);
        paint.setShader(g1);c.drawRect(0,0,w,h,paint);
        RadialGradient g2=new RadialGradient(x2,y2,w*.50f,new int[]{c2,light?0x182F6BFF:0x222F6BFF,0x00000000},new float[]{0f,.45f,1f},Shader.TileMode.CLAMP);
        paint.setShader(g2);c.drawRect(0,0,w,h,paint);
        paint.setShader(null);
        postDelayed(invalidateTask,90);
    }

    private final Runnable invalidateTask=new Runnable(){@Override public void run(){invalidate();}};

    @Override protected void onDetachedFromWindow(){
        removeCallbacks(invalidateTask);
        lastFrame=0L;
        stopWidgets();
        super.onDetachedFromWindow();
    }
}
