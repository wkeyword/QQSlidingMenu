package com.wangchangzheng.slidingmenutest;

import android.content.Context;
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
public class DragLayout extends FrameLayout {
    private View redView;
    private View bludeView;
    private ViewDragHelper viewDragHelper;

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public DragLayout(Context context) {
        super(context);
        init();
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //让ViewDragHelper帮我们判断是否应该拦截
        boolean result=viewDragHelper.shouldInterceptTouchEvent(ev);
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //把触摸事件交给ViewDragHelper来解析处理
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    private void init() {
        viewDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {

            /**
             * 用于判断是否捕获当前child的触摸事件
             * @param child：当前触摸的子View
             * @param pointerId
             * @return 如果返回的是true 就捕获并解析，如果返回的是false：那么久不处理事件
             */
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return child == redView || child == bludeView;
            }


            /**
             * 获取View水平方向的拖拽范围，但是目前不能限制边界，但会的值目前用在手抬起的时候view缓慢移动的动画计算上，最好不要返回0
             * @param child
             * @return
             */
            @Override
            public int getViewHorizontalDragRange(View child) {
                return getMeasuredWidth() - child.getMeasuredWidth();
            }


            /**
             * 获取view垂直方向的拖拽方位，最好不要返回0
             * @param child
             * @return
             */
            @Override
            public int getViewVerticalDragRange(View child) {
                return getMeasuredHeight() - child.getMeasuredHeight();
            }

            /**
             * clamp 是跟着走的意思
             * 本方法的作用是：控制child在水平方向的移动left
             * 表示ViewDragHelper认为你想让当前child的left改变的值，left=child.getLeft+dx
             * @param child
             * @param left
             * @param dx：本次child水平方向上移动的距离
             * @return：表示你真正想让child的left变成的值
             */
            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                if (left < 0) {
                    //表示child的左边界超出了当前父View的的边界，那么就返回0
                    left = 0;
                } else if (left > (getMeasuredWidth() - child.getMeasuredWidth())) {
                    //如果child超出了右边的范围，那么就让其返回的位置是：父View-子View的宽度，就限制了超出边界
                    left = getMeasuredWidth() - child.getMeasuredWidth();
                }
                return left;
            }


            /**
             * clamp表示的是跟着走的意思，容易记住函数名的意思
             * 当child的位置改变的时候执行，一般用做其他子View的伴随移动
             * 表示ViewDrageHelper认为你想让当前child的top改变的值
             * @param child：改变位置的child
             * @param top：top=child.getTop+dy
             * @param dy：表示本次child垂直方向移动的距离
             * @return：表示你真正想让child的top改变的值
             */
            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                if (top < 0) {
                    top=0;
                }else if (top>(getMeasuredHeight()-child.getMeasuredHeight())){
                    top=getMeasuredHeight()-child.getMeasuredHeight();
                }
                return top;
            }


            /**
             * 当chiild的位置改变的时候执行，一般用来做其他子View的伴随移动
             * @param changedView：位置改变的view
             * @param left：child当前最新的left
             * @param top：child当前最新的top
             * @param dx：表示本次水平移动的距离
             * @param dy：表示本次垂直移动的距离
             */
            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
                if (changedView == bludeView) {
                    //blueView移动的时候让redView随着他的移动而移动
                    redView.layout(redView.getLeft()+dx,redView.getTop()+dy,redView.getRight()+dx,redView.getBottom()+dy);
                }else if (changedView==redView){
                    //redView移动的时候也让blueview随着他的移动而移动
                    bludeView.layout(bludeView.getLeft()+dx,bludeView.getTop()+dy,bludeView.getRight()+dx,bludeView.getBottom()+dy);
                }
                //计算View移动的百分比
                float fraction = changedView.getLeft()*1f/(getMeasuredWidth()-changedView.getMeasuredWidth());
                executeAnim(fraction);
            }

            /**
             * 手指抬起的时候执行该方法，
             * @param releasedChild：当前抬起的view
             * @param xvel：x方向的移动速度 正：向右移动  负：向左移动
             * @param yvel：y方向上的移动速度  正：向右移动  负：向左移动
             */
            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);
                int centerLeft=getMeasuredWidth()/2;
                if (releasedChild.getLeft()<centerLeft ) {
                    //说明是在左半边，应该向左缓慢移动
                    viewDragHelper.smoothSlideViewTo(releasedChild,0,releasedChild.getTop());
                    ViewCompat.postInvalidateOnAnimation(DragLayout.this);
                }else {
                    viewDragHelper.smoothSlideViewTo(releasedChild,getMeasuredWidth()-releasedChild.getMeasuredWidth(),releasedChild.getTop());
                    ViewCompat.postInvalidateOnAnimation(DragLayout.this);
                }
            }
        });
    }

    private void executeAnim(float fraction) {
//        ViewHelper.setRotationX(redView,360*fraction);//围绕X轴旋转
//        ViewHelper.setRotation(redView,360*fraction);//围绕Z轴旋转
//        ViewHelper.setRotationY(redView,360*fraction);//围绕Y轴旋转
//        ViewHelper.setScaleX(redView,1+0.5f*fraction);//缩放 x方向
//        ViewHelper.setScaleY(redView,1+05f*fraction);//缩放y方向

        ViewHelper.setAlpha(redView,1-fraction);//透明度的变化

//        ViewHelper.setTranslationX(redView,80*fraction);//平移

    }


    /**
     * 分别设置两个View的位置
     *
     * @param changed
     * @param left    左边的位置
     * @param top     顶部位置
     * @param right   右边位置
     * @param bottom  底部位置，注意使用 getBottom为获取到控件的顶部高度
     */

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int p_left = getPaddingLeft();
        int p_top = getPaddingTop();

        //第一个View的位置
        redView.layout(p_left, p_top, p_left + redView.getMeasuredWidth(), p_top + redView.getMeasuredHeight());

        //第二个View的位置
        bludeView.layout(p_left, redView.getBottom(), p_left + bludeView.getMeasuredWidth(), redView.getBottom() + bludeView.getMeasuredHeight());

    }


    /**
     * 在加载完成后查询两个子View
     * 注意：传入的只能是两个View对象
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        redView = getChildAt(0);
        bludeView = getChildAt(1);
    }

    /**
     * 重写computeScroll()的原因
     调用startScroll()是不会有滚动效果的，只有在computeScroll()获取滚动情况，做出滚动的响应
     */
    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(DragLayout.this);
        }
    }



}
