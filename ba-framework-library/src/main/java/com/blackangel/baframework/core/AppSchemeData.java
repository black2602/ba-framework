package com.blackangel.baframework.core;

import android.content.Intent;

import com.blackangel.baframework.core.base.BaseActivity;
import com.blackangel.baframework.core.base.BaseMainActivity;
import com.blackangel.baframework.intent.IntentConst;
import com.blackangel.baframework.util.IntentUtil;

import java.util.Map;

/**
 * Created by Finger-kjh on 2017-09-13.
 */

public class AppSchemeData {
    public static final String SCHEME_HOST_DO_REMIT = "remit";
    public static final String SCHEME_HOST_REMIT_HISTORY = "history";

    public static AppSchemeData sAppSchemeData;

    private String host;
    private Map<String, String> datas;

    public AppSchemeData(String host, Map<String, String> datas) {
        this.host = host;
        this.datas = datas;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Map<String, String> getDatas() {
        return datas;
    }

    public void setDatas(Map<String, String> datas) {
        this.datas = datas;
    }

    public void goSchemeActionToMain(BaseActivity activity, final boolean isAppRunning, Class<? extends BaseMainActivity> mainActivityClass) {
        activity.startActivity(new BaseActivity.IntentExtraProvider() {
            @Override
            public void putExtras(Intent intent) {
                if (isAppRunning) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                }
                intent.putExtra(IntentConst.KEY_SCHEME_HOST, host);
                if (datas != null && datas.size() > 0) {
                    intent.putExtras(IntentUtil.convertMapToBundle(datas));
                }
            }
        }, mainActivityClass);
    }

    public static boolean isExistAppScheme() {
        return AppSchemeData.sAppSchemeData != null;
    }

    public static String getGoActionName() {
        return AppSchemeData.sAppSchemeData.getHost();
    }

    public static Map<String, String> getGoActionDatas() {
        return AppSchemeData.sAppSchemeData.getDatas();
    }

    public static void flushAppSchemeData() {
        AppSchemeData.sAppSchemeData = null;
    }
}
