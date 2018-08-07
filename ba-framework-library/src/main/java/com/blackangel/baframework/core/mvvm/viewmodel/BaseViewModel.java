package com.blackangel.baframework.core.mvvm.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayMap;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.blackangel.baframework.BR;
import com.blackangel.baframework.core.model.ModelLoadError;
import com.blackangel.baframework.core.model.BaseError;
import com.blackangel.baframework.network.listener.ModelGetResultCallback;


/**
 * Created by kimjeonghun on 2018. 6. 12..
 */

public abstract class BaseViewModel<T> extends BaseObservable {

    private ObservableField<T> model = new ObservableField<>();
    private ObservableBoolean progressBarVisibility = new ObservableBoolean();
    private ObservableField<ModelLoadError> modelLoadError = new ObservableField<>();
    private ObservableField<BaseError> globalError = new ObservableField<>();  // 네트워크 에러 같은 공통으로 처리할 에러

    // 기타 필요한 속성 오브젝트를 저장할 맵
    private ObservableArrayMap<Integer, Object> etcProperties = new ObservableArrayMap<>();
    private ObservableArrayMap<Integer, ModelLoadError> etcPropertiesErrors = new ObservableArrayMap<>();

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
    public ModelLoadError getModelLoadError() {
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

    protected void setModelLoadError(ModelLoadError modelLoadError) {
        this.modelLoadError.set(modelLoadError);
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

        dataSource.getDataAsync(new ModelGetResultCallback<T>() {
                    @Override
                    public void onSuccess(T response) {
                        setProgressBarVisibility(false);
                        setModel(response);
                    }

                    @Override
                    public void onFail(ModelLoadError modelLoadError) {
                        setProgressBarVisibility(false);
                        setModelLoadError(modelLoadError);
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
    public <T> void loadAnotherRequestApi(int propertyId, boolean showProgress, DataSource<T> dataSource) {
        if (showProgress) {
            setProgressBarVisibility(true);
        }

        dataSource.getDataAsync(new ModelGetResultCallback<T>() {
            @Override
            public void onSuccess(T response) {
                setProgressBarVisibility(false);
                removeEtcPropertyError(propertyId);
                addEtcProperty(propertyId, response);
            }

            @Override
            public void onFail(ModelLoadError modelLoadError) {
                setProgressBarVisibility(false);
                removeEtcPropertyValue(propertyId);
                addEtcPropertyLoadError(propertyId, modelLoadError);
            }
        });
    }

    private void removeEtcPropertyValue(int propertyId) {
        etcProperties.remove(propertyId);
    }

    private void removeEtcPropertyError(int propertyId) {
        etcPropertiesErrors.remove(propertyId);
    }

    private void addEtcPropertyLoadError(int propertyId, ModelLoadError modelLoadError) {
        etcPropertiesErrors.put(propertyId, modelLoadError);
        notifyPropertyChanged(propertyId);
    }


    public <T> T getEtcPropertyObjectValue(int key, Class<T> clazz) {
        Object obj = etcProperties.get(key);
        if(obj == null) {
            return null;

        } else if(obj.getClass() == clazz) {
            return (T) obj;

        } else {
            throw new IllegalStateException("this etcProperty type is not " + clazz.getSimpleName());
        }
    }

    public boolean getEtcPropertyBooleanValue(int key) {
        Boolean value = getEtcPropertyObjectValue(key, Boolean.class);

        if(value == null) {
            return false;
        } else {
            return value;
        }
    }

    public int getEtcPropertyIntValue(int key) {
        Integer value = getEtcPropertyObjectValue(key, Integer.class);

        if(value == null) {
            return 0;
        } else {
            return value;
        }
    }

    public long getEtcPropertyLongValue(int key) {
        Long value = getEtcPropertyObjectValue(key, Long.class);

        if(value == null) {
            return 0L;
        } else {
            return value;
        }
    }

    public double getEtcPropertyDoubleValue(int key) {
        Double value = getEtcPropertyObjectValue(key, Double.class);

        if(value == null) {
            return 0d;
        } else {
            return value;
        }
    }

    protected void addEtcProperty(int propertyId, Object object) {
        etcProperties.put(propertyId, object);
        notifyPropertyChanged(propertyId);
    }

    public boolean hasEtcPropertyError(int propertyId) {
        return etcPropertiesErrors.get(propertyId) != null;
    }

    public ModelLoadError getEtcPropertyError(int propertyId) {
        return etcPropertiesErrors.get(propertyId);
    }
}
