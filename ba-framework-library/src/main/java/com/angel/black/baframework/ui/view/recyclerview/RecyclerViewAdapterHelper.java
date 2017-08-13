package com.angel.black.baframework.ui.view.recyclerview;

import android.view.ViewGroup;

import com.angel.black.baframework.logger.BaLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
public interface RecyclerViewAdapterHelper<T1, T2> {

    /**
     * 어댑터의 한 행의 뷰 홀더를 만든다.
     *
     * @param parent 한 행의 최상위 parent view
     * @return AbsRecyclerViewHolder 를 상속한 뷰홀더 클래스
     */
    AbsRecyclerViewHolder createViewHolder(ViewGroup parent);

    /**
     * 어댑터의 한 행의 데이터를 바인딩한다.
     *
     * @param holder 현재 행 뷰 홀더
     * @param position 현재 행 포지션
     * @param data 현재 행 데이터
     */
    void onBindViewHolder(AbsRecyclerViewHolder holder, int position, T2 data);

    /**
     * 어댑터에 데이터를 제공한다.
     * @return RecyclerViewColletionData(T1 콜렉션형, T2 데이터형) 을 상속한 RecyclerView 에 표시될 데이터 모델
     */
    RecyclerViewColletionData<T1, T2> provideData();

    /**
     * RecyclerView 에 표현될 데이터 모델 정의
     * 추상클래스이므로 이 클래스를 상속하여 써야한다.
     *
     * @param <T1> Collection 형 데이터 타입
     * @param <T2> 원소 한개 데이터 타입
     */
    abstract class RecyclerViewColletionData<T1, T2> {
        public abstract int addDataset(T1 dataset);

        public abstract int length();

        public abstract long getItemId(int position);

        public abstract T2 getData(int position);

        public abstract void addData(T2 data);

        public abstract void removeData(int position);

        public abstract void setDataset(T1 dataset);
    }

    /**
     * RecyclerViewColletionData 을 상속
     * T 타입과 List<T> 형식으로 표현되는 RecyclerView 데이터 모델
     *
     * @param <T> 데이터 원소 한개의 타입
     */
    class ListModelRecyclerViewCollectionData<T> extends RecyclerViewColletionData<List<T>, T> {
        private List<T> mList = new ArrayList<>();

        @Override
        public int addDataset(List<T> dataset) {
            int count = 0;
            for (int i = 0; i < dataset.size(); i++, count++) {
                T obj = dataset.get(i);
                mList.add(obj);
                BaLog.d("addDataset >> " + obj);
            }

            return count;
        }

        @Override
        public int length() {
            return mList.size();
        }

        @Override
        public long getItemId(int position) {
            return mList.get(position).hashCode();
        }

        @Override
        public T getData(int position) {
            return mList.get(position);
        }

        @Override
        public void addData(T data) {
            mList.add(data);
        }

        @Override
        public void removeData(int position) {
            mList.remove(position);
        }

        @Override
        public void setDataset(List<T> dataset) {
            mList = null;
            mList = dataset;
        }
    }


    class JSONRecyclerViewCollectionData extends RecyclerViewColletionData<JSONArray, JSONObject> {
        private JSONArray mDataset = new JSONArray();

        @Override
        public int addDataset(JSONArray dataset) {
            int count = 0;
            try {
                for (int i = 0; i < dataset.length(); i++, count++) {
                    JSONObject jsonObject = dataset.getJSONObject(i);
                    mDataset.put(jsonObject);
                    BaLog.d("addDataset >> " + jsonObject);
                }

                return count;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return 0;
        }

        @Override
        public int length() {
            return mDataset.length();
        }

        @Override
        public long getItemId(int position) {
            try {
                return ((JSONObject) mDataset.get(position)).optLong("id");
            } catch (JSONException e) {
                e.printStackTrace();
                return 0;
            }
        }

        @Override
        public JSONObject getData(int position) {
            try {
                return mDataset.getJSONObject(position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void addData(JSONObject data) {
            mDataset.put(data);
        }

        @Override
        public void removeData(int position) {
            mDataset.remove(position);
        }

        @Override
        public void setDataset(JSONArray dataset) {
            mDataset = null;
            mDataset = dataset;
        }
    }
}
