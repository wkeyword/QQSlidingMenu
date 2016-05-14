package com.wangchangzheng.qqslidingmenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by wangchangzheng on 16/5/14.
 */
public class MyLinearLayout extends LinearLayout{
    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private SlideMenu slideMenu;

    public void setSlideMenu(SlideMenu slideMenu) {
        this.slideMenu = slideMenu;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (slideMenu!=null && slideMenu.getCurrentState()== SlideMenu.DragState.Open){
            //如果slidemenu代开则应该拦截并消费掉事件
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (slideMenu!=null && slideMenu.getCurrentState()== SlideMenu.DragState.Open){
            if (event.getAction()==MotionEvent.ACTION_UP){
                //抬起则应该关闭slidemenu
                slideMenu.close();
            }
            //如果slidemenu打开则应该拦截并消费事件
            return true;
        }
        return super.onTouchEvent(event);
    }
}
