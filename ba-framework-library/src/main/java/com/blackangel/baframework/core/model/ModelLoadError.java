package com.blackangel.baframework.core.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true, of = {})
public class ModelLoadError extends BaseError {

    private String requestUri;

    public ModelLoadError(String requestUri, int errCode, String errMessage, Throwable throwable) {
        super(errCode, errMessage, throwable);
        this.requestUri = requestUri;
    }
}
