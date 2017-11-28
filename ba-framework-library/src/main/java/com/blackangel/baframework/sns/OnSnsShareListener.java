package com.blackangel.baframework.sns;

/**
 * Created by Finger-kjh on 2017-11-28.
 */

public interface OnSnsShareListener {
    void onShareCompleted();
    void onShareFailed(String message);
}
