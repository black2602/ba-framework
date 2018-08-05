package com.blackangel.baframework;

import android.app.Application;

import com.blackangel.baframework.app.constants.ApiInfo;
import com.blackangel.baframework.preference.MyPreferenceManager;

/**
 * Created by KimJeongHun on 2016-05-19.
 */
public abstract class BaApplication extends Application {
    public static boolean sDebug;

    private static BaApplication sApp;
    public static MyPreferenceManager sMyPreferenceManager;

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;

        ApiInfo.APP_SERVER_URL = getAppServerUrl();
    }

    protected abstract String getAppServerUrl();

    public synchronized static BaApplication getInstance() {
        return sApp;
    }

    public static MyPreferenceManager getPreferenceManager() {
        if(sMyPreferenceManager == null)
            sMyPreferenceManager = MyPreferenceManager.getInstance(sApp);

        return sMyPreferenceManager;
    }
}
