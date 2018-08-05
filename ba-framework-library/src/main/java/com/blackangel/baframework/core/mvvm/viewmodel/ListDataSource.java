package com.blackangel.baframework.core.mvvm.viewmodel;


import com.blackangel.baframework.network.listener.ListModelResultCallback;

/**
 * Created by kimjeonghun on 2018. 7. 8..
 *
 * 어떤 리스트 데이터 모델을 가져오기 위한 리스트 데이터 소스 인터페이스
 */

public interface ListDataSource<T> {

    /**
     * 리스트 데이터 모델을 비동기로 가져온다.
     *
     * @param page                      가져올 페이지
     * @param pageSize                  한페이지에 들어갈 아이템 사이즈
     * @param listModelResultCallback   리스트 모델이 로드될 때 호출할 콜백
     */
    void getListDataAsync(int page, int pageSize, ListModelResultCallback<T> listModelResultCallback);
}
