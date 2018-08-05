package com.blackangel.baframework.core.mvvm.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

/**
 * BaseListFragment 의 RecyclerView 에 표시할 기본 아이템 뷰모델
 * @param <T>
 */
public class BaseListItemViewModel<T> extends BaseObservable {

    protected ObservableField<T> model;
    protected ObservableInt itemViewType;

    public BaseListItemViewModel() {
        this.model = new ObservableField<>();
        this.itemViewType = new ObservableInt();
    }

    public T getModel() {
        return model.get();
    }

    public void setModel(T model) {
        this.model.set(model);
        notifyChange();
    }

    public int getItemViewType() {
        return itemViewType.get();
    }

    public void setItemViewType(int itemViewType) {
        this.itemViewType.set(itemViewType);
    }
}
