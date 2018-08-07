package com.blackangel.baframework.core.mvvm.viewmodel;


import com.blackangel.baframework.core.model.ListModel;
import com.blackangel.baframework.network.listener.ListModelGetResultCallback;
import com.blackangel.baframework.network.retrofit.BaseRetrofitRunner;

import retrofit2.Call;

/**
 * 원격 서버로부터 데이터를 가져오기 위한 뷰모델 - Retrofit, OkHttpClient 사용하여 원격서버로부터 데이터 가져옴
 * @param <T> 서버로부터 가져올 모델 아이템 타입
 */
public abstract class BaseListViewModelFromRemote<T> extends BaseListViewModel<T> implements ListDataSource<T> {

    public BaseListViewModelFromRemote() {
        super();
        setListDataSource(this);
    }

    /**
     * 원격 서버로부터 리스트 데이터를 가져올 Call(Retrofit) 객체를 만든다.
     */
    protected abstract Call<? extends ListModel<T>> createLoadApiCall(int page, int pageSize);

    @Override
    public void getListDataAsync(int page, int pageSize, ListModelGetResultCallback<T> apiListModelGetResultCallback) {
        BaseRetrofitRunner.executeAsyncForList(
                createLoadApiCall(page, pageSize),
                (globalError, extras) -> {
                    setProgressBarVisibility(false);
                    setGlobalError(globalError);
                    setListDataLoaded(false);
                },
                apiListModelGetResultCallback);
    }
}