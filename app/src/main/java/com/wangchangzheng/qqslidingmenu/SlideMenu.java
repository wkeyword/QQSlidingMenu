package com.wangchangzheng.qqslidingmenu;

import android.animation.FloatEvaluator;
import android.animation.IntEvaluator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by wangchangzheng on 16/5/14.
 */
public class SlideMenu extends FrameLayout {

    private View menuView;
    private View mainView;
    private ViewDragHelper viewDragHelper;
    private float dragRange;//拖拽范围
    private int width;
    private FloatEvaluator floatEvaluator;//floadt的计算器
    private IntEvaluator intEvaluator;//int的计算器

    /**
     * 定义状态常量
     */
    enum DragState{
        Open,Close
    }
    private DragState currentState=DragState.Close;//当前SlideMenu的状态默认是关闭的

    public DragState getCurrentState(){
        return currentState;
    }

    /**
     * 初始化两个子View空间，并且做出判断
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount()!=2){
            throw new IllegalArgumentException("Slidemenu only have 2 Children，Plasease Delete other View");
        }
        menuView=getChildAt(0);
        mainView=getChildAt(1);
    }

    /**
     * 该方法在onMeasure执行完之后，那么可以在该方法中初始化自己和子View的宽和高
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width=getMeasuredWidth();
        dragRange=width*0.6f;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }



    public interface OnDragStateChangeListener{
        /**
         * 打开回调
         */
        void onOpen();

        void onClose();

        void onDraging(float fraction);
    }


    @Override
    public void computeScroll() {

        if (viewDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
        }
    }


    private OnDragStateChangeListener listener;
    public void setOnDragStateChangeListener(OnDragStateChangeListener listener){
        this.listener=listener;
    }

    public SlideMenu(Context context) {
        super(context);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        viewDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return child == menuView || child == mainView;
            }


            @Override
            public int getViewHorizontalDragRange(View child) {
                return (int) dragRange;
            }


            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                if (child == mainView) {
                    if (left < 0) {
                        left = 0;
                    }
                    if (left > dragRange) {
                        left = (int) dragRange;
                    }
                }
                return left;
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                if (changedView==menuView){
                    menuView.layout(0,0,menuView.getMeasuredWidth(),menuView.getMeasuredHeight());
                    int newLeft=mainView.getLeft()+dx;
                    if (newLeft<0)newLeft=0;
                    if (newLeft>dragRange)newLeft= (int) dragRange;
                    mainView.layout(newLeft,mainView.getTop()+dy,mainView.getMeasuredWidth()+newLeft,mainView.getBottom()+dy);
                }

                //1 计算滑动的百分比
                float fracion=mainView.getLeft()/dragRange;

                //2 执行伴随动画
                executeAnim(fracion);

                //3 更改状态，回调listener方法
                if (fracion==0 && currentState!=DragState.Close){
                    //更改状态为关闭，并回调关闭的方法
                    currentState=DragState.Close;
                    if (listener!=null)listener.onClose();;
                }else if (fracion==1f && currentState!=DragState.Open){
                    //更改状态为打开，并回调打开的方法
                    currentState=DragState.Open;
                    if (listener!=null)listener.onOpen();
                }
                //将drag的fraction暴漏给外界
                if (listener!=null){
                    listener.onDraging(fracion);
                }
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                if (mainView.getLeft()<dragRange/2){
                    close();
                }else {
                    open();
                }


                if (xvel>200 && currentState!=DragState.Close){
                    open();
                }else if (xvel<-200 && currentState!=DragState.Close){
                    close();
                }
            }
        });

        floatEvaluator=new FloatEvaluator();
        intEvaluator=new IntEvaluator();

        }

    /**
     * 关闭菜单
     */
    public void close() {
        viewDragHelper.smoothSlideViewTo(mainView,0,mainView.getTop());
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
    }

    /**
     * 打开菜单
     */
    public void open(){
        viewDragHelper.smoothSlideViewTo(mainView, (int) dragRange,mainView.getTop());
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
    }

    /**
     * 执行动画
     * @param fracion
     */
    private void executeAnim(float fracion) {
        ViewHelper.setScaleY(mainView,floatEvaluator.evaluate(fracion,1f,0.8f));
        ViewHelper.setScaleY(mainView,floatEvaluator.evaluate(fracion,1f,0.8f));

        //移动menuView
        ViewHelper.setTranslationX(menuView,intEvaluator.evaluate(fracion,-menuView.getMeasuredWidth()/2,0));

        //放大 menuView
        ViewHelper.setScaleX(menuView,floatEvaluator.evaluate(fracion,0.5f,1f));
        ViewHelper.setScaleY(menuView,floatEvaluator.evaluate(fracion,0.5f,1f));

        //改变menuView的透明度
        ViewHelper.setAlpha(menuView,floatEvaluator.evaluate(fracion,0.3f,1f));

        //给SlideMenu的背景添加黑色的遮盖效果
        getBackground().setColorFilter((Integer) ColorUtil.evaluateColor(fracion, Color.BLACK,Color.TRANSPARENT), PorterDuff.Mode.SRC_OVER);


    }
}
