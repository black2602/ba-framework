package com.blackangel.baframework.core.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.blackangel.baframework.R;

/**
 * Created by Finger-kjh on 2017-12-01.
 */

public abstract class BaseIntroActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;
    private Button mBtnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_intro);

        mViewPager = findViewById(R.id.viewpager);
        mViewPager.setAdapter(createPagerAdapter());
        mViewPager.addOnPageChangeListener(this);

        if(isHandleInnerViews()) {
            mBtnNext = findViewById(R.id.btn_next);
            mBtnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goNext();
                }
            });
        }
    }

    private PagerAdapter createPagerAdapter() {
        return new IntroPagerAdapter(isHandleInnerViews(), getPageResIds());
    }

    protected abstract void goNext();
    protected abstract boolean isHandleInnerViews();
    protected abstract int[] getPageResIds();

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(position == mViewPager.getAdapter().getCount() - 1) {
            mBtnNext.setVisibility(View.VISIBLE);
        } else {
            mBtnNext.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class IntroPagerAdapter extends PagerAdapter {
        private int[] mPageResIds;
        private boolean mIsHandleInnerViews;
        private LayoutInflater mLayoutInflater;

        public IntroPagerAdapter(boolean isHandleInnerViews, int[] pageResIds) {
            mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mIsHandleInnerViews = isHandleInnerViews;
            mPageResIds = pageResIds;
        }

        @Override
        public int getCount() {
            return mPageResIds.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view;
            if(mIsHandleInnerViews) {
                view = new ImageView(BaseIntroActivity.this);
                ((ImageView) view).setImageResource(mPageResIds[position]);
                container.addView(view);

            } else {
                view = mLayoutInflater.inflate(mPageResIds[position], container, false);
                container.addView(view);
            }

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

}
