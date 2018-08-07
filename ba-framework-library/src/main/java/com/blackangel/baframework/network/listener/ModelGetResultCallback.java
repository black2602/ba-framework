package com.blackangel.baframework.network.listener;

import com.blackangel.baframework.core.model.ModelLoadError;

public interface ModelGetResultCallback<T> {
    void onSuccess(T response);

    void onFail(ModelLoadError modelLoadError);

}
