package com.blackangel.baframework.core.base;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.blackangel.baframework.R;
import com.blackangel.baframework.core.model.ApiModelLoadError;
import com.blackangel.baframework.core.model.BaseError;
import com.blackangel.baframework.core.mvvm.viewmodel.BaseListViewModel;
import com.blackangel.baframework.core.mvvm.viewmodel.BaseListViewModelPropertyChangedCallback;
import com.blackangel.baframework.core.ui.recyclerview.MyRecyclerViewAdapter;
import com.blackangel.baframework.core.ui.recyclerview.MyRecyclerViewAdapterHelper;
import com.blackangel.baframework.databinding.FragmentBaseListBinding;
import com.blackangel.baframework.logger.MyLog;

import java.util.List;

public abstract class BaseListFragmentMvvm<T> extends BaseFragment implements MyRecyclerViewAdapterHelper<T> {
    protected static final String ARG_IS_AUTO_REQUEST_DATA_ON_START = "autoReqDataOnStart";
    protected static final String ARG_IS_USING_VIEWPAGER = "isUsingViewPager";
    protected static final String ARG_IS_REQUEST_LIST_ON_EVERY_VISIBLE = "isRequestListOnEveryVisible";

    protected RecyclerView mRecyclerView;
    protected MyRecyclerViewAdapter<T> mRecyclerViewAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    private boolean isCanLoadMore = true;                   // 계속 로드(페이징) 가능한지 여부
    protected boolean isInitialLoadCompleted = false;       // 최초 한번 데이터 로드가 완료되었는지 여부
    protected boolean isAnimating = false;                  // 프래그먼트가 애니메이션 중인지 여부

    private boolean isIdleOfViewPager = true;               // 뷰페이저에서 사용시 idle 한지 여부
    private boolean isAutoRequestDataOnStart = true;        // 시작과 동시에 바로 데이터 요청할지 여부. 디폴트는 true
    private boolean isUsingInViewPager = false;             // 뷰페이저에서 사용중인지 여부
    private boolean isRequestListOnEveryVisible = false;    // 매번 사용자에게 보여질때마다 리스트를 새로 요청할지 여부

    private FragmentBaseListBinding mBaseListBinding;
    protected BaseListViewModel<T> mListViewModel;
    protected BaseListViewModelPropertyChangedCallback mListViewModelPropertyChangedCallback;
    private Snackbar mSnackbar;

    /**
     * 데이터를 요청한다.
     * (뷰페이저의 PagerFragmentAdapter 안의 Fragment 로 포함되어 있을때는 setUserVisibleHint 에 의해 자동으로 호출된다.
     * - isRequestListOnEveryVisible 가 true 일 때)
     *
     * @param showOutProgress 리스트내에 프로그레스가 아닌 바깥영역에서 프로그레스를 보여줘야 할지 여부
     */
    public void requestList(boolean showOutProgress) {
        MyLog.i();
        mListViewModel.loadListAsync(showOutProgress, mListViewModel.getCurPage(), mListViewModel.getRequestPageSize());
    }

    public void setRequestPageSize(int pageSize) {
        this.mListViewModel.setRequestPageSize(pageSize);
    }

    /**
     * 페이지 정보를 초기화하고 데이터를 처음부터 새로 요청한다.
     */
    public void requestListWithInitPagination() {
        initPagination();
        requestList(true);
    }

    protected abstract MyRecyclerViewAdapter<T> createRecyclerViewAdapter();

    public void setIdleOfViewPager(boolean idle) {
        MyLog.i(this.getClass().getSimpleName(), "idle="+idle);
        this.isIdleOfViewPager = idle;
    }

