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
import android.widget.TextView;

import com.blackangel.baframework.R;
import com.blackangel.baframework.logger.MyLog;
import com.blackangel.baframework.ui.view.recyclerview.MyRecyclerViewAdapter;

import java.util.List;

/**
 * Created by KimJeongHun on 2016-09-11.
 */
public abstract class BaseListFragment extends BaseFragment {
    public static final int DEFAULT_PAGE_SIZE = 20;

    protected RecyclerView mRecyclerView;
    protected MyRecyclerViewAdapter mRecyclerViewAdapter;
    protected LinearLayoutManager mLayoutManager;
    protected ViewGroup mEmptyView;

    protected ListRowClicker mListRowClicker;

    protected int mCurPage = 1;
    protected int mCurItemCount = 0;    // 현재까지 표시된 아이템 갯수
    protected int mTotalItemCount = 0;  // 표시해야하는 총 토탈 아이템 갯수

    private boolean isCanLoadMore = true;               // 계속 로드(페이징) 가능한지 여부
    protected boolean isInitialLoadCompleted = false;   // 최초 한번 데이터 로드가 완료되었는지 여부
    protected boolean isAnimating = false;              // 프래그먼트가 애니메이션 중인지 여부

    public abstract void requestList(boolean showOutProgress);
    protected abstract MyRecyclerViewAdapter createRecyclerViewAdapter();

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

        mRecyclerViewAdapter = createRecyclerViewAdapter();
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
     * (뷰페이저를 사용하지 않는 경우 호출되지 않음)
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
