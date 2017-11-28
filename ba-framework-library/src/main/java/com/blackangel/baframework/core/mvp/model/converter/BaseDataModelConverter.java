package com.blackangel.baframework.core.mvp.model.converter;


import com.blackangel.baframework.core.mvp.model.BaseDataModel;

/**
 * Created by Finger-kjh on 2017-04-26.
 */

public abstract class BaseDataModelConverter<T, M extends BaseDataModel> {

    public abstract M convertModel(T originData);

}
