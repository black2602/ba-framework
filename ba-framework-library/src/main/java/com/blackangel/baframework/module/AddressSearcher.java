package com.blackangel.baframework.module;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.blackangel.baframework.R;
import com.blackangel.baframework.core.base.BaseActivity;
import com.blackangel.baframework.core.base.BaseListFragment;
import com.blackangel.baframework.logger.MyLog;
import com.blackangel.baframework.ui.dialog.custom.AbstractDialogCustomViewInflater;
import com.blackangel.baframework.ui.dialog.custom.DialogCustomViewInflater;
import com.blackangel.baframework.ui.dialog.custom.DialogItems;
import com.blackangel.baframework.ui.view.recyclerview.AbsRecyclerViewHolder;
import com.blackangel.baframework.ui.view.recyclerview.RecyclerViewAdapterHelper;
import com.blackangel.baframework.ui.view.recyclerview.SimpleLineDividerItemDecoration;
import com.blackangel.baframework.util.ScreenUtil;
import com.blackangel.baframework.util.ViewUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * 우편번호 검색 모듈
 * @author finger
 *
 */
public class AddressSearcher {

    private static final String TAG_ADDRESS_SEARCH_FRAGMENT = "fragAddrSearch";
    private static final String TAG_SEARCH_ADDR = "dlgSearchAddr";
    private static final String TAG_SELECT_ADDR = "selectAddr";

    public interface OnAddressSelectedListener {
        void onSelected(String postNo, String address);
    }

    private BaseActivity mActivity;
    private EditText mEditSearch;
    private String mSearch;
    private AddressSearchFragment mFragment;

    public AddressSearcher(BaseActivity activity) {
        mActivity = activity;
    }

    private void onClickSearch(String search) {
        if (search.equals(mSearch)) {
            // 같은 검색어 또 눌렀을 때
            return;
        } else if (search.length() <= 1) {
            mActivity.showAlertDialog(mActivity.getResources().getString(R.string.addr_input_validation_error));
            return;
        } else {
            mFragment.initSearchMembers();
        }

        ViewUtil.hideSoftKeyboard(mActivity, mEditSearch);
        mSearch = search;
        mFragment.setSearchKeyword(search);

        mFragment.requestList(false);
    }

    public void showDialogSearchAddress(final OnAddressSelectedListener onAddressSelectedListener) {
        DialogItems dialogItems = new DialogItems.Builder(mActivity)
                .setTitle(R.string.addr_search)
                .setCustomViewInflater(new DialogCustomViewInflater(new AbstractDialogCustomViewInflater() {
                    @Override
                    public View inflateContentView() {
                        View view = mActivity.getLayoutInflater().inflate(R.layout.dialog_address_search, null);

                        mFragment = (AddressSearchFragment) mActivity.getSupportFragmentManager().findFragmentByTag(TAG_ADDRESS_SEARCH_FRAGMENT);
                        mFragment.setOnAddressSelectedListener(onAddressSelectedListener);
                        MyLog.d("mFragment=" + mFragment);

                        mEditSearch = (EditText) view.findViewById(R.id.edit_search_data);

                        Button search = (Button) view.findViewById(R.id.button_address_search);
                        search.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onClickSearch(mEditSearch.getText().toString().trim());
                            }
                        });

                        ViewUtil.setEditorActionForButtonClick(mEditSearch, search);

                        return view;
                    }
                }))
                .build();