    /**
     * RecyclerView 에 추가로 데코레이션 한다. (Divider, Padding 설정 등)
     * 필요시 오버라이드 한다.
     */
    protected void addRecyclerViewDecoration(RecyclerView recyclerView) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            isUsingInViewPager = getArguments().getBoolean(ARG_IS_USING_VIEWPAGER);
            isAutoRequestDataOnStart = getArguments().getBoolean(ARG_IS_AUTO_REQUEST_DATA_ON_START);
            isRequestListOnEveryVisible = getArguments().getBoolean(ARG_IS_REQUEST_LIST_ON_EVERY_VISIBLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MyLog.i(this.getClass().getSimpleName());
        mBaseListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_base_list, container, false);
        mListViewModel = createListViewModel();
        mListViewModelPropertyChangedCallback = createListViewModelPropertyChangedCallback();
        mListViewModel.addOnPropertyChangedCallback(mListViewModelPropertyChangedCallback);
        mBaseListBinding.setViewModel(mListViewModel);
        return mBaseListBinding.getRoot();
    }

    /**
     * 리스트뷰 모델의 프로퍼티 변경 콜백을 생성 후 리턴한다.
     * 기본으로 DefaultBaseListViewModelPropertyChangedCallback 인스턴스를 만들어 사용한다.
     * 필요시 오버라이드 하도록 한다.
     *
     * @return  리스트뷰 모델의 프로퍼티 변경시 수신할 콜백
     */
    protected BaseListViewModelPropertyChangedCallback createListViewModelPropertyChangedCallback() {
        return new DefaultBaseListViewModelPropertyChangedCallback();
    }

    public List<T> getItemList() {
        return mRecyclerViewAdapter.getDataContainer().getDataset();
    }

    /**
     * 기본적인 리스트뷰모델의 속성 변경을 수신하는 Observer 역할을 하는 콜백
     */
    public class DefaultBaseListViewModelPropertyChangedCallback extends BaseListViewModelPropertyChangedCallback {

        @Override
        protected void handleLoadMoreErrorVisiblity(boolean loadMoreErrorVisible) {
            if (loadMoreErrorVisible) {
                showSnackBarErrorOnLoadMore();
            } else {
                hideSnackBarErrorOnLoadMoreIfShown();
            }
        }

        @Override
        protected void handleListModelLoaded() {
//            MyLog.i();
            populateList(mListViewModel.getListModel().getItemList());
            setTotalItemCount(mListViewModel.getListModel().getTotalCount());
        }

        @Override
        protected void handleListDataChanged() {
            showProgress();
            mRecyclerViewAdapter.notifyDataSetChanged();
            hideProgress();
        }

        @Override
        protected void handleProgressBarVisible(boolean progressBarVisible) {
            if (progressBarVisible) {
                showProgress();
            } else {
                hideProgress();
                hideLoadingFooter();
            }
        }

        @Override
        protected void handleModelLoadError(int propertyId, ApiModelLoadError modelLoadError) {
            if(mListViewModel.getCurPage() > 1) {
                showAlertDialog(modelLoadError.getErrMessage());
                showSnackBarErrorOnLoadMore();
            } else {
                showErrorLayout(modelLoadError.getErrMessage());
            }

            onListModelLoadError(modelLoadError);
        }

        @Override
        protected void handleGlobalError(BaseError globalError) {
            if (mActivity != null) {
                mActivity.handleGlobalError(globalError);
            }

            if(mListViewModel.getCurPage() > 1) {
                mListViewModel.setLoadMoreErrorVisible(true);
            } else {
                showErrorLayout(globalError.getErrMessage());
            }

            onListModelLoadError(globalError);
        }
    }

    /**
     * 리스트 모델 로드 중 에러가 발생했을 때 콜백
     * 기본적으로 현재 설정된 타이틀을 다시 액션바에 보여준다.
     * 후처리할 것이 있으면, 오버라이드하여 처리하도록 한다.
     * @param error
     */
    protected void onListModelLoadError(BaseError error) {
        MyLog.w("error = " + error);
        showCurrentTitle();
    }

    public BaseListViewModel<T> getListViewModel() {
        return mListViewModel;
    }

    private void showSnackBarErrorOnLoadMore() {
        mSnackbar = Snackbar.make(getView(),
                getString(R.string.list_load_more_error), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry), v -> loadMore());
        mSnackbar.show();
    }

    private void hideSnackBarErrorOnLoadMoreIfShown() {
        if (mSnackbar != null && mSnackbar.isShown()) {
            mSnackbar.dismiss();
        }
    }

    protected abstract BaseListViewModel<T> createListViewModel();

    public void setListViewModel(BaseListViewModel<T> listViewModel) {
        mListViewModel = listViewModel;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = createLayoutMananger();
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerViewAdapter = createRecyclerViewAdapter();
        mRecyclerViewAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.addOnScrollListener(new MyScrollListener());

        addRecyclerViewDecoration(mRecyclerView);
    }

    protected abstract RecyclerView.LayoutManager createLayoutMananger();

    @Override
    public void onStart() {
        super.onStart();

        if(!isInitialLoadCompleted && isAutoRequestDataOnStart) {
            if(isUsingInViewPager) {
                if(isIdleOfViewPager && getUserVisibleHint()) {
                    requestList(true);
                }

            } else if(isAdded() && getUserVisibleHint()) {
                requestList(true);
            }
        }
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        MyLog.i(this.getClass().getSimpleName());
        try {
            Animation anim = AnimationUtils.loadAnimation(mActivity, nextAnim);
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

                    MyLog.i(BaseListFragmentMvvm.this.getClass().getSimpleName(), "onAnimationEnd");

                    if(isAdded() && isVisible() && getUserVisibleHint() && !isInitialLoadCompleted) {
                        MyLog.i(this.getClass().getSimpleName(), "request List of onAnimationEnd");
                        requestList(true);
                    }
                }
            });

            return anim;
        } catch (Resources.NotFoundException e) {
            MyLog.w(this.getClass().getSimpleName(), e.getMessage());
        }

        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    protected void showEmptyLayout() {
        mBaseListBinding.layoutOverlayView.removeAllViews();
        mBaseListBinding.layoutOverlayView.addView(createEmptyView());

        mBaseListBinding.layoutOverlayView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    protected void showErrorLayout(String errMessage) {
        mBaseListBinding.layoutOverlayView.removeAllViews();
        mBaseListBinding.layoutOverlayView.addView(createErrorView(errMessage));

        mBaseListBinding.layoutOverlayView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    protected View createEmptyView() {
        View view = View.inflate(mActivity, R.layout.list_default_empty_view, null);
        TextView emptyTxt = view.findViewById(R.id.txt_no_data);
        emptyTxt.setText(getEmptyText());

        return view;
    }

    protected View createErrorView(String errMessage) {
        View view = View.inflate(mActivity, R.layout.list_default_error_view, null);
        view.findViewById(R.id.btn_reload).setOnClickListener(v -> requestList(true));

        return view;
    }

    protected String getEmptyText() {
        return getString(R.string.empty_data);
    }

    protected String getErrorText() {
        return getString(R.string.error_unknown);
    }

    public int getCurItemCount() {
        return mListViewModel.getCurItemCount();
    }

    public int getToDisplayTotalItemCount() {
        return mListViewModel.getToDisplayTotalItemCount();
    }

    public void showLoadingFooter() {
        mRecyclerViewAdapter.addData(null);
    }

    public void hideLoadingFooter() {
        int loadingFooterIdx = mRecyclerViewAdapter.getItemCount() - 1;

        if(loadingFooterIdx >= 0) {
            T data = mRecyclerViewAdapter.getData(loadingFooterIdx);

            // 로딩바 뛰어놓고 loadMore 하여 추가적인 데이터 로딩시에 만약 데이터를 추가로 조작하였다면
            // loadingFooterIdx 가 아이템의 마지막이 아닐수도 있다.
            // 따라서 로딩바(null인) 아이템의 위치를 다시 찾는다.
            if(data != null) {
                loadingFooterIdx = mRecyclerViewAdapter.getDataContainer().getDataset().indexOf(null);
            }

            if(loadingFooterIdx >= 0) {
//                MyLog.i("loadingFooter Idx = " + loadingFooterIdx + " removed");
                mRecyclerViewAdapter.remove(loadingFooterIdx);
            }
        }
    }

    protected void loadMore() {
        // 하단 프로그레스바 추가
        showLoadingFooter();
        requestList(false);
    }

    public void setTotalItemCount(int totalCount) {
        mListViewModel.setToDisplayTotalItemCount(totalCount);
    }

    public void initPagination() {
        MyLog.i();
        mListViewModel.setCurPage(1);
        mListViewModel.setToDisplayTotalItemCount(0);
        mListViewModel.setCurItemCount(0);
    }

    public void onDataLoaded() {
        MyLog.i(this.getClass().getSimpleName());
        isCanLoadMore = true;
        isInitialLoadCompleted = true;
    }

    public void populateList(List<T> dataset) {
        MyLog.i("curPage = " + mListViewModel.getCurPage() + ", totalItemCount=" + mListViewModel.getToDisplayTotalItemCount()+
                ", curItemCount=" + mListViewModel.getCurItemCount());

        boolean noData = dataset == null || dataset.size() == 0;

        if(noData) {
            onNoDataLoaded(mListViewModel.getCurPage() == 1);

            if(mListViewModel.getCurPage() > 1)
                hideLoadingFooter();

        } else {
            if(mBaseListBinding.layoutOverlayView.getVisibility() == View.VISIBLE) {
                mBaseListBinding.layoutOverlayView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }

            if (mListViewModel.getCurPage() > 1) {
                hideLoadingFooter();
                mRecyclerViewAdapter.addDataset(dataset);
            } else {
                mRecyclerViewAdapter.setDataset(dataset);
            }

            mListViewModel.increaseCurItemCount(dataset.size());
            onDataLoaded();
        }
    }

    protected void onNoDataLoaded(boolean firstPage) {
        isInitialLoadCompleted = true;
        if(firstPage) {
            showEmptyLayout();
        }

        hideProgress();
    }

    /**
     * 뷰페이저에 의해 프래그먼트가 추가될 때 데이터를 미리 로드하지 않고, 실제 사용자에게 보여질 때 로드한다.
     * (뷰페이저를 사용하지 않는 경우 호출되지 않음. 따라서 isUsingInViewPager 필드는 체크하지 않는다.)
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser) {
            // 실제로 유저에게 보여질때 & 애니메이션이 끝났을때만 리스트를 요청
            if (isIdleOfViewPager && isVisible() && !isAnimating) {
                if (isRequestListOnEveryVisible) {
                    MyLog.i(this.getClass().getSimpleName(), "request List of setUserVisibleHint");
                    requestList(true);
                } else {
                    refreshList();
                }
            }

            if(mListViewModel != null)
                mListViewModel.onVisibleAtViewPager();
        } else {
            onHiddenAtFragmentPagerAdatper();
        }
    }

    /**
     * FragmentPagerAdapter 의 원소로 붙어있는 경우 사용자가 다른페이지로 이동하여 안보일때 콜백
     */
    protected void onHiddenAtFragmentPagerAdatper() {
        MyLog.i(this.getClass().getSimpleName());
        if(isRequestListOnEveryVisible) {
            initPagination();   // 안보일때 페이지 정보 초기화하여 다음번에 보여질때 1페이지부터 다시 데이터 쌓이도록..
        }

        if(mListViewModel != null)
            mListViewModel.onHiddenAtViewPager();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MyLog.i();
        initPagination();
        isInitialLoadCompleted = false;
    }

    protected boolean isEmptyData() {
        int count = mRecyclerViewAdapter.getItemCount();
        MyLog.i("itemCount = " + count);
        return count <= 0;
    }

    protected void refreshList() {
        mRecyclerViewAdapter.notifyDataSetChanged();
    }

    private class MyScrollListener extends RecyclerView.OnScrollListener {
        private int pastVisiblesItems, visibleItemCount, totalItemCount;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if(dy > 0) {
                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();

                if(mLayoutManager instanceof LinearLayoutManager)
                    pastVisiblesItems = ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
                else if(mLayoutManager instanceof GridLayoutManager)
                    pastVisiblesItems = ((GridLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
                else
                    MyLog.w("can not calculate pastVisiblesItems!!");

//                MyLog.i("isCanLoadMore=" + isCanLoadMore + ", mCurItemCount=" + mCurItemCount + ", mTotalImteCount=" + mTotalItemCount
//                        + ", visibleItemCount=" + visibleItemCount + ", pastVisibleItems=" + pastVisiblesItems);

                if (isCanLoadMore && getCurItemCount() < getToDisplayTotalItemCount()) {
                    if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount && getListViewModel().isCanLoadMore()) {
                        isCanLoadMore = false;
                        MyLog.v("Last Item Wow !");
                        mListViewModel.increaseCurPage();
                        loadMore();
                    }
                }
            }
        }
    }
}
