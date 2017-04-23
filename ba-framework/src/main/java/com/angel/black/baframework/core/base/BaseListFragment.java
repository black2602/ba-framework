package com.angel.black.baframework.core.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.angel.black.baframework.R;
import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.ui.view.recyclerview.AbsRecyclerViewHolder;
import com.angel.black.baframework.ui.view.recyclerview.RecyclerViewAdapterData;

/**
 * Created by KimJeongHun on 2016-09-11.
 */
public abstract class BaseListFragment extends BaseFragment {
    public static final String TAG_LIST_ROW = "listRow";

    protected RecyclerView mRecyclerView;
    protected MyRecyclerViewAdapter mRecyclerViewAdapter;
    protected LinearLayoutManager mLayoutManager;
    protected View mLoadingFooterView;

    protected int mCurPage = 1;
    protected int mCurItemCount = 0;    // 리스트의 현재까지 페이징 된 아이템 갯수
    protected int mTotalItemCount = 0;  // 리스트의 모든 페이지 토탈 아이템 갯수

    protected boolean isCanLoadMore = true;

    public abstract void requestList();
    protected abstract MyRecyclerViewAdapter createListAdapter();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerViewAdapter = createListAdapter();
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.addOnScrollListener(new MyScrollListener());

        mLoadingFooterView = inflater.inflate(R.layout.loading_progress, null);

        return view;
    }

    protected void showEmptyLayout() {

    }

    public void showLoadingFooter() {
        mRecyclerViewAdapter.addData(null);
    }

    public void hideLoadingFooter() {
        mRecyclerViewAdapter.remove(mRecyclerViewAdapter.getItemCount() - 1);
    }

    protected void loadMore() {
        mCurPage++;
        requestList();

        //TODO 테스트용 3초 지연
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                BaLog.w("Load More");
//
//            }
//        }, 3000);
    }


    protected void setPagination(int totalCount) {
        mTotalItemCount = totalCount;

        BaLog.d("mCurItemCount=" + mCurItemCount + ", mCurPage=" + mCurPage + ", mTotalItemCount=" + mTotalItemCount);
    }

    public class MyRecyclerViewAdapter extends RecyclerView.Adapter<AbsRecyclerViewHolder> {
        private final int VIEW_TYPE_HEADER = 0;
        private final int VIEW_TYPE_ITEM = 1;
        private final int VIEW_TYPE_LOADING = 2;

        private RecyclerViewAdapterData.RecyclerViewColletionData mDataset;
        private RecyclerViewAdapterData mRecyclerViewAdapterData;

        public MyRecyclerViewAdapter(RecyclerViewAdapterData recyclerViewAdapterData) {
            this.mRecyclerViewAdapterData = recyclerViewAdapterData;
            this.mDataset = mRecyclerViewAdapterData.provideData();
        }

        public RecyclerViewAdapterData.RecyclerViewColletionData getDataset() {
            return mDataset;
        }

        public void setDataset(Object dataset) {
            BaLog.i();
            mDataset.setDataset(dataset);
            notifyDataSetChanged();
        }

        public class ProgressViewHolder extends AbsRecyclerViewHolder {
            public ProgressBar progressBar;

            public ProgressViewHolder(View v) {
                super(v);
                progressBar = (ProgressBar) v.findViewById(R.id.loading_progress);
            }
        }

        public class HeaderViewHolder extends AbsRecyclerViewHolder {
            public HeaderViewHolder(View v) {
                super(v);
            }
        }

        public void addDataset(Object dataset) {
            BaLog.i();
            int itemCount = mDataset.addDataset(dataset);
            notifyItemRangeInserted(mCurItemCount + 1, itemCount);
        }

        public void addData(Object data) {
            mDataset.addData(data);
            notifyItemInserted(getItemCount() - 1);
        }

        public void remove(int position) {
            mDataset.removeData(position - 1);
            notifyItemRemoved(getItemCount());
        }

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? VIEW_TYPE_HEADER :
                    mDataset.getData(position - 1) != null ? VIEW_TYPE_ITEM : VIEW_TYPE_LOADING;
        }

        @Override
        public AbsRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            BaLog.i();
            AbsRecyclerViewHolder vh = null;
            if(viewType == VIEW_TYPE_HEADER) {
                View view = createHeaderView(parent);
                vh = new HeaderViewHolder(view);
            } else if(viewType == VIEW_TYPE_ITEM) {
                vh = mRecyclerViewAdapterData.createViewHolder(parent);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_list_footer, parent, false);
                vh = new ProgressViewHolder(view);
            }

            return vh;
        }

        @Override
        public void onBindViewHolder(AbsRecyclerViewHolder holder, int position) {
            if(holder != null && holder instanceof HeaderViewHolder) {
                bindHeaderView();
            } else if(holder instanceof ProgressViewHolder) {
                ProgressViewHolder loadingViewHolder = (ProgressViewHolder) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            } else {
                mRecyclerViewAdapterData.onBindViewHolder(holder, position, mDataset.getData(position - 1));
            }
        }

        @Override
        public int getItemCount() {
            return mDataset.length() + 1;
        }

        @Override
        public long getItemId(int position) {
            return mDataset.getItemId(position - 1);
        }

    }

    protected abstract void bindHeaderView();

    protected abstract View createHeaderView(ViewGroup parent);

    class MyScrollListener extends RecyclerView.OnScrollListener {
        private int pastVisiblesItems, visibleItemCount, totalItemCount;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//            BaLog.i();
            if(dy > 0) {
                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

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
