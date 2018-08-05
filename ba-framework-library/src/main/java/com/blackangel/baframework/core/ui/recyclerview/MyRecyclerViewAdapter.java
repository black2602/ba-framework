package com.blackangel.baframework.core.ui.recyclerview;

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

    public static final int VIEW_TYPE_ITEM = 0;
    public static final int VIEW_TYPE_LOADING = 1;

    protected RecyclerView mRecyclerView;

    private MyRecyclerViewAdapterHelper<T> mMyRecyclerViewAdapterHelper;

    /**
     * 이 어댑터에 데이터를 담고 있는 데이터 컨테이너 표현
     */
    private MyRecyclerViewAdapterHelper.MyRecyclerViewDataContainer<T> mDataContainer;

    public MyRecyclerViewAdapter(RecyclerView recyclerView, @NonNull MyRecyclerViewAdapterHelper<T> myRecyclerViewAdapterHelper) {
        this.mRecyclerView = recyclerView;
        this.mMyRecyclerViewAdapterHelper = myRecyclerViewAdapterHelper;
        this.mDataContainer = new MyRecyclerViewAdapterHelper.MyRecyclerViewDataContainer<>();
    }

    public MyRecyclerViewAdapterHelper.MyRecyclerViewDataContainer<T> getDataContainer() {
        return mDataContainer;
    }

    public void setDataset(List<T> dataset) {
//        MyLog.i("dataset size=" + dataset.size());
        mDataContainer.setDataset(dataset);
        notifyDataSetChanged();
        mRecyclerView.smoothScrollToPosition(0);
    }

    public T getData(int position) {
        return mDataContainer.getData(position);
    }

    public void addDataset(List<T> dataset) {
//        MyLog.i("dataset size=" + dataset.size());
        int prevDatasetSize = mDataContainer.length();
        int addedItemCount = mDataContainer.addDataset(dataset);
        mRecyclerView.post(() -> notifyItemRangeInserted(prevDatasetSize + 1, addedItemCount));
    }

    public void addData(T data) {
        mDataContainer.addData(data);
        mRecyclerView.post(() -> notifyItemInserted(getItemCount()));
    }

    public void remove(T data) {
        int idx = mDataContainer.getDataset().indexOf(data);
        remove(idx);
    }

    public void remove(int position) {
        // inconsistency detected 에러 방어코드
        mRecyclerView.getRecycledViewPool().clear();
        mDataContainer.removeData(position);
        try {
            mRecyclerView.post(() -> {
                notifyItemRemoved(position);
            });
        } catch (IndexOutOfBoundsException e) {
            MyLog.w(e.getMessage());
            // 여기서 에러나는것은 무시
        }
    }

    /**
     * 다른 뷰 타입이 필요할 경우 오버라이드 한다. 추가하려는 뷰타입은 0, 1 이면 안됨
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
        int itemViewType = getItemViewType(position);

        if(itemViewType == VIEW_TYPE_LOADING) {
            ProgressViewHolder loadingViewHolder = (ProgressViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        } else {
            mMyRecyclerViewAdapterHelper.onBindViewHolder(holder, position, mDataContainer.getData(position), itemViewType);
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

    public void removeAll() {
        mDataContainer.removeAll();
        notifyDataSetChanged();
    }

    class ProgressViewHolder extends AbsRecyclerViewHolder {
        ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.loading_progress_footer);
        }
    }
}
