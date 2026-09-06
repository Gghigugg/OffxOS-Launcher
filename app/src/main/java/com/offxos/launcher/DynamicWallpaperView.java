package com.offxos.launcher;

import android.content.Context;
import android.graphics.*;
import android.view.View;

public class DynamicWallpaperView extends View {
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private float phase=0f;
    public DynamicWallpaperView(Context c){super(c);setLayerType(View.LAYER_TYPE_SOFTWARE,null);}
    @Override protected void onDraw(Canvas c){super.onDraw(c);int w=getWidth(),h=getHeight();c.drawColor(0xFF08090D);phase+=0.006f;float x1=w*(0.30f+0.12f*(float)Math.sin(phase));float y1=h*(0.28f+0.10f*(float)Math.cos(phase*1.2));float x2=w*(0.72f+0.10f*(float)Math.cos(phase*.8));float y2=h*(0.70f+0.10f*(float)Math.sin(phase));RadialGradient g1=new RadialGradient(x1,y1,w*.55f,new int[]{0x663C8DFF,0x223C8DFF,0x00000000},new float[]{0f,.45f,1f},Shader.TileMode.CLAMP);paint.setShader(g1);c.drawRect(0,0,w,h,paint);RadialGradient g2=new RadialGradient(x2,y2,w*.50f,new int[]{0x66FF2F92,0x222F6BFF,0x00000000},new float[]{0f,.45f,1f},Shader.TileMode.CLAMP);paint.setShader(g2);c.drawRect(0,0,w,h,paint);paint.setShader(null);postInvalidateDelayed(32);}
}
