package com.offxos.launcher;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Reliable top-edge gestures for the OffxOS launcher.
 * Left half: Notification Center. Right half: Control Center.
 * It only intercepts a downward swipe that starts near the top edge,
 * so normal home-screen touches remain untouched.
 */
public class EdgeGestureFrameLayout extends FrameLayout {
    private float downX, downY;
    private boolean tracking;
    private Runnable leftAction;
    private Runnable rightAction;
    private int edgePx;
    private int triggerPx;

    public EdgeGestureFrameLayout(Context context) { super(context); init(); }
    public EdgeGestureFrameLayout(Context context, AttributeSet attrs) { super(context, attrs); init(); }
    public EdgeGestureFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); init(); }

    private void init() {
        float d = getResources().getDisplayMetrics().density;
        edgePx = (int)(72 * d + .5f);
        triggerPx = (int)(90 * d + .5f);
        setClickable(true);
    }

    public void setLeftAction(Runnable action) { leftAction = action; }
    public void setRightAction(Runnable action) { rightAction = action; }

    @Override public boolean onInterceptTouchEvent(MotionEvent e) {
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downX = e.getX();
                downY = e.getY();
                tracking = downY <= edgePx;
                return false;
            case MotionEvent.ACTION_MOVE:
                if (!tracking) return false;
                float dy = e.getY() - downY;
                float dx = e.getX() - downX;
                if (dy > triggerPx && Math.abs(dy) > Math.abs(dx) * 1.15f) {
                    return true;
                }
                return false;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                tracking = false;
                return false;
            default:
                return false;
        }
    }

    @Override public boolean onTouchEvent(MotionEvent e) {
        if (e.getActionMasked() == MotionEvent.ACTION_UP) {
            float dy = e.getY() - downY;
            if (dy > triggerPx) {
                if (downX < getWidth() * .5f) {
                    if (leftAction != null) leftAction.run();
                } else {
                    if (rightAction != null) rightAction.run();
                }
            }
            tracking = false;
            return true;
        }
        return true;
    }
}
