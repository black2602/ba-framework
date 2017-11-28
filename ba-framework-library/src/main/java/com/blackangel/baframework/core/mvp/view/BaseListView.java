package com.blackangel.baframework.core.mvp.view;

public interface BaseListView<T> extends BaseView<T> {

    void savePaginationInfo(int totalItemCount);
}
