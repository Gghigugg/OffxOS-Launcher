package com.offxos.launcher;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/** Reliable top-edge gestures. The launcher home remains fully touchable. */
public class EdgeGestureFrameLayout extends FrameLayout {
    private float downX, downY;
    private boolean tracking;
    private Runnable leftAction, rightAction;
    private int edgePx, triggerPx;

    public EdgeGestureFrameLayout(Context context) { super(context); init(); }
    public EdgeGestureFrameLayout(Context context, AttributeSet attrs) { super(context, attrs); init(); }
    public EdgeGestureFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); init(); }

    private void init() {
        float d=getResources().getDisplayMetrics().density;
        edgePx=(int)(96*d+.5f); triggerPx=(int)(72*d+.5f); setClickable(true);
    }
    public void setLeftAction(Runnable action){leftAction=action;}
    public void setRightAction(Runnable action){rightAction=action;}

    /** MainActivity historically installs a catch-all listener. Ignore it so child views keep working. */
    @Override public void setOnTouchListener(View.OnTouchListener listener) { super.setOnTouchListener(null); }

    @Override public boolean onInterceptTouchEvent(MotionEvent e){
        switch(e.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                downX=e.getX(); downY=e.getY(); tracking=downY<=edgePx; return false;
            case MotionEvent.ACTION_MOVE:
                if(!tracking) return false;
                float dy=e.getY()-downY,dx=e.getX()-downX;
                return dy>triggerPx && Math.abs(dy)>Math.abs(dx)*1.15f;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: tracking=false; return false;
            default: return false;
        }
    }
    @Override public boolean onTouchEvent(MotionEvent e){
        if(e.getActionMasked()==MotionEvent.ACTION_UP){
            if(e.getY()-downY>triggerPx){
                if(downX<getWidth()*.5f){if(leftAction!=null)leftAction.run();}
                else if(rightAction!=null)rightAction.run();
            }
            tracking=false; return true;
        }
        return true;
    }
}
