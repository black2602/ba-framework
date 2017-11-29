package com.blackangel.baframework.network;

/**
 * Created by Finger-kjh on 2017-11-15.
 */

public interface ApiProgressListener {
    void onStartApi(String progressMsg);
    void onFinishApi();
    void onGlobalErrorResponse(int errCode, String errMessage, Object... extras);
    boolean isGlobalError(int errCode);
}
