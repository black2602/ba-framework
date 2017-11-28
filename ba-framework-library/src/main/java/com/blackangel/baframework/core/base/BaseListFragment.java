package com.blackangel.baframework.core.base;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blackangel.baframework.R;
import com.blackangel.baframework.logger.MyLog;
import com.blackangel.baframework.ui.view.recyclerview.AbsRecyclerViewHolder;
import com.blackangel.baframework.ui.view.recyclerview.RecyclerViewAdapterHelper;

import java.util.List;

/**
 * Created by KimJeongHun on 2016-09-11.
 */
public abstract class BaseListFragment extends BaseFragment {
    public static final String TAG_LIST_ROW = "listRow";
    public static final int DEFAULT_PAGE_SIZE = 20;

    protected RecyclerView mRecyclerView;
    protected MyRecyclerViewAdapter mRecyclerViewAdapter;
    protected LinearLayoutManager mLayoutManager;
    protected ViewGroup mEmptyView;

    protected ListRowClicker mListRowClicker;

    protected int mCurPage = 1;
    protected int mCurItemCount = 0;    // 리스트의 현재까지 페이징 된 아이템 갯수
    protected int mTotalItemCount = 0;  // 리스트의 모든 페이지 토탈 아이템 갯수

    private boolean isCanLoadMore = true;
    protected boolean isInitialLoadCompleted = false;
    protected boolean isAnimating = false;          // 프래그먼트가 애니메이션 중인지 여부

    public abstract void requestList(boolean showOutProgress);
    protected abstract MyRecyclerViewAdapter createListAdapter();

    /**
     * RecyclerView 에 추가로 데코레이션 한다.
     * Divider, Padding 설정 등
     */
    protected abstract void addRecyclerViewDecoration(RecyclerView recyclerView);

