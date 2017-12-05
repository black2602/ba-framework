package com.blackangel.baframework.sample;

import android.os.Bundle;
import android.view.View;

import com.blackangel.baframework.R;
import com.blackangel.baframework.core.base.BaseSplashActivity;
import com.blackangel.baframework.logger.MyLog;

/**
 * Created by KimJeongHun on 2016-09-13.
 */
public class SampleSplashActivity extends BaseSplashActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_splash);
    }

    @Override
    protected void goIntro() {
        startActivity(SampleMainActivity.class);
        finish();
    }

    @Override
    protected void configPush() {
        MyLog.i();
    }

    @Override
    protected boolean isNeedShowIntro() {
        return false;
    }

    @Override
    protected void doAfterSplashSkipIntro() {
        startActivity(SampleMainActivity.class);
        finish();
    }
}
