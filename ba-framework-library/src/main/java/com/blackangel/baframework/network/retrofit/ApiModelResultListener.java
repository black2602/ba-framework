package com.blackangel.baframework.network.retrofit;

/**
 * Created by Finger-kjh on 2017-06-15.
 */

public interface ApiModelResultListener<T> {
    void onSuccess(T response);
    void onFail(String url, int errCode, String message, Throwable throwable);
}