//
//        CustomDialogFragment customDialog = CustomDialogFragment.newInstance(dialogItems);
//        customDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                MyLog.i();
//                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
//                ft.remove(mFragment);
//                ft.commitAllowingStateLoss();
//
//                mFragment = null;
//            }
//        });
//        mActivity.showCustomDialog(dialogItems, TAG_SEARCH_ADDR);

        mActivity.showCustomDialog(dialogItems, TAG_SEARCH_ADDR, new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                MyLog.i();
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.remove(mFragment);
                ft.commitAllowingStateLoss();

                mFragment = null;
            }
        });
    }

    public static class AddressSearchFragment extends BaseListFragment implements RecyclerViewAdapterHelper<List<Address>, Address>, BaseListFragment.ListRowClicker {

        private OnAddressSelectedListener mOnAddressSelectedListener;
        private String mSearchKeyword;

        public void setOnAddressSelectedListener(OnAddressSelectedListener listener) {
            mOnAddressSelectedListener = listener;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setListRowClicker(this);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            int padding = ScreenUtil.convertDpToPixel(getActivity(), 8);
            mRecyclerView.setPadding(padding, 0, padding, 0);
        }

        @Override
        public void requestList(boolean morePage) {
//            Retrofit globalRetrofit = RetrofitRunner.getGlobalRetrofit();
//            CommonService commonService = globalRetrofit.create(CommonService.class);
//
//            ApiExecuter.ApiProgressListener apiProgressListener = !morePage ? this : null;
//
//            RetrofitRunner.executeAsync(apiProgressListener,
//                    commonService.getAddressList(mSearchKeyword, mCurPage, 20),
//                    new ApiModelResultListener<ListModel<Address>>() {
//                        @Override
//                        public void onSuccess(ListModel<Address> response) {
//
//                            setTotalItemCount(response.getTotalItemCount());
//                            populateList(response.getItemList());
//                        }
//
//                        @Override
//                        public void onFail(String url, String message, Throwable throwable) {
//                            showToast(message);
//                            hideLoadingFooter();
//                        }
//                    });

        }


        private List<Address> testResponse(int start) {
            JSONArray jsonArray = new JSONArray();

            List<Address> addressInfoList = new ArrayList<>();

            for(int i=start; i<start+20; i++) {
                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("post_number", (long) i);
                    jsonObject.put("street_addr", "경기도 양주시 백석읍 꿈나무로 6-" + i);
                    jsonObject.put("jibun_addr", "경기도 양주시 백석읍 방성리 지번-" + i);

                    addressInfoList.add(new Address("" + i, "경기도 양주시 백석읍 꿈나무로 6-" + i, "경기도 양주시 백석읍 방성리 지번-" + i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                jsonArray.put(jsonObject);
            }

            return addressInfoList;
        }

        @Override
        protected MyRecyclerViewAdapter createListAdapter() {
            return new MyRecyclerViewAdapter(this);
        }

        @Override
        protected void addRecyclerViewDecoration(RecyclerView recyclerView) {
            recyclerView.addItemDecoration(new SimpleLineDividerItemDecoration(getActivity()));
        }

        @Override
        protected void noDataLoaded(boolean firstPage) {
            if(firstPage) {
                getBaseActivity().showAlertDialog(R.string.error_not_found_search_result);
            }
        }

//        @Nullable
//        @Override
//        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//            View view = super.onCreateView(inflater, container, savedInstanceState);
//
//            ListView listView = (ListView) mCollectionView.getView();
//            listView.setDivider(getContext().getResources().getDrawable(R.color.back_border_color));
//            listView.setDividerHeight(ScreenUtil.convertDpToPixel(getContext(), 1));
//
//            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mEmptyLayout.getLayoutParams();
//            layoutParams.topMargin = ScreenUtil.convertDpToPixel(getContext(), 16);
//            layoutParams.bottomMargin = layoutParams.topMargin * 2;
//            mEmptyLayout.setLayoutParams(layoutParams);
//
//            return view;
        /**
         * 검색 관련 변수 초기화
         */
        private void initSearchMembers() {
            initPagination();
            mSearchKeyword = null;
        }

//        }


        private DialogItems makeDialogItemAddressSelect(final Address addressInfo) {
            final BaseActivity activity = (BaseActivity) getActivity();

            DialogItems dialogItems = new DialogItems.Builder(activity)
                    .setTitle(R.string.select_addr)
                    .setCustomViewInflater(new DialogCustomViewInflater(new AbstractDialogCustomViewInflater() {
                        @Override
                        public View inflateContentView() {
                            View customView = View.inflate(activity, R.layout.dialog_address_select_view, null);

                            RadioGroup rg = (RadioGroup) customView.findViewById(R.id.rg_addr_select);
                            RadioButton btnStreetAddr = (RadioButton) rg.findViewById(R.id.btn_street_addr);
                            RadioButton btnJibunAddr = (RadioButton) rg.findViewById(R.id.btn_jibun_addr);

                            btnStreetAddr.setText(String.format(activity.getString(R.string.street_addr_format), addressInfo.getStreetAddress()));
                            btnJibunAddr.setText(String.format(activity.getString(R.string.jibun_addr_format), addressInfo.getLegacyAddress()));

                            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(RadioGroup group, int checkedId) {
                                    String addr;

                                    if(checkedId == R.id.btn_street_addr) {
                                        // 도로명 주소 선택
                                        addr = addressInfo.getStreetAddress();
                                    } else {
                                        // 지번 주소 선택
                                        addr = addressInfo.getLegacyAddress();
                                    }

                                    if(mOnAddressSelectedListener != null) {
                                        mOnAddressSelectedListener.onSelected(addressInfo.getPostalCode(), addr);
                                    }

                                    initSearchMembers();
                                    activity.removeCustomDialog(TAG_SELECT_ADDR);
                                    activity.removeCustomDialog(TAG_SEARCH_ADDR);
                                    activity.removeFragment(AddressSearchFragment.this);
                                }
                            });

                            return customView;
                        }
                    }))
                    .setShowCloseButton(true, null)
                    .build();

            return dialogItems;
        }

        public void setSearchKeyword(String searchKeyword) {
            mSearchKeyword = searchKeyword;
        }

        @Override
        public AbsRecyclerViewHolder createViewHolder(ViewGroup parent) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.adapter_address_list, parent, false);

            ViewGroup row = (ViewGroup) v.findViewById(R.id.row);
            TextView postNumber = (TextView) v.findViewById(R.id.text_post_number);
            TextView txtAddr1 = (TextView) v.findViewById(R.id.text_address);
            TextView txtAddr2 = (TextView) v.findViewById(R.id.text_address2);

            return new ViewHolder(v, row, postNumber, txtAddr1, txtAddr2);
        }

        @Override
        public void onBindViewHolder(AbsRecyclerViewHolder holder, int position, Address data) {
            ((ViewHolder) holder).mPostNumber.setText(data.getPostalCode());
            ((ViewHolder) holder).mTxtStreetAddr.setText(data.getStreetAddress());
            ((ViewHolder) holder).mTxtJibunAddr.setText(data.getLegacyAddress());
        }

        @Override
        public RecyclerViewColletionData<List<Address>, Address> provideRecyclerViewColletionData() {
            return new ListModelRecyclerViewCollectionData<Address>();
        }

