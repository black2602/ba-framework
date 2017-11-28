package com.blackangel.baframework.core.mvp.model.converter;

import com.blackangel.baframework.core.mvp.model.BaseListDataModel;

import java.util.List;

/**
 * Created by Finger-kjh on 2017-04-26.
 */

public abstract class BaseListDataModelConverter<T, M extends BaseListDataModel> {

    public abstract List<M> convertModel(T originData);

}
