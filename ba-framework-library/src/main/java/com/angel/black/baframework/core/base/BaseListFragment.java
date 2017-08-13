package com.angel.black.baframework.core.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.angel.black.baframework.R;
import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.ui.view.recyclerview.AbsRecyclerViewHolder;
import com.angel.black.baframework.ui.view.recyclerview.RecyclerViewAdapterHelper;

import java.util.List;

/**
 * Created by KimJeongHun on 2016-09-11.
 */
public abstract class BaseListFragment extends BaseFragment {
    public static final String TAG_LIST_ROW = "listRow";

    protected RecyclerView mRecyclerView;
    protected MyRecyclerViewAdapter mRecyclerViewAdapter;
    protected LinearLayoutManager mLayoutManager;
    protected ViewGroup mEmptyView;
    protected View.OnClickListener mOnRowClickListener;

    protected int mCurPage = 1;
    protected int mCurItemCount = 0;    // 리스트의 현재까지 페이징 된 아이템 갯수
    protected int mTotalItemCount = 0;  // 리스트의 모든 페이지 토탈 아이템 갯수

    protected boolean isCanLoadMore = true;

    public abstract void requestList(boolean showOutProgress);
    protected abstract MyRecyclerViewAdapter createListAdapter();

    /**
     * RecyclerView 에 추가로 데코레이션 한다.
     * Divider, Padding 설정 등
     */
    protected abstract void addRecyclerViewDecoration(RecyclerView recyclerView);

