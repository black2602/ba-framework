package com.blackangel.baframework.core.mvp.model;

import com.blackangel.baframework.core.base.BaseActivity;
import com.blackangel.baframework.preference.MyPreferenceManager;


/**
 * Created by Finger-kjh on 2017-06-13.
 */

public abstract class BaseDataLoader {
    protected BaseActivity mActivity;
    protected MyPreferenceManager mPreferenceManager;

    public BaseDataLoader(BaseActivity activity) {
        mActivity = activity;
        mPreferenceManager = MyPreferenceManager.getInstance(activity);
    }

}
