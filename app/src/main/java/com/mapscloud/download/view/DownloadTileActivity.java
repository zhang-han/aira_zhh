package com.mapscloud.download.view;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mapscloud.download.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mapscloud8 on 2017/10/13.
 */

public class DownloadTileActivity extends FragmentActivity implements ActionBar.TabListener, ViewPager.OnPageChangeListener {

    DownloadTileActivity activity;
    private List<Fragment> mFragments = new ArrayList<Fragment>();
    LinearLayout ll_title_select;
    TextView tv_title1, tv_title2;
    private ViewPager mViewPager;
    private FragmentAdapter mAdapter;
    ImageButton btn_back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.window_download_tile);

        initView();

    }

    public void initView(){

        mFragments.add(new DownloadingFragment());
        mFragments.add(new DownloadCompletFragment() );

        mViewPager = (ViewPager) findViewById(R.id.actionbar_view_pager);
        mViewPager.setOnPageChangeListener(this);
        mAdapter = new FragmentAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mAdapter.setDatas(mFragments);
        mAdapter.notifyDataSetChanged();

        ll_title_select = (LinearLayout) findViewById(R.id.ll_title_select);
        tv_title1 = (TextView) findViewById(R.id.tv_title1);
        tv_title2 = (TextView) findViewById(R.id.tv_title2);

        tv_title1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
            }
        });
        tv_title2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(1);
            }
        });

        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition(), true);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(position ==  0){
            ll_title_select.setBackground(activity.getResources().getDrawable(R.mipmap.download_window_title1));
            tv_title1.setTextColor(activity.getResources().getColor(R.color.downloadWindownWhite));
            tv_title2.setTextColor(activity.getResources().getColor(R.color.downloadWindownBlue));
        } else if(position == 1){
            ll_title_select.setBackground(activity.getResources().getDrawable(R.mipmap.download_window_title2));
            tv_title1.setTextColor(activity.getResources().getColor(R.color.downloadWindownBlue));
            tv_title2.setTextColor(activity.getResources().getColor(R.color.downloadWindownWhite));
        }
    }

    @Override
    public void onPageScrollStateChanged(int position) {

    }
}