    public void setListRowClicker(ListRowClicker listRowClicker) {
        mListRowClicker = listRowClicker;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MyLog.i(this.getClass().getSimpleName());
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

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        try {
            Animation anim = AnimationUtils.loadAnimation(getActivity(), nextAnim);
            anim.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                    isAnimating = true;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // 최초 프래그먼트 전환 애니메이션이 끝난 후 요청한다.
                    isAnimating = false;
                }
            });

            return anim;
        } catch (Resources.NotFoundException e) {
            MyLog.w(e.getMessage());
        }

        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    protected void showEmptyLayout() {
        if(mEmptyView.getChildCount() == 0) {
            mEmptyView.addView(createEmptyView());
        }

        mEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    protected View createEmptyView() {
        View view = View.inflate(getContext(), R.layout.default_empty_view, null);
        TextView emptyTxt = (TextView) view.findViewById(R.id.txt_no_data);
        emptyTxt.setText(getEmptyText());

        return view;
    }

    protected String getEmptyText() {
        return getString(R.string.empty_data);
    }

    public void showLoadingFooter() {
        mRecyclerViewAdapter.addData(null);
    }

    public void hideLoadingFooter() {
        int loadingFooterIdx = mRecyclerViewAdapter.getItemCount() - 1;

        if(loadingFooterIdx < 0)
            loadingFooterIdx = 0;

        mRecyclerViewAdapter.remove(loadingFooterIdx);
    }

    protected void loadMore() {
        mCurPage++;

        // 하단 프로그레스바 추가
        showLoadingFooter();
        requestList(false);
    }

    protected void setTotalItemCount(int totalCount) {
        mTotalItemCount = totalCount;

        MyLog.d("mCurItemCount=" + mCurItemCount + ", mCurPage=" + mCurPage + ", mTotalItemCount=" + mTotalItemCount);
    }

    public void initPagination() {
        mCurPage = 1;
        mTotalItemCount = 0;
        mCurItemCount = 0;
    }

    public void dataLoaded() {
        isCanLoadMore = true;
        isInitialLoadCompleted = true;
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

            dataLoaded();
        }
    }

    protected void noDataLoaded(boolean firstPage) {
        isInitialLoadCompleted = true;
        if(firstPage) {
            showEmptyLayout();
        }

        hideProgress();
    }

    protected View inflateRowView(int layoutResId, ViewGroup parent) {
        View v = getActivity().getLayoutInflater().inflate(layoutResId, parent, false);
        v.setTag(TAG_LIST_ROW);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mRecyclerView.getChildAdapterPosition(v);
                Object data = mRecyclerViewAdapter.getData(position);

                MyLog.i("onListRowClick position = " + position);

                if(mListRowClicker != null)
                    mListRowClicker.onListRowClick(position, data);
            }
        });

        return v;
    }

    /**
     * 뷰페이저에 의해 프래그먼트가 추가될 때 데이터를 미리 로드하지 않고, 실제 사용자에게 보여질 때 로드한다.
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        MyLog.i("class=" + this.getClass().getSimpleName() + ", isVisibleToUser=" + isVisibleToUser
                + ", isInitialLoadCompleted=" + isInitialLoadCompleted
                + ", isAnimating=" + isAnimating);

        if(isVisibleToUser) {
            // 실제로 유저에게 보여질때 & 애니메이션이 끝났을때만 리스트를 요청하고, 보여지고 있지 않거나 애니메이션 중일때는 0.5 초 대기 후 다시 시도한다.
            if(isVisible() && !isAnimating) {
                requestList(true);
            } else {
                mBaseListFragmentHandler.sendEmptyMessageDelayed(0, 500);
            }
        }
    }

    private Handler mBaseListFragmentHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            MyLog.i("mBaseListFragmentHandler handleMessage isVisible() = " + isVisible());

            if(isVisible()) {
                requestList(true);
            } else {
                mBaseListFragmentHandler.sendEmptyMessageDelayed(0, 500);
            }
        }
    };

    @Override
    public void onDetach() {
        super.onDetach();
        MyLog.i(this.getClass().getSimpleName());
        isInitialLoadCompleted = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MyLog.i();
        initPagination();
    }

    protected boolean isEmptyData() {
        int count = mRecyclerViewAdapter.getItemCount();
        MyLog.i("itemCount = " + count);
        return count <= 0;
    }

    /**
     * 상단에 헤더뷰를 포함하여 표현하는 커스텀 RecyclerView 어댑터
     */
    public abstract class MyRecyclerViewAdapterWithHeader extends MyRecyclerViewAdapter {
        protected boolean mIsShownHeader = false;

        public MyRecyclerViewAdapterWithHeader(RecyclerViewAdapterHelper recyclerViewAdapterHelper) {
            super(recyclerViewAdapterHelper);
            mIsShownHeader = getIsWillDisplayHeader();
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
                bindHeaderView((HeaderViewHolder) holder);
            } else if(holder instanceof ProgressViewHolder) {
                super.onBindViewHolder(holder, position);
            } else {
                if(mIsShownHeader) {
                    mRecyclerViewAdapterHelper.onBindViewHolder(holder, position, mDataset.getData(position - 1));
                } else {
                    mRecyclerViewAdapterHelper.onBindViewHolder(holder, position, mDataset.getData(position));
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            if(mIsShownHeader) {
                if(position == 0) {
                    return VIEW_TYPE_HEADER;
                } else {
                    return mDataset.getData(position - 1) != null ? VIEW_TYPE_ITEM : VIEW_TYPE_LOADING;
                }
            } else {
                return mDataset.getData(position) != null ? VIEW_TYPE_ITEM : VIEW_TYPE_LOADING;
            }
        }

        @Override
        public int getItemCount() {
            return mIsShownHeader ? mDataset.length() + 1 : mDataset.length();
        }

        @Override
        public long getItemId(int position) {
            return mIsShownHeader ? mDataset.getItemId(position - 1) : mDataset.getItemId(position);
        }

        @Override
        public Object getData(int position) {
            if(mIsShownHeader) {
                return super.getData(position - 1);
            } else {
                return super.getData(position);
            }
        }

        public void addData(Object data) {
            super.addData(data);
        }

        public void remove(int position) {
            if(mIsShownHeader) {
                mDataset.removeData(position - 1);
                notifyItemRemoved(position - 1);
            } else {
                super.remove(position);
            }

        }

        public abstract void bindHeaderView(HeaderViewHolder holder);

        public abstract View createHeaderView(ViewGroup parent);

        public abstract boolean getIsWillDisplayHeader();

        public boolean isShownHeader() {
            return mIsShownHeader;
        }

        public void setShownHeader(boolean shownHeader) {
            mIsShownHeader = shownHeader;
        }

        public void hideHeader() {
            setShownHeader(false);
            notifyDataSetChanged();
        }
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
            this.mDataset = mRecyclerViewAdapterHelper.provideRecyclerViewColletionData();
        }

        public RecyclerViewAdapterHelper.RecyclerViewColletionData getDataset() {
            MyLog.i("mDataset=" + mDataset);
            return mDataset;
        }

        void setDataset(Object dataset) {
            MyLog.i();
            mDataset.setDataset(dataset);
            notifyDataSetChanged();
        }

        public Object getData(int position) {
            return mDataset.getData(position);
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

        void addDataset(Object dataset) {
            MyLog.i();
            int itemCount = mDataset.addDataset(dataset);
            notifyItemRangeInserted(mCurItemCount + 1, itemCount);
        }

        public void addData(Object data) {
            mDataset.addData(data);
            notifyItemInserted(getItemCount());
        }

        public void remove(int position) {
            try {
                mDataset.removeData(position);
                notifyItemRemoved(position);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();

                // 여기서 에러나는것은 무시
            }
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
//            MyLog.i("dy=" + dy);

            if(dy > 0) {
                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

//                MyLog.i("isCanLoadMore=" + isCanLoadMore + ", mCurItemCount=" + mCurItemCount + ", mTotalImteCount=" + mTotalItemCount
//                + ", visibleItemCount=" + visibleItemCount + ", pastVisibleItems=" + pastVisiblesItems);

                if (isCanLoadMore && mCurItemCount < mTotalItemCount) {
                    if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        isCanLoadMore = false;
                        MyLog.v("Last Item Wow !");
                        loadMore();
                    }
                }
            }
        }
    }

    public interface ListRowClicker<T> {
        void onListRowClick(int position, T object);
    }
}