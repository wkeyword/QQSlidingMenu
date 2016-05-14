package com.wangchangzheng.qqslidingmenu;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.Random;

public class MainActivity extends Activity {
    private ListView menu_listView, main_listView;
    private SlideMenu slidemenu;
    private ImageView iv_head;
    private MyLinearLayout my_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();

    }

    private void initData() {
        menu_listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Constant.sCheeseStrings){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textview= (TextView) super.getView(position,convertView,parent);
                textview.setTextColor(Color.WHITE);
                return textview;
            }
        });

        main_listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Constant.NAMES){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view=convertView==null?super.getView(position, convertView, parent):convertView;
                //先缩小view
                ViewHelper.setScaleX(view,0.5f);
                ViewHelper.setScaleY(view,0.5f);

                //以属性动画放大
                ViewPropertyAnimator.animate(view).scaleX(1).setDuration(350).start();
                ViewPropertyAnimator.animate(view).scaleY(1).setDuration(350).start();

                return view;
            }
        });


        slidemenu.setOnDragStateChangeListener(new SlideMenu.OnDragStateChangeListener() {
            @Override
            public void onOpen() {
                menu_listView.smoothScrollToPosition(new Random().nextInt(menu_listView.getCount()));
            }

            @Override
            public void onClose() {
                ViewPropertyAnimator.animate(iv_head).translationXBy(15)
                        .setInterpolator(new CycleInterpolator(4))
                        .setDuration(500)
                        .start();
            }

            @Override
            public void onDraging(float fraction) {
                ViewHelper.setAlpha(iv_head,1-fraction);
            }
        });
        my_layout.setSlideMenu(slidemenu);
    }

    /**
     * 初始化界面
     */
    private void initView() {
        menu_listView= (ListView) findViewById(R.id.menu_listview);
        main_listView= (ListView) findViewById(R.id.main_listview);
        slidemenu= (SlideMenu) findViewById(R.id.slideMenu);
        iv_head= (ImageView) findViewById(R.id.iv_head);
        my_layout= (MyLinearLayout) findViewById(R.id.my_layout);
    }


}
