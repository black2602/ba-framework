package com.blackangel.baframework.sample;

import com.blackangel.baframework.BaApplication;
import com.blackangel.baframework.network.HttpAPIRequester;

/**
 * Created by Finger-kjh on 2017-11-28.
 */

public class SampleApplication extends BaApplication {

    @Override
    public HttpAPIRequester.HttpRequestStrategy getHttpRequestStrategy() {
        return null;
    }
}
