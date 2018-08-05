package com.blackangel.baframework.core.mvvm.viewmodel;

/**
 * 로컬 에서 리스트 데이터를 가져오기 위한 리스트 뷰모델
 * @param <T> 가져올 리스트 아이템 모델 타입
 */
public abstract class BaseListViewModelFromLocal<T> extends BaseListViewModel<T> {

    public BaseListViewModelFromLocal(ListDataSource<T> dataSource) {
        super(dataSource);
    }
}