package com.blackangel.baframework.sns;

/**
 * Created by Finger-kjh on 2017-11-28.
 */

public interface OnSnsLoginListener {
    void onLoginCompleted(SnsLoginResult snsLoginResult, ISnsLoginParam... moreLoginParams);
    void onLoginFailed(String message);
}
