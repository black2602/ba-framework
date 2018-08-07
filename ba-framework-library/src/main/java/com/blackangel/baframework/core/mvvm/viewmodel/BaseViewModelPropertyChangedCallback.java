package com.blackangel.baframework.core.mvvm.viewmodel;

import android.databinding.Observable;

import com.blackangel.baframework.BR;
import com.blackangel.baframework.core.model.ModelLoadError;
import com.blackangel.baframework.core.model.BaseError;

/**
 * 기본 뷰 모델 프로퍼티 콜백
 */
public abstract class BaseViewModelPropertyChangedCallback extends Observable.OnPropertyChangedCallback {

    @Override
    public void onPropertyChanged(Observable sender, int propertyId) {
//        MyLog.i("propertyId = " + propertyId + ", sender = " + sender);
//        MyLog.i("sender instanceof BaseViewModel=" + (sender instanceof BaseViewModel));
        if(sender instanceof BaseViewModel) {
            if (propertyId == BR.globalError) {
                BaseError globalError = ((BaseViewModel) sender).getGlobalError();
                handleGlobalError(globalError);
            }
            else if (propertyId == BR.modelLoadError) {
                ModelLoadError modelLoadError = ((BaseViewModel) sender).getModelLoadError();
                handleModelLoadError(BR.model, modelLoadError);

            } else if (propertyId == BR.progressBarVisibility) {
                handleProgressBarVisible(((BaseViewModel) sender).getProgressBarVisibility());

            } else if (propertyId == BR.model) {
                handleViewMainModelUpdate();

            } else {
                handleEtcPropertyValueOrError(propertyId);
            }
        }
    }

    /**
     * 기본적으로 사용하는 BaseViewModel 의 속성 외에 추가적으로 핸들링이 필요한 기타 프로퍼티를 처리한다.
     * @param propertyId
     */
    public void handleEtcPropertyValueOrError(int propertyId) {}

    protected abstract void handleProgressBarVisible(boolean progressBarVisible);

    protected abstract void handleModelLoadError(int propertyId, ModelLoadError modelLoadError);

    protected abstract void handleGlobalError(BaseError globalError);

    protected abstract void handleViewMainModelUpdate();
}
