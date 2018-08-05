package com.blackangel.baframework.network.listener;

public interface ModelResultCallback<T> {
    void onSuccess(T response);

    void onFail(String url, int errCode, String message, Throwable throwable);
}
