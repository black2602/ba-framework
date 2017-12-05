package com.blackangel.baframework.ui.view.recyclerview;

/**
 * Created by Finger-kjh on 2017-12-05.
 */

import android.view.View;
import android.view.ViewGroup;

/**
 * 상단에 헤더뷰를 포함하여 표현하는 커스텀 RecyclerView 어댑터
 */
public abstract class MyRecyclerViewAdapterWithHeader<T> extends MyRecyclerViewAdapter<T> {
    protected boolean mIsShownHeader = false;

    public MyRecyclerViewAdapterWithHeader(MyRecyclerViewAdapterHelper<T> myRecyclerViewAdapterHelper) {
        super(myRecyclerViewAdapterHelper);
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
        if(getItemViewType(position) == VIEW_TYPE_HEADER) {
            bindHeaderView((HeaderViewHolder) holder);

        } else if(getItemViewType(position) == VIEW_TYPE_LOADING) {
            super.onBindViewHolder(holder, position);

        } else {
            if(mIsShownHeader) {
                mMyRecyclerViewAdapterHelper.onBindViewHolder(holder, position, mDataContainer.getData(position - 1));
            } else {
                mMyRecyclerViewAdapterHelper.onBindViewHolder(holder, position, mDataContainer.getData(position));
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(mIsShownHeader) {
            if(position == 0) {
                return VIEW_TYPE_HEADER;
            } else {
                return mDataContainer.getData(position - 1) != null ? VIEW_TYPE_ITEM : VIEW_TYPE_LOADING;
            }
        } else {
            return mDataContainer.getData(position) != null ? VIEW_TYPE_ITEM : VIEW_TYPE_LOADING;
        }
    }

    @Override
    public int getItemCount() {
        return mIsShownHeader ? mDataContainer.length() + 1 : mDataContainer.length();
    }

    @Override
    public long getItemId(int position) {
        return mIsShownHeader ? mDataContainer.getItemId(position - 1) : mDataContainer.getItemId(position);
    }

    @Override
    public T getData(int position) {
        if(mIsShownHeader) {
            return super.getData(position - 1);
        } else {
            return super.getData(position);
        }
    }

    public void addData(T data) {
        super.addData(data);
    }

    public void remove(int position) {
        if(mIsShownHeader) {
            mDataContainer.removeData(position - 1);
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
