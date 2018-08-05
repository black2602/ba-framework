package com.blackangel.baframework.network.listener;


import com.blackangel.baframework.core.model.BaseError;

public interface GlobalErrorHandler {
    void onGlobalErrorResponse(BaseError globalError, Object... extras);
}
