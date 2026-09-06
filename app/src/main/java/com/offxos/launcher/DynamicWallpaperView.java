package com.offxos.launcher;

import android.content.Context;
import android.graphics.*;
import android.view.View;

public class DynamicWallpaperView extends View {
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final PerformanceStore performance;
    private final ThemeStore theme;
    private float phase=0f;
    public DynamicWallpaperView(Context c){super(c);performance=new PerformanceStore(c);theme=new ThemeStore(c);setLayerType(View.LAYER_TYPE_HARDWARE,null);}
    @Override protected void onDraw(Canvas c){
        super.onDraw(c);int w=getWidth(),h=getHeight();
        boolean light=theme.isLight();
        c.drawColor(light?0xFFF4F6FA:0xFF08090D);
        if(performance.isLiteMode()){postInvalidateDelayed(250);return;}
        phase+=0.004f;
        float x1=w*(0.30f+0.12f*(float)Math.sin(phase));
        float y1=h*(0.28f+0.10f*(float)Math.cos(phase*1.2f));
        float x2=w*(0.72f+0.10f*(float)Math.cos(phase*.8f));
        float y2=h*(0.70f+0.10f*(float)Math.sin(phase));
        int c1=light?0x553C8DFF:0x663C8DFF;
        int c2=light?0x55FF2F92:0x66FF2F92;
        RadialGradient g1=new RadialGradient(x1,y1,w*.55f,new int[]{c1,light?0x182F6BFF:0x223C8DFF,0x00000000},new float[]{0f,.45f,1f},Shader.TileMode.CLAMP);
        paint.setShader(g1);c.drawRect(0,0,w,h,paint);
        RadialGradient g2=new RadialGradient(x2,y2,w*.50f,new int[]{c2,light?0x182F6BFF:0x222F6BFF,0x00000000},new float[]{0f,.45f,1f},Shader.TileMode.CLAMP);
        paint.setShader(g2);c.drawRect(0,0,w,h,paint);paint.setShader(null);
        postInvalidateDelayed(48);
    }
}