    public void setOnRowClickListener(View.OnClickListener onRowClickListener) {
        mOnRowClickListener = onRowClickListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        BaLog.i();
        View view = inflater.inflate(R.layout.fragment_base_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mEmptyView = (ViewGroup) view.findViewById(R.id.layout_empty_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.setSmoothScrollbarEnabled(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerViewAdapter = createListAdapter();
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.addOnScrollListener(new MyScrollListener());

        addRecyclerViewDecoration(mRecyclerView);

        return view;
    }

    protected void showEmptyLayout() {
        if(mEmptyView.getChildCount() == 0) {
            mEmptyView.addView(createEmptyView());
        }

        mEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    protected View createEmptyView() {
        TextView textView = new TextView(getContext());
        textView.setText(R.string.empty_data);

        return textView;
    }

    public void showLoadingFooter() {
        mRecyclerViewAdapter.addData(null);
    }

    public void hideLoadingFooter() {
        mRecyclerViewAdapter.remove(mRecyclerViewAdapter.getItemCount() - 1);
    }

    protected void loadMore() {
        mCurPage++;

        // 하단 프로그레스바 추가
        showLoadingFooter();
        requestList(true);
    }

    protected void setTotalItemCount(int totalCount) {
        mTotalItemCount = totalCount;

        BaLog.d("mCurItemCount=" + mCurItemCount + ", mCurPage=" + mCurPage + ", mTotalItemCount=" + mTotalItemCount);
    }

    public void initPagination() {
        mCurPage = 1;
        mTotalItemCount = 0;
        mCurItemCount = 0;
    }

    public void setLoadComplete() {
        isCanLoadMore = true;
    }

    public void populateList(List<?> dataset) {
        boolean noData = dataset == null || dataset.size() == 0;

        if(noData) {
            noDataLoaded(mCurPage == 1);

            if(mCurPage > 1)
                hideLoadingFooter();

        } else {

            if(mEmptyView.getVisibility() == View.VISIBLE) {
                mEmptyView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }

            if (mCurPage > 1) {
                hideLoadingFooter();
                mRecyclerViewAdapter.addDataset(dataset);
            } else {
                mRecyclerViewAdapter.setDataset(dataset);
            }

            mCurItemCount += dataset.size();

            setLoadComplete();
        }
    }

    protected abstract void noDataLoaded(boolean firstPage);

    /**
     * 상단에 헤더뷰를 포함하여 표현하는 커스텀 RecyclerView 어댑터
     */
    public abstract class MyRecyclerViewAdapterWithHeader extends MyRecyclerViewAdapter {

        public MyRecyclerViewAdapterWithHeader(RecyclerViewAdapterHelper recyclerViewAdapterHelper) {
            super(recyclerViewAdapterHelper);
        }

        @Override
        public AbsRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == VIEW_TYPE_HEADER) {
                View view = createHeaderView(parent);
                return new HeaderViewHolder(view);
            } else {
                return super.onCreateViewHolder(parent, viewType);
            }
        }

        @Override
        public void onBindViewHolder(AbsRecyclerViewHolder holder, int position) {
            if(holder instanceof HeaderViewHolder) {
                bindHeaderView();
            } else if(holder instanceof ProgressViewHolder) {
                super.onBindViewHolder(holder, position);
            } else {
                mRecyclerViewAdapterHelper.onBindViewHolder(holder, position, mDataset.getData(position - 1));
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? VIEW_TYPE_HEADER :
                    mDataset.getData(position - 1) != null ? VIEW_TYPE_ITEM : VIEW_TYPE_LOADING;
        }

        @Override
        public int getItemCount() {
            return mDataset.length() + 1;
        }

        @Override
        public long getItemId(int position) {
            return mDataset.getItemId(position - 1);
        }

        public void addData(Object data) {
            mDataset.addData(data);
            notifyItemInserted(getItemCount() - 1);
        }

        public void remove(int position) {
            mDataset.removeData(position - 1);
            notifyItemRemoved(getItemCount());
        }

        public abstract void bindHeaderView();

        public abstract View createHeaderView(ViewGroup parent);
    }

    /**
     * 헤더 뷰 없는 기본적인 커스텀 RecyclerView 어댑터
     */
    public class MyRecyclerViewAdapter extends RecyclerView.Adapter<AbsRecyclerViewHolder> {
        final int VIEW_TYPE_HEADER = 0;
        final int VIEW_TYPE_ITEM = 1;
        final int VIEW_TYPE_LOADING = 2;

        RecyclerViewAdapterHelper.RecyclerViewColletionData mDataset;
        RecyclerViewAdapterHelper mRecyclerViewAdapterHelper;

        public MyRecyclerViewAdapter(RecyclerViewAdapterHelper recyclerViewAdapterHelper) {
            this.mRecyclerViewAdapterHelper = recyclerViewAdapterHelper;
            this.mDataset = mRecyclerViewAdapterHelper.provideData();
        }

        public RecyclerViewAdapterHelper.RecyclerViewColletionData getDataset() {
            return mDataset;
        }

        void setDataset(Object dataset) {
            BaLog.i();
            mDataset.setDataset(dataset);
            notifyDataSetChanged();
        }

        class ProgressViewHolder extends AbsRecyclerViewHolder {
            ProgressBar progressBar;

            ProgressViewHolder(View v) {
                super(v);
                progressBar = (ProgressBar) v.findViewById(R.id.loading_progress);
            }
        }

        class HeaderViewHolder extends AbsRecyclerViewHolder {
            HeaderViewHolder(View v) {
                super(v);
            }
        }

        void addDataset(Object dataset) {
            BaLog.i();
            int itemCount = mDataset.addDataset(dataset);
            notifyItemRangeInserted(mCurItemCount + 1, itemCount);
        }

        public void addData(Object data) {
            mDataset.addData(data);
            notifyItemInserted(getItemCount());
        }

        public void remove(int position) {
            mDataset.removeData(position);
            notifyItemRemoved(position);
        }

        @Override
        public int getItemViewType(int position) {
            return mDataset.getData(position) != null ? VIEW_TYPE_ITEM : VIEW_TYPE_LOADING;
        }

        @Override
        public AbsRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            AbsRecyclerViewHolder vh = null;
            if(viewType == VIEW_TYPE_ITEM) {
                vh = mRecyclerViewAdapterHelper.createViewHolder(parent);
            } else {
                // 로딩 뷰
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_progress, parent, false);
                vh = new ProgressViewHolder(view);
            }

            return vh;
        }

        @Override
        public void onBindViewHolder(AbsRecyclerViewHolder holder, int position) {
            if(holder instanceof ProgressViewHolder) {
                ProgressViewHolder loadingViewHolder = (ProgressViewHolder) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            } else {
                mRecyclerViewAdapterHelper.onBindViewHolder(holder, position, mDataset.getData(position));
            }
        }

        @Override
        public int getItemCount() {
            return mDataset.length();
        }

        @Override
        public long getItemId(int position) {
            return mDataset.getItemId(position);
        }
    }



    private class MyScrollListener extends RecyclerView.OnScrollListener {
        private int pastVisiblesItems, visibleItemCount, totalItemCount;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//            BaLog.i("dy=" + dy);

            if(dy > 0) {
                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

//                BaLog.i("isCanLoadMore=" + isCanLoadMore + ", mCurItemCount=" + mCurItemCount + ", mTotalImteCount=" + mTotalItemCount
//                + ", visibleItemCount=" + visibleItemCount + ", pastVisibleItems=" + pastVisiblesItems);

                if (isCanLoadMore && mCurItemCount < mTotalItemCount) {
                    if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        isCanLoadMore = false;
                        BaLog.v("Last Item Wow !");
                        loadMore();
                    }
                }
            }
        }
    }
}
