package com.blackangel.baframework.ui.view.recyclerview;

import android.view.ViewGroup;

import com.blackangel.baframework.logger.MyLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KimJeongHun on 2016-06-12.
 *
 * RecyclerView 의 어댑태가 해야할 표현을 도와주는 인터페이스
 *
 * T1 : 콜렉션 형태의 데이터 타입
 * T2 : 원소 하나에 해당하는 데이터 타입
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
     */
    void onBindViewHolder(AbsRecyclerViewHolder holder, int position, T data);

    /**
     * T 타입과 List<T> 형식으로 표현되는 RecyclerView 데이터 컨테이너 모델
     *
     * @param <T> 데이터 원소 한개의 타입
     */
    class MyRecyclerViewDataContainer<T> {
        private List<T> mList = new ArrayList<>();

        public int addDataset(List<T> dataset) {
            int count = 0;
            for (int i = 0; i < dataset.size(); i++, count++) {
                T obj = dataset.get(i);
                mList.add(obj);
                MyLog.d("addDataset >> " + obj);
            }

            return count;
        }

        public int length() {
            return mList.size();
        }

        public long getItemId(int position) {
            return mList.get(position).hashCode();
        }

        public T getData(int position) {
            return mList.get(position);
        }

        public void addData(T data) {
            mList.add(data);
        }

        public void removeData(int position) {
            mList.remove(position);
        }

        public void setDataset(List<T> dataset) {
            mList = null;
            mList = dataset;
        }

        public List<T> getDataset() {
            return mList;
        }
    }
}
