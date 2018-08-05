package com.blackangel.baframework.core.mvvm.viewmodel;

import android.databinding.Observable;

import com.blackangel.baframework.BR;


public abstract class BaseListViewModelPropertyChangedCallback extends BaseViewModelPropertyChangedCallback {

    @Override
    public void onPropertyChanged(Observable sender, int propertyId) {
//        MyLog.i("propertyId = " + propertyId);

        if (propertyId == BR.listDataChanged) {
            handleListDataChanged();

        } else if(propertyId == BR.listModel) {
            handleListModelLoaded();

        } else if (propertyId == BR.loadMoreErrorVisible) {
            handleLoadMoreErrorVisiblity(((BaseListViewModel) sender).isLoadMoreErrorVisible());

        } else {
            super.onPropertyChanged(sender, propertyId);
        }
    }

    protected abstract void handleLoadMoreErrorVisiblity(boolean loadMoreErrorVisible);

    protected abstract void handleListModelLoaded();

    protected abstract void handleListDataChanged();

    @Override
    protected final void handleViewMainModelUpdate() {
        // do not use
    }
}