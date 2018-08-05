package com.blackangel.baframework.core.model;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class ListModel<T> {

    @SerializedName("meta")
    private ListModelMetaData listModelMetaData;

    @SerializedName("documents")
    private List<T> itemList;

    public ListModel() {
        this.listModelMetaData = new ListModelMetaData();
    }

    public void setTotalCount(int count) {
        listModelMetaData.setTotalCount(count);
    }

    public void setPageableCount(int pageableCount) {
        listModelMetaData.setPageableCount(pageableCount);
    }

    public void setEnd(boolean isEnd) {
        listModelMetaData.setEnd(isEnd);
    }

    public int getTotalCount() {
        return listModelMetaData.getTotalCount();
    }

    public int getPageableCount() {
        return listModelMetaData.getPageableCount();
    }

    public boolean isEnd() {
        return listModelMetaData.isEnd();
    }

    public static <T> ListModel fromLocalList(int page, int pageSize, @NonNull List<T> allPageList) {

        if(allPageList.size() == 0)
            return new ListModel();

        int fromIndex = pageSize * (page - 1);
        int toIndex = fromIndex + pageSize;
        boolean isEnd = false;

        if(fromIndex >= allPageList.size()) {
            // 페이징 시작인덱스가 전체 리스트의 크기를 넘어가버리면 불가능한 페이징 모델 요청임
            throw new IllegalArgumentException("request fromIndex (" + fromIndex + ") bigger or equal than allPageList size (" + allPageList.size() + ")");
        }

        else if(toIndex >= allPageList.size()) {
            // 페이징 끝 인덱스가 전체 리스트의 크기를 넘어가버리면 마지막 페이지이고, 가져올 목록크기가 페이지 사이즈보다 작은 경우
            toIndex = allPageList.size();
            isEnd = true;
        }

        List<T> listPerPage = allPageList.subList(fromIndex, toIndex);  // toIndex 는 exclusive
        ListModel<T> listModel = new ListModel<>();
        listModel.setItemList(listPerPage);
        listModel.setPageableCount(allPageList.size());
        listModel.setTotalCount(allPageList.size());
        listModel.setEnd(isEnd);

        return listModel;
    }

    public static <T> ListModel empty() {
        ListModel<T> listModel = new ListModel<>();
        listModel.setListModelMetaData(new ListModelMetaData());
        listModel.setItemList(Collections.emptyList());
        return listModel;
    }
}