//        @Override
//        public void populateList(List<Address> dataset) {
//            if(mCurPage > 1) {
//                mRecyclerViewAdapter.addDataset(dataset);
//            } else {
//                mRecyclerViewAdapter.setDataset(dataset);
//            }
//
//            mCurItemCount += dataset.size();
//
//            setLoadComplete();
//        }


        public static AddressSearchFragment newInstance() {
            return new AddressSearchFragment();
        }

        @Override
        public void onListRowClick(int position, Object object) {
            Address addressInfo = (Address) mRecyclerViewAdapter.getData(position);

            if(mOnAddressSelectedListener != null) {
                mOnAddressSelectedListener.onSelected(addressInfo.getPostalCode(), addressInfo.getStreetAddress());
            }

            initSearchMembers();
            getBaseActivity().removeCustomDialog(TAG_SELECT_ADDR);
            getBaseActivity().removeCustomDialog(TAG_SEARCH_ADDR);
            getBaseActivity().removeFragment(AddressSearchFragment.this);
        }

        static class ViewHolder extends AbsRecyclerViewHolder {
            public ViewGroup mRow;
            public TextView mPostNumber;
            public TextView mTxtStreetAddr;
            public TextView mTxtJibunAddr;

            public ViewHolder(View rowLayout, ViewGroup layout, TextView postNumber, TextView txtStreetAddr, TextView txtJibunAddr) {
                super(rowLayout);
                this.mRow = layout;
                this.mPostNumber = postNumber;
                this.mTxtStreetAddr = txtStreetAddr;
                this.mTxtJibunAddr = txtJibunAddr;
            }
        }
    }

//    public static class AddressListAdapter extends BaseAdapter {
//        private LayoutInflater mInflater;
//        private List<Address> mData;
//
//        public AddressListAdapter(BaseActivity activity) {
//            mInflater = LayoutInflater.from(activity);
//        }
//
//        public AddressListAdapter(BaseActivity activity, List<Address> data) {
//            mInflater = LayoutInflater.from(activity);
//            mData = data;
//        }
//
//        @Override
//        public int getCount() {
//            return mData.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return mData.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return 0;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder viewHolder;
//            if (convertView == null) {
//                convertView = mInflater.inflate(R.layout.adapter_address_list, null);
//
//                viewHolder = new ViewHolder();
//
//                viewHolder.mPostNumber = (TextView) convertView.findViewById(R.id.text_post_number);
//                viewHolder.mAddress1 = (TextView) convertView.findViewById(R.id.text_address);
//                viewHolder.mAddress2 = (TextView) convertView.findViewById(R.id.text_address2);
//
//                convertView.setTag(viewHolder);
//            }
//            else {
//                viewHolder = (ViewHolder) convertView.getTag();
//            }
//
//            Address addressInfo = mData.get(position);
//
//            // 도로명지번 주소검색 API(CMSYST31) 응답
//            String postNo = addressInfo.postNo;
//            String streetAddr = addressInfo.streetAddr;
//            String jibunAddr = addressInfo.jibunAddr;
//
//            viewHolder.mPostNumber.setText(postNo);
//            viewHolder.mAddress1.setText(streetAddr);
//            viewHolder.mAddress2.setText(jibunAddr);
//
//            return convertView;
//        }
//
//        public void setList(List<Address> data) {
//            mData = data;
//
//            notifyDataSetChanged();
//        }
//
//        public class ViewHolder {
//            TextView mPostNumber;
//            TextView mAddress1;
//            TextView mAddress2;
//        }
//    }

}
