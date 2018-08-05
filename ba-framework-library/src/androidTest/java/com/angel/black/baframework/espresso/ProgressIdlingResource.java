package com.angel.black.baframework.espresso;

import android.app.Activity;
import android.support.test.espresso.IdlingResource;

import com.blackangel.kakaobanktest.core.base.BaseActivity;


public class ProgressIdlingResource implements IdlingResource {

    private ResourceCallback resourceCallback;
    private Activity activity;

    public ProgressIdlingResource(BaseActivity activity){
        this.activity = activity;

//        dataLoadProgressCallback = new DataLoadProgressCallback() {
//            @Override
//            public void onLoadStart(String progressMsg) {
//
//            }
//
//            @Override
//            public void onLoadDone() {
//                if (resourceCallback == null){
//                    return ;
//                }
//                //Called when the resource goes from busy to idle.
//                resourceCallback.onTransitionToIdle();
//            }
//        };
//
//        this.activity.setDataLoadProgressCallback(dataLoadProgressCallback);
    }
    @Override
    public String getName() {
        return "My idling resource";
    }

    @Override
    public boolean isIdleNow() {
        // the resource becomes idle when the progress has been dismissed
//        return !activity.isShownProgress();
        return false;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
        this.resourceCallback = resourceCallback;
    }
}