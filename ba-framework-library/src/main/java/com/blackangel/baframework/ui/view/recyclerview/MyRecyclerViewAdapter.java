package com.blackangel.baframework.ui.view.recyclerview;

/**
 * Created by Finger-kjh on 2017-12-05.
 */

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.blackangel.baframework.R;
import com.blackangel.baframework.logger.MyLog;

import java.util.List;

/**
 * 헤더 뷰 없는 기본적인 커스텀 RecyclerView 어댑터
 */
public class MyRecyclerViewAdapter<T> extends RecyclerView.Adapter<AbsRecyclerViewHolder> {
    public static final int VIEW_TYPE_HEADER = 0;
    public static final int VIEW_TYPE_ITEM = 1;
    public static final int VIEW_TYPE_LOADING = 2;

    MyRecyclerViewAdapterHelper<T> mMyRecyclerViewAdapterHelper;

    /**
     * 이 어댑터에 데이터를 담고 있는 데이터 컨테이너 표현
     */
    MyRecyclerViewAdapterHelper.MyRecyclerViewDataContainer<T> mDataContainer;

    public MyRecyclerViewAdapter(@NonNull MyRecyclerViewAdapterHelper<T> myRecyclerViewAdapterHelper) {
        this.mMyRecyclerViewAdapterHelper = myRecyclerViewAdapterHelper;
        this.mDataContainer = new MyRecyclerViewAdapterHelper.MyRecyclerViewDataContainer<>();
    }

    public MyRecyclerViewAdapterHelper.MyRecyclerViewDataContainer<T> getDataContainer() {
        return mDataContainer;
    }

    public void setDataset(List<T> dataset) {
        MyLog.i();
        mDataContainer.setDataset(dataset);
        notifyDataSetChanged();
    }

    public T getData(int position) {
        return mDataContainer.getData(position);
    }

    public void addDataset(List<T> dataset) {
        MyLog.i();
        int addedItemCount = mDataContainer.addDataset(dataset);
        notifyItemRangeInserted(mDataContainer.getDataset().size() + 1, addedItemCount);
    }

    public void addData(T data) {
        mDataContainer.addData(data);
        notifyItemInserted(getItemCount());
    }

    public void remove(int position) {
        try {
            mDataContainer.removeData(position);
            notifyItemRemoved(position);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();

            // 여기서 에러나는것은 무시
        }
    }

    /**
     * 다른 뷰 타입이 필요할 경우 오버라이드 한다. 추가하려는 뷰타입은 0, 1, 2 이면 안됨
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return mDataContainer.getData(position) != null ? VIEW_TYPE_ITEM : VIEW_TYPE_LOADING;
    }

    @Override
    public AbsRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AbsRecyclerViewHolder vh = null;
        if(viewType == VIEW_TYPE_LOADING) {
            // 로딩 뷰
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_progress, parent, false);
            vh = new ProgressViewHolder(view);
        } else {
            vh = mMyRecyclerViewAdapterHelper.createViewHolder(parent, viewType);
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(AbsRecyclerViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_LOADING) {
            ProgressViewHolder loadingViewHolder = (ProgressViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        } else {
            mMyRecyclerViewAdapterHelper.onBindViewHolder(holder, position, mDataContainer.getData(position));
        }
    }

    @Override
    public int getItemCount() {
        return mDataContainer.length();
    }

    @Override
    public long getItemId(int position) {
        return mDataContainer.getItemId(position);
    }

    class ProgressViewHolder extends AbsRecyclerViewHolder {
        ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.loading_progress);
        }
    }

    public class HeaderViewHolder extends AbsRecyclerViewHolder {
        HeaderViewHolder(View v) {
            super(v);
        }
    }
}
