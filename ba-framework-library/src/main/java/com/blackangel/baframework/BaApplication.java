package com.blackangel.baframework;

import android.app.Application;

import com.blackangel.baframework.app.constants.ApiInfo;
import com.blackangel.baframework.preference.MyPreferenceManager;

/**
 * Created by KimJeongHun on 2016-05-19.
 */
public abstract class BaApplication extends Application {
    public static boolean sDebug;

    private static BaApplication instance;
    public static MyPreferenceManager sMyPreferenceManager;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        ApiInfo.APP_SERVER_URL = getAppServerUrl();
    }

    protected abstract String getAppServerUrl();

    public synchronized static BaApplication getInstance() {
        return instance;
    }

    public static MyPreferenceManager getPreferenceManager() {
        if(sMyPreferenceManager == null)
            sMyPreferenceManager = MyPreferenceManager.getInstance(instance);

        return sMyPreferenceManager;
    }
}
