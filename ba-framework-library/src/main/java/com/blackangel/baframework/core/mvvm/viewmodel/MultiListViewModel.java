package com.blackangel.baframework.core.mvvm.viewmodel;

import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;

import com.blackangel.baframework.core.model.ModelLoadError;
import com.blackangel.baframework.core.model.BaseError;
import com.blackangel.baframework.core.model.ListModel;
import com.blackangel.baframework.network.listener.ListModelGetResultCallback;

import java.util.Arrays;
import java.util.List;

/**
 * Created by kimjeonghun on 2018. 7. 7..
 *
 * 여러개의 리스트 뷰모델을 동시에 사용하기 위한 멀티리스트 뷰모델
 * 동시에 특정 리스트 데이터를 받아오기 위해 동시에 요청할때 사용한다.
 *
 * T : 두가지 리스트 모델로부터 머지할 아이템 타입
 */
public abstract class MultiListViewModel<T> extends BaseListViewModel<T> implements ListDataSource<T> {

    private ObservableArrayList<BaseListViewModel> childListViewModels = new ObservableArrayList<>();
    private ObservableBoolean isAllListDataLoadDone = new ObservableBoolean();

    public MultiListViewModel(BaseListViewModel... childListViewModels) {
        super();
        setListDataSource(this);
        setChildListViewModels(Arrays.asList(childListViewModels));
    }

    public ObservableArrayList<BaseListViewModel> getChildListViewModels() {
        return childListViewModels;
    }

    public void setChildListViewModels(List<BaseListViewModel> listViewModels) {
        // 기존의 프로퍼티 체인지 콜백 제거
        for (BaseListViewModel baseListViewModel : this.childListViewModels) {
            baseListViewModel.removeOnPropertyChangedCallback(childListViewModelPropertyChangedCallback);
        }

        this.childListViewModels.clear();
        this.childListViewModels.addAll(listViewModels);

        for (BaseListViewModel baseListViewModel : this.childListViewModels) {
            baseListViewModel.addOnPropertyChangedCallback(childListViewModelPropertyChangedCallback);
        }
    }

    private boolean checkFinishedAllListDataLoad() {
        for (BaseListViewModel baseListViewModel : childListViewModels) {
            if(baseListViewModel.isListDataLoading() || !baseListViewModel.isListDataLoaded()) {
                return false;
            }
        }

        return true;
    }

    @Bindable
    public boolean isAllListDataLoadDone() {
        return isAllListDataLoadDone.get();
    }

    public void setAllListDataLoadDone(boolean isAllListDataLoadDone) {
        if(isAllListDataLoadDone) {
            setListModel(mergeAllIndividualLoadedModels());
            setProgressBarVisibility(false);
            removeGlobalError();
            removeModelLoadError();
            setListDataLoaded(true);
        }

        this.isAllListDataLoadDone.set(isAllListDataLoadDone);
    }

    public abstract ListModel<T> mergeAllIndividualLoadedModels();

    @Override
    public void getListDataAsync(int page, int pageSize, ListModelGetResultCallback<T> apiListModelGetResultCallback) {
        for (BaseListViewModel baseListViewModel : childListViewModels) {
            baseListViewModel.loadListAsync(false, page, pageSize);
        }
    }

    BaseListViewModelPropertyChangedCallback childListViewModelPropertyChangedCallback = new BaseListViewModelPropertyChangedCallback() {
        @Override
        protected void handleLoadMoreErrorVisiblity(boolean loadMoreErrorVisible) {
            setLoadMoreErrorVisible(loadMoreErrorVisible);
        }

        @Override
        protected void handleListModelLoaded() {
            // 각각의 리스트 뷰모델 에서 데이터를 성공적으로 가져왔을 때, 모든 리스트 뷰모델이 다 데이터를 가져왔는지
            // 체크하여 모든 데이터가 로드되었다는 프로퍼티를 기록한다.
            setAllListDataLoadDone(checkFinishedAllListDataLoad());
        }

        @Override
        protected void handleListDataChanged() {
            // do not use
        }

        @Override
        protected void handleProgressBarVisible(boolean progressBarVisible) {
            // do not use
        }

        @Override
        protected void handleModelLoadError(int propertyId, ModelLoadError modelLoadError) {
            // 각 리스트 뷰모델에서 모델 로드중 에러 발생시, 동일한 종류의 에러에 대해서는 패스하도록
            if (getModelLoadError() == null || !getModelLoadError().equals(modelLoadError)) {
                setModelLoadError(modelLoadError);
                setProgressBarVisibility(false);
            }
        }

        @Override
        protected void handleGlobalError(BaseError globalError) {
            // 각 리스트 뷰모델에서 글로벌 에러 발생시, 동일한 종류의 글로벌 에러에 대해서는 패스하도록
            if (getGlobalError() == null || !getGlobalError().equals(globalError)) {
                setGlobalError(globalError);
                setProgressBarVisibility(false);
            }
        }
    };
}
