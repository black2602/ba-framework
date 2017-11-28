package com.blackangel.baframework.core.base;

import android.content.Intent;
import android.os.Bundle;

import com.blackangel.baframework.core.AppSchemeData;
import com.blackangel.baframework.intent.IntentConst;
import com.blackangel.baframework.util.DebugUtil;
import com.blackangel.baframework.util.IntentUtil;

/**
 * Created by Finger-kjh on 2017-11-28.
 */

public abstract class BaseMainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(AppSchemeData.isExistAppScheme()) {
            goActionByScheme(AppSchemeData.getGoActionName(), IntentUtil.convertMapToBundle(AppSchemeData.getGoActionDatas()));
            AppSchemeData.flushAppSchemeData();
        }
    }

    public void goActionByScheme(String actionName, Bundle extras) {
        DebugUtil.debugBundle(extras);

        if(extras != null) {
            processScheme(extras);
        }
    }

    protected abstract void processScheme(Bundle extras);

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        goActionByScheme(intent.getStringExtra(IntentConst.KEY_SCHEME_HOST), intent.getExtras());
    }
}
