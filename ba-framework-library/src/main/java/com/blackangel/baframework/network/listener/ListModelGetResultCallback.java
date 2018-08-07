package com.blackangel.baframework.network.listener;


import com.blackangel.baframework.core.model.ListModel;

public abstract class ListModelGetResultCallback<T> implements ModelGetResultCallback<ListModel<T>> {
    public abstract void onSuccess(ListModel<T> response);
}
