package com.blackangel.baframework.sample;

import com.blackangel.baframework.BaApplication;

/**
 * Created by Finger-kjh on 2017-11-28.
 */

public class SampleApplication extends BaApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        sDebug = true;
    }

    @Override
    protected String getAppServerUrl() {
        return null;
    }
}
