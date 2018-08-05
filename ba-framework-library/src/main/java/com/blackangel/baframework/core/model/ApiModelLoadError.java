package com.blackangel.baframework.core.model;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true, of = {})
public class ApiModelLoadError extends BaseError {

    private String apiRequestUrl;

    public ApiModelLoadError(String apiRequestUrl, int errCode, String errMessage, Throwable throwable) {
        super(errCode, errMessage, throwable);
        this.apiRequestUrl = apiRequestUrl;
    }

    public String getApiRequestUrl() {
        return apiRequestUrl;
    }

    public void setApiRequestUrl(String apiRequestUrl) {
        this.apiRequestUrl = apiRequestUrl;
    }

}
