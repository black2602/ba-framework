package com.blackangel.baframework.network.listener;


import com.blackangel.baframework.core.model.ListModel;

public abstract class ListModelResultCallback<T> implements ModelResultCallback<ListModel<T>> {
    public abstract void onSuccess(ListModel<T> response);
}
