package com.blackangel.baframework.core.mvvm.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayMap;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.blackangel.baframework.BR;
import com.blackangel.baframework.core.model.ApiModelLoadError;
import com.blackangel.baframework.core.model.BaseError;
import com.blackangel.baframework.network.listener.ModelResultCallback;


/**
 * Created by kimjeonghun on 2018. 6. 12..
 */

public abstract class BaseViewModel<T> extends BaseObservable {

    private ObservableField<T> model = new ObservableField<>();
    private ObservableBoolean progressBarVisibility = new ObservableBoolean();
    private ObservableField<ApiModelLoadError> modelLoadError = new ObservableField<>();
    private ObservableField<BaseError> globalError = new ObservableField<>();  // 네트워크 에러 같은 공통으로 처리할 에러

    // 기타 필요한 속성 오브젝트를 저장할 맵
    private ObservableArrayMap<Integer, Object> etcProperties = new ObservableArrayMap<>();
    private ObservableArrayMap<Integer, ApiModelLoadError> etcPropertiesErrors = new ObservableArrayMap<>();

    private DataSource<T> dataSource;

    public BaseViewModel() {
    }

    public BaseViewModel(DataSource<T> dataSource) {
        this.dataSource = dataSource;
    }

    public void setDataSource(DataSource<T> dataSource) {
        this.dataSource = dataSource;
    }

    @Bindable
    public BaseError getGlobalError() {
        return globalError.get();
    }

    @Bindable
    public ApiModelLoadError getModelLoadError() {
        return modelLoadError.get();
    }

    @Bindable
    public boolean getProgressBarVisibility() {
        return progressBarVisibility.get();
    }

    @Bindable
    public T getModel() {
        return model.get();
    }

    protected void setModel(T model) {
        this.model.set(model);
        notifyPropertyChanged(BR.model);
    }

    protected void setProgressBarVisibility(boolean progressBarVisibility) {
        this.progressBarVisibility.set(progressBarVisibility);
        notifyPropertyChanged(BR.progressBarVisibility);
    }

    protected void setModelLoadError(ApiModelLoadError apiModelLoadError) {
        this.modelLoadError.set(apiModelLoadError);
        notifyPropertyChanged(BR.modelLoadError);
    }

    public void setGlobalError(BaseError globalError) {
        this.globalError.set(globalError);
        notifyPropertyChanged(BR.globalError);
    }

    protected void removeModelLoadError() {
        this.modelLoadError.set(null);
    }

    protected void removeGlobalError() {
        this.globalError.set(null);
    }

    public final void load(boolean showProgress) {
        if (showProgress) {
            setProgressBarVisibility(true);
        }

        dataSource.getDataAsync(new ModelResultCallback<T>() {
                    @Override
                    public void onSuccess(T response) {
                        setProgressBarVisibility(false);
                        setModel(response);
                    }

                    @Override
                    public void onFail(String url, int errCode, String message, Throwable throwable) {
                        setProgressBarVisibility(false);
                        setModelLoadError(new ApiModelLoadError(url, errCode, message, throwable));
                    }
                }
        );
    }

    /**
     * 메인으로 로드하는 api 외에 어떤 특정 api 를 호출해야할 경우 사용하는 api 요청 메소드
     * @param propertyId        응답을 리스닝할 프로퍼티 id (Observable 객체로 선언해두고 get메서드에 @Bindable 붙인)
     * @param showProgress
     * @param dataSource
     * @param <T>
     */
    public <T> void loadWithAnotherRequestApi(int propertyId, boolean showProgress, DataSource<T> dataSource) {
        if (showProgress) {
            setProgressBarVisibility(true);
        }

        dataSource.getDataAsync(new ModelResultCallback<T>() {
            @Override
            public void onSuccess(T response) {
                setProgressBarVisibility(false);
                removeEtcPropertyError(propertyId);
                addEtcProperty(propertyId, response);
            }

            @Override
            public void onFail(String url, int errCode, String message, Throwable throwable) {
                setProgressBarVisibility(false);
                removeEtcPropertyValue(propertyId);
                addEtcPropertyLoadError(propertyId, new ApiModelLoadError(url, errCode, message, throwable));
            }
        });
    }

    private void removeEtcPropertyValue(int propertyId) {
        etcProperties.remove(propertyId);
    }

    private void removeEtcPropertyError(int propertyId) {
        etcPropertiesErrors.remove(propertyId);
    }

    private void addEtcPropertyLoadError(int propertyId, ApiModelLoadError apiModelLoadError) {
        etcPropertiesErrors.put(propertyId, apiModelLoadError);
        notifyPropertyChanged(propertyId);
    }

    public Object getEtcPropertyValue(int key) {
        return etcProperties.get(key);
    }

    protected void addEtcProperty(int propertyId, Object object) {
        etcProperties.put(propertyId, object);
        notifyPropertyChanged(propertyId);
    }

    public boolean hasEtcPropertyError(int propertyId) {
        return etcPropertiesErrors.get(propertyId) != null;
    }

    public ApiModelLoadError getEtcPropertyError(int propertyId) {
        return etcPropertiesErrors.get(propertyId);
    }
}
