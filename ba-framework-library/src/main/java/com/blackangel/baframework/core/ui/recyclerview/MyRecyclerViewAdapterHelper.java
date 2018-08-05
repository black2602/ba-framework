package com.blackangel.baframework.core.ui.recyclerview;

import android.view.ViewGroup;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView 의 어댑태가 해야할 표현을 도와주는 인터페이스
 *
 * T : 원소 하나에 해당하는 데이터 타입
 */
public interface MyRecyclerViewAdapterHelper<T> {

    /**
     * 어댑터의 한 행의 뷰 홀더를 만든다.
     *
     * @param parent 한 행의 최상위 parent view
     * @param viewType 뷰 타입 (여러가지 다른 뷰타입이 올 수 있음 getItemViewType 에서 정의한 것에 따라)
     * @return AbsRecyclerViewHolder 를 상속한 뷰홀더 클래스
     */
    AbsRecyclerViewHolder createViewHolder(ViewGroup parent, int viewType);

    /**
     * 어댑터의 한 행의 데이터를 바인딩한다.
     *
     * @param holder 현재 행 뷰 홀더
     * @param position 현재 행 포지션
     * @param data 현재 행 데이터
     * @param itemViewType 현재 행 데이터 타입
     */
    void onBindViewHolder(AbsRecyclerViewHolder holder, int position, T data, int itemViewType);

    /**
     * T 타입과 List<T> 형식으로 표현되는 RecyclerView 데이터 컨테이너 모델
     *
     * @param <T> 데이터 원소 한개의 타입
     */
    class MyRecyclerViewDataContainer<T> {
        private List<T> mList = new ArrayList<>();

        public int addDataset(List<T> dataset) {
            List<T> newList = new ArrayList<>(dataset);
            mList.addAll(newList);

            return dataset.size();
        }

        public int length() {
            return mList.size();
        }

        public long getItemId(int position) {
            Assert.assertTrue(position >= 0);
            T t = mList.get(position);
            if(t != null)
                return t.hashCode();
            else
                return 0;
        }

        public T getData(int position) {
            Assert.assertTrue(position >= 0);
            return mList.get(position);
        }

        public void addData(T data) {
            mList.add(data);
        }

        public void removeData(int position) {
            mList.remove(position);
        }

        public void setDataset(List<T> dataset) {
            mList = new ArrayList<>(dataset);
        }

        public List<T> getDataset() {
            return mList;
        }

        public void removeAll() {
            mList.clear();
        }
    }
}
