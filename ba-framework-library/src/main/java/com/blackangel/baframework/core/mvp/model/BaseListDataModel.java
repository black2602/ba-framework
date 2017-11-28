package com.blackangel.baframework.core.mvp.model;

import java.util.List;

/**
 * Created by Finger-kjh on 2017-04-20.
 */

public interface BaseListDataModel {

    interface DataLoadCallback<T extends BaseListDataModel> {

        void onDataLoaded(List<T> datas);

        void onDataLoadFailed(String msg);
    }

}
