package com.offxos.launcher;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

/** iPhone-style horizontal home pages. */
public class AppPagerGrid extends LinearLayout {
    private HorizontalScrollView scroller;
    private LinearLayout pages;
    private LinearLayout currentPage;
    private int rowsPerPage = 5;
    private int rowCount = 0;
    private TextView dots;

    public AppPagerGrid(Context c){ super(c); init(); }
    public AppPagerGrid(Context c, AttributeSet a){ super(c,a); init(); }

    private void init(){
        super.setOrientation(VERTICAL);
        setGravity(Gravity.FILL);
        scroller = new HorizontalScrollView(getContext());
        scroller.setHorizontalScrollBarEnabled(false);
        scroller.setOverScrollMode(OVER_SCROLL_NEVER);
        pages = new LinearLayout(getContext());
        pages.setOrientation(HORIZONTAL);
        scroller.addView(pages, new HorizontalScrollView.LayoutParams(-1,-1));
        super.addView(scroller, new LinearLayout.LayoutParams(-1,0,1));
        dots = new TextView(getContext());
        dots.setTextColor(0xBFFFFFFF);
        dots.setTextSize(10);
        dots.setGravity(Gravity.CENTER);
        super.addView(dots, new LinearLayout.LayoutParams(-1,dp(20)));
        scroller.setOnScrollChangeListener((v,x,y,oldx,oldy)->updateDots());
        post(this::recalculateRows);
    }

    private int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}

    @Override public void setOrientation(int orientation){ super.setOrientation(VERTICAL); }

    @Override public void removeAllViews(){
        pages.removeAllViews();
        currentPage=null;
        rowCount=0;
        updateDots();
    }

    @Override public void addView(View child, ViewGroup.LayoutParams params){
        if(child==dots || child==scroller){ super.addView(child, params); return; }
        if(currentPage==null || rowCount>=rowsPerPage){
            currentPage=new LinearLayout(getContext());
            currentPage.setOrientation(VERTICAL);
            currentPage.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
            pages.addView(currentPage,new LinearLayout.LayoutParams(-1,-1));
            rowCount=0;
            post(this::updatePageWidths);
        }
        currentPage.addView(child, params);
        rowCount++;
        updateDots();
    }

    @Override protected void onSizeChanged(int w,int h,int oldw,int oldh){
        super.onSizeChanged(w,h,oldw,oldh);
        recalculateRows();
        updatePageWidths();
    }

    private void recalculateRows(){
        int h=getHeight()-dp(20);
        if(h>0) rowsPerPage=Math.max(4,Math.min(6,h/dp(94)));
    }

    private void updatePageWidths(){
        int w=getWidth();
        if(w<=0)return;
        for(int i=0;i<pages.getChildCount();i++){
            View p=pages.getChildAt(i);
            ViewGroup.LayoutParams lp=p.getLayoutParams();
            lp.width=w; lp.height=-1;
            p.setLayoutParams(lp);
        }
    }

    private void updateDots(){
        int count=pages==null?0:pages.getChildCount();
        if(dots==null)return;
        if(count<=1){dots.setText("");return;}
        int page=scroller.getScrollX()/Math.max(1,getWidth());
        StringBuilder b=new StringBuilder();
        for(int i=0;i<count;i++) b.append(i==page?"● ":"○ ");
        dots.setText(b.toString().trim());
    }
}
