package com.blackangel.baframework.core.mvvm.viewmodel;


import com.blackangel.baframework.network.listener.ModelGetResultCallback;

/**
 * Created by kimjeonghun on 2018. 7. 8..
 *
 * 어떤 데이터 모델을 가져오기 위한 데이터 소스 인터페이스
 */

public interface DataSource<T> {
    /**
     * 데이터 모델을 비동기로 가져온다.
     *
     * @param modelResultCallback   모델이 로드될 때 호출할 콜백
     */
    void getDataAsync(ModelGetResultCallback<T> modelResultCallback);
}
