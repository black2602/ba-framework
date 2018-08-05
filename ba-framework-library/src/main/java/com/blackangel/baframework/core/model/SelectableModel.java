package com.blackangel.baframework.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

/**
 * Created by kimjeonghun on 2018. 7. 8..
 *
 * 어떤 아이템을 래핑하여 선택상태를 저장하기 위한 모델
 */

@Data
@EqualsAndHashCode(of = {"item"})
public class SelectableModel<T> {

    /**
     * 현재 선택모드인지 여부.
     * TODO 이 값은 모든 아이템에 공통으로 적용되는 속성일 가능성이 높으니, 굳이 모든 아이템 모델에 할당되면 메모리 낭비인 것으로 추정됨.. -> 개선 필요
     */
    private boolean isSelectionMode;
    private boolean isSelected;
    @NonNull private T item;

    public SelectableModel(T item) {
        this.item = item;
    }

    /**
     * 순수 리스트로부터 SelectableModel 타입 리스트로 변환
     */
    public static <T> List<SelectableModel<T>> ofCollection(Collection<T> itemList) {
        List<SelectableModel<T>> convertedList = new ArrayList<>();
        for (T t : itemList) {
            SelectableModel<T> selectableModel = new SelectableModel<>(t);
            convertedList.add(selectableModel);
        }

        return convertedList;
    }

    /**
     * SelectableModel 타입 리스트를 순수 리스트 타입으로 변환
     */
    public static <T> List<T> toPrimaryList(Collection<SelectableModel<T>> selectableModelCollection) {
        List<T> convertedList = new ArrayList<>();
        for (SelectableModel<T> selectableModel : selectableModelCollection) {
            convertedList.add(selectableModel.getItem());
        }

        return convertedList;
    }

}
