package com.angel.black.baframework.espresso;

import android.support.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Finger-kjh on 2018-03-13.
 */

public class SimpleIdlingResource implements IdlingResource {
    private volatile ResourceCallback resourceCallback;
    private AtomicBoolean isIdleNow = new AtomicBoolean();

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public boolean isIdleNow() {
        boolean isIdle = isIdleNow.get();

        System.out.println("SimpleIdlingResource isIdle = " + isIdle);
        return isIdle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.resourceCallback = callback;
    }

    public void setIdleState(boolean isIdleNow) {
        this.isIdleNow.set(isIdleNow);

        System.out.println("setIdleState = " + isIdleNow);

        if (isIdleNow && resourceCallback != null) {
            resourceCallback.onTransitionToIdle();
        }
    }
}
