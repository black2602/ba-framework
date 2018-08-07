package com.blackangel.baframework.core.mvvm.viewmodel;

import android.databinding.Bindable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import com.blackangel.baframework.BR;
import com.blackangel.baframework.core.model.ModelLoadError;
import com.blackangel.baframework.core.model.ListModel;
import com.blackangel.baframework.logger.MyLog;
import com.blackangel.baframework.network.listener.ListModelGetResultCallback;


public abstract class BaseListViewModel<T> extends BaseViewModel {
    public static final int DEFAULT_PAGE_SIZE = 20;

    private ObservableInt curPage = new ObservableInt(1);
    private ObservableInt curItemCount = new ObservableInt();
    private ObservableInt requestPageSize = new ObservableInt(DEFAULT_PAGE_SIZE);
    private ObservableInt toDisplayTotalItemCount = new ObservableInt();

    private ObservableField<ListModel<T>> listModel = new ObservableField<>();
    private ObservableBoolean listDataChanged = new ObservableBoolean();
    private ObservableBoolean listDataLoading = new ObservableBoolean();
    private ObservableBoolean listDataLoaded = new ObservableBoolean();
    private ObservableBoolean isLoadMoreErrorVisible = new ObservableBoolean();

    protected ListDataSource<T> listDataSource;

    public BaseListViewModel() {}

    public BaseListViewModel(ListDataSource<T> listDataSource) {
        super();
        this.listDataSource = listDataSource;
    }

    public void setCurPage(int curPage) {
        this.curPage.set(curPage);
    }

    public int getCurPage() {
        return curPage.get();
    }

    public void increaseCurPage() {
        this.curPage.set(getCurPage() + 1);
    }

    public int getCurItemCount() {
        return curItemCount.get();
    }


    public void setCurItemCount(int curItemCount) {
        this.curItemCount.set(curItemCount);
    }

    public void increaseCurItemCount(int itemCount) {
        setCurItemCount(getCurItemCount() + itemCount);
    }

    public void decreaseCurItemCount(int deletedItemCount) {
        setCurItemCount(getCurItemCount() - deletedItemCount);
    }

    @Override
    public final void setDataSource(DataSource dataSource) throws IllegalAccessError {
        //do not use
        throw new IllegalAccessError("this feature is not supported!");
    }

    public void setListDataSource(ListDataSource<T> listDataSource) {
        this.listDataSource = listDataSource;
        MyLog.i("listDataSource=" + listDataSource);
    }

    @Bindable
    public ListModel<T> getListModel() {
        return listModel.get();
    }

    public void setListModel(ListModel<T> listModel) {
        MyLog.i(this.getClass().getSimpleName());
        this.listModel.set(listModel);
        notifyPropertyChanged(BR.listModel);
    }

    @Bindable
    public boolean getListDataChanged() {
        return listDataChanged.get();
    }

    public void setListDataChanged(boolean listDataChanged) {
        this.listDataChanged.set(listDataChanged);
        notifyPropertyChanged(BR.listDataChanged);
    }

    public boolean isListDataLoading() {
        return listDataLoading.get();
    }

    public void setListDataLoading(boolean listDataLoading) {
        MyLog.i(this.getClass().getSimpleName() + " setListDataLoading=" + listDataLoading);
        this.listDataLoading.set(listDataLoading);
    }

    public boolean isListDataLoaded() {
        return listDataLoaded.get();
    }

    public void setListDataLoaded(boolean listDataLoaded) {
        this.listDataLoaded.set(listDataLoaded);
    }

    public boolean isCanLoadMore() {
        return !getListModel().isEnd();
    }

    public void loadListAsync(boolean showCenterProgress, int page, int pageSize) {
        MyLog.i(this.getClass().getSimpleName());
        removeGlobalError();
        removeModelLoadError();

        if(showCenterProgress && page == 1) {
            setProgressBarVisibility(true);
        }

        setListDataLoading(true);
        listDataSource.getListDataAsync(page, pageSize, new ListModelGetResultCallback<T>() {
            @Override
            public void onSuccess(ListModel<T> response) {
                setProgressBarVisibility(false);
                removeModelLoadError();
                removeGlobalError();
                setListDataLoading(false);
                setListDataLoaded(true);
                setListModel(processLoadedListModel(response, page));
            }

            @Override
            public void onFail(ModelLoadError modelLoadError) {
                setProgressBarVisibility(false);
                setListDataLoading(false);
                setListDataLoaded(false);
                setModelLoadError(modelLoadError);
                if(getCurPage() > 1) {
                    setLoadMoreErrorVisible(true);
                }
            }
        });
    }

    protected ListModel<T> processLoadedListModel(ListModel<T> listModel, int page) {
        return listModel;
    }

    public void onVisibleAtViewPager() {
        if (getCurPage() > 1 && (getGlobalError() != null || getModelLoadError() != null)) {
            setLoadMoreErrorVisible(true);
        }
    }

    public void onHiddenAtViewPager() {
        setLoadMoreErrorVisible(false);
    }

    @Bindable
    public boolean isLoadMoreErrorVisible() {
        return isLoadMoreErrorVisible.get();
    }

    public void setLoadMoreErrorVisible(boolean isLoadMoreErrorVisible) {
        this.isLoadMoreErrorVisible.set(isLoadMoreErrorVisible);
        notifyPropertyChanged(BR.loadMoreErrorVisible);
    }

    @Override
    protected void removeGlobalError() {
        super.removeGlobalError();
        setLoadMoreErrorVisible(false);
    }

    @Override
    protected void removeModelLoadError() {
        super.removeModelLoadError();
        setLoadMoreErrorVisible(false);
    }

    public int getRequestPageSize() {
        return requestPageSize.get();
    }

    public void setRequestPageSize(int requestPageSize) {
        this.requestPageSize.set(requestPageSize);
    }

    public int getToDisplayTotalItemCount() {
        return toDisplayTotalItemCount.get();
    }

    public void setToDisplayTotalItemCount(int toDisplayTotalItemCount) {
        this.toDisplayTotalItemCount.set(toDisplayTotalItemCount);
    }
}