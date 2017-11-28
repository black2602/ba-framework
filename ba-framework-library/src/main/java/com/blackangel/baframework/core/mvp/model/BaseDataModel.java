package com.blackangel.baframework.core.mvp.model;

/**
 * Created by Finger-kjh on 2017-04-20.
 */

public interface BaseDataModel {

    interface DataLoadCallback<T extends BaseDataModel> {

        void onDataLoaded(T data);

        void onDataLoadFailed(String msg);
    }

}
