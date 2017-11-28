package com.blackangel.baframework.core.mvp.view;

public interface BaseView<T> {

    void setPresenter(T presenter);

    void showErrorMessage(String url, int errCode, String message, boolean finish);
}
