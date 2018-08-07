package com.blackangel.baframework.core.mvvm.viewmodel;


import com.blackangel.baframework.network.listener.ModelGetResultCallback;
import com.blackangel.baframework.network.retrofit.BaseRetrofitRunner;

import retrofit2.Call;

public abstract class BaseViewModelFromRemote<T> extends BaseViewModel<T> implements DataSource<T> {

    public BaseViewModelFromRemote() {
        super();
        setDataSource(this);
    }

    @Override
    public void getDataAsync(ModelGetResultCallback<T> modelResultCallback) {
        BaseRetrofitRunner.executeAsyncWithoutUi(
                createLoadApiCall(),
                (globalError, extras) -> {
                    setProgressBarVisibility(false);
                    setGlobalError(globalError);
                },
                modelResultCallback);
    }

    protected abstract Call<T> createLoadApiCall();
}
