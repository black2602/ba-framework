package com.blackangel.baframework.ui.dialog.custom;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blackangel.baframework.R;
import com.blackangel.baframework.core.base.BaseActivity;
import com.blackangel.baframework.logger.MyLog;


/**
 * Created by KimJeongHun on 2016-03-17.
 */
public class CustomDialogFragment extends DialogFragment {
    public static final String TAG = CustomDialogFragment.class.getSimpleName();

    protected static final String ARG_KEY_DIALOG_ITEMS = "dialogItems";
    private static final String ARG_DISPLAY_ORIENTATION = "displayOrientation";
    private static final String ARG_KEY_IS_DISMISS_ON_BUTTON_CLICK = "isDismissOnBtnClick" ;

    /**
     * 이 CustomDialogFragment 가 다이얼로그의 기능은 갖으면서 일반적인 프래그먼트로서 동작할지의 여부 -
     * 이 값에 의해 setShowsDialog(true/false) 를 셋팅한다.
     * (childFragment 를 안에 추가하기 위해서는 일반적인 프래그먼트 라이프사이클로 동작해야한다.)
     */
    private static final String ARG_KEY_IS_SHOW_AS_NORMAL_FRAGMENT = "isShowAsNormFragment";

    public static final int BUTTON_CLOSE = 0;

    protected BaseActivity mActivity;
    private DialogInterface.OnShowListener mOnShowListener;
    private DialogInterface.OnDismissListener mOnDismissListener;

    private DialogCustomViewInflater mDialogCustomViewInflater;
    private DialogInterface.OnClickListener mButtonClickListener;
    private DialogInterface.OnClickListener mCloseClickListener;


    public void setDialogCustomViewInflater(DialogCustomViewInflater dialogCustomViewInflater) {
        mDialogCustomViewInflater = dialogCustomViewInflater;
    }

    public void setButtonClickListener(DialogInterface.OnClickListener buttonClickListener) {
        mButtonClickListener = buttonClickListener;
    }

    public void setCloseClickListener(DialogInterface.OnClickListener closeClickListener) {
        mCloseClickListener = closeClickListener;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }

    public void setOnShowListener(DialogInterface.OnShowListener onShowListener) {
        mOnShowListener = onShowListener;
    }


    public static CustomDialogFragment newInstance(DialogItems dialogItems) {
        CustomDialogFragment customDialog = new CustomDialogFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_KEY_DIALOG_ITEMS, dialogItems);
        args.putBoolean(ARG_KEY_IS_DISMISS_ON_BUTTON_CLICK, dialogItems.isDismissOnButtonClick());
        customDialog.setArguments(args);

        return customDialog;
    }

    public static CustomDialogFragment newInstance(DialogItems dialogItems, boolean showAsNormalFragment) {
        CustomDialogFragment customDialog = new CustomDialogFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_KEY_DIALOG_ITEMS, dialogItems);
        args.putBoolean(ARG_KEY_IS_DISMISS_ON_BUTTON_CLICK, dialogItems.isDismissOnButtonClick());
        args.putBoolean(ARG_KEY_IS_SHOW_AS_NORMAL_FRAGMENT, showAsNormalFragment);
        customDialog.setArguments(args);

        return customDialog;
    }

    public static CustomDialogFragment newInstance(DialogItems dialogItems, BaseCustomDialog.DisplayOrientation displayOrientation) {
        CustomDialogFragment customDialog = new CustomDialogFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_KEY_DIALOG_ITEMS, dialogItems);
        args.putInt(ARG_DISPLAY_ORIENTATION, displayOrientation.ordinal());
        args.putBoolean(ARG_KEY_IS_DISMISS_ON_BUTTON_CLICK, dialogItems.isDismissOnButtonClick());
        customDialog.setArguments(args);

        return customDialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        MyLog.i(this.getClass().getSimpleName());

        if(context instanceof BaseActivity)
            mActivity = (BaseActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i(this.getClass().getSimpleName() + ", savedInstanceState = " + savedInstanceState);

        if(savedInstanceState != null) {
            // DialogFragment 인스턴스가 떠있는 상태에서 액티비티가 시스템의 의해 종료되고 재시작될때 내부적으로 상태를 복구하기 위해
            // savedInstanceState 이 전달된다. 여기서 내부상태를 복원할 수 있지만 클릭리스너 등은 Parcelabel, Serializable 이용해서 복구시 크래쉬 이슈가 있다.
            // 따라서 기존에 클릭리스너가 없었던 경우만 상태를 복구하여 표시하도록 한다.
            if(!savedInstanceState.getBoolean("isAliveRecreate"))
                dismiss();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isAliveRecreate",
                mOnShowListener == null && mOnDismissListener == null && mDialogCustomViewInflater == null &&
                mButtonClickListener == null && mCloseClickListener == null);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        MyLog.i(this.getClass().getSimpleName());
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.i(this.getClass().getSimpleName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.i(this.getClass().getSimpleName());
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        MyLog.i(this.getClass().getSimpleName());
    }

    @Override
    public LayoutInflater onGetLayoutInflater(Bundle savedInstanceState) {
        MyLog.i(this.getClass().getSimpleName());
        return super.onGetLayoutInflater(savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        MyLog.i(this.getClass().getSimpleName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MyLog.i(this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MyLog.i(this.getClass().getSimpleName());
    }

    @Override
    public void onStop() {
        super.onStop();
        MyLog.i(this.getClass().getSimpleName());
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        MyLog.i(this.getClass().getSimpleName());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MyLog.i(this.getClass().getSimpleName());

        boolean isShowAsNormalFragment = getArguments().getBoolean(ARG_KEY_IS_SHOW_AS_NORMAL_FRAGMENT);

        // false 이면 CustomDialogFragment 가 onCreateDialog 를 통해
        // Dialog 내용 표시후에 일반적인 Fragment 로 라이프사이클이 동작함.
        // 따라서 안에 childFragment 를 추가해줄 수 있음
        setShowsDialog(!isShowAsNormalFragment);

        // 다이얼로그 생성 중 익셉션이 나면 어차피 팝업 표시 못한다.
        // 그럴때는 그냥 dismiss 처리 한다.
        try {
            return buildDialog();

        } catch (Exception e) {
            MyLog.w("dialog can not build! " + e.getMessage());
            dismiss();
            return null;
        }
    }

    private Dialog buildDialog() throws Exception {
        Bundle args = getArguments();

        final DialogItems dialogItems = args.getParcelable(ARG_KEY_DIALOG_ITEMS);
        int displayOrientation = args.getInt(ARG_DISPLAY_ORIENTATION);

        BaseCustomDialog.Builder builder = new BaseCustomDialog.Builder(getActivity());
        String title = dialogItems.getTitle();
        boolean showTitle = dialogItems.isShowTitle();

        if(showTitle) {
            builder.setTitle(title);
        } else {
            builder.setTitleBarHidden();
        }

        if(dialogItems.getContentMessage() != null) {
            builder.setMessage(dialogItems.getContentMessage());
        } else {
            // 커스텀뷰 inflater 와 초기화 로직을 받는다.
            // mDialogCustomViewInflater 는 null 일 수 있다.
            builder.setView(mDialogCustomViewInflater.inflateContentView());
        }

        builder.setDismissOnButtonClick(args.getBoolean(ARG_KEY_IS_DISMISS_ON_BUTTON_CLICK));

        if(dialogItems.getPositiveButtonText() != null) {
            builder.setPositiveButton(dialogItems.getPositiveButtonText(), mButtonClickListener);
        }

        if(dialogItems.getNeutralButtonText() != null) {
            builder.setNeutralButton(dialogItems.getNeutralButtonText(), mButtonClickListener);
        }

        if(dialogItems.getNegativeButtonText() != null) {
            builder.setNegativeButton(dialogItems.getNegativeButtonText(), mButtonClickListener);
        }

        builder.setCloseButton(dialogItems.isShowCloseButton(), mCloseClickListener);

        setCancelable(dialogItems.isCancelable());
        builder.setDisplayOrientation(dialogItems.getDisplayOrientation());

        BaseCustomDialog dialog = builder.create();
        dialog.setOnShowListener(mOnShowListener);
        dialog.setOnDismissListener(mOnDismissListener != null ? mOnDismissListener : new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                // dialog 가 dismiss 가 되면 CustomDialogFragment 자체도 Fragment 라이프사이클에서 제거되어야함
                MyLog.i();
                removeThis();
            }
        });

        dialog.setCancelable(dialogItems.isCancelable());
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dismiss();
            }
        });

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        boolean isShowAsNormalFragment = getArguments().getBoolean(ARG_KEY_IS_SHOW_AS_NORMAL_FRAGMENT);
//        MyLog.i("isShowAsNormalFragment = " + isShowAsNormalFragment + ", getDialog = " + getDialog());

        if(isShowAsNormalFragment) {

            //todo 다이얼로그 null 테스트
//        if(true) {

            if(getDialog() == null) {
                // 액티비티가 시스템에 의해 강제 종료되고 앱이 다시 재실행될 때
                // 붙어있던 CustomDialogFragment 도 savedInstanceState 를 가지고 재시작된다.
                // 이 때 onCreateDialog 메소드가 호출되지 않아 getDialog() 가 null 인 케이스가 있다.
                // 일단 수동으로 다이얼로그를 만든 후 셋팅을 해보고, 그래도 에러 난다면(DialogCustomViewInflater 가 null 인 케이스 등)
                // 그냥 에러가 나지 않게 하고, 이 CustomDialogFragment 를 제거하도록 한다.
                try {
                    Dialog dialog = buildDialog();
                    setupDialog(dialog, 0);

                    if(dialog == null) {
                        throw new IllegalStateException("custom buildDialog called but returned null.");
                    }

                    return dialog.findViewById(R.id.dialog_root);

                } catch (Exception e) {
                    e.printStackTrace();
                    dismiss();

                    // "fragment does not have view" 에러 피하는 방어 코드
                    // 여기까지 왔으면 다이얼로그 생성중 에러가 난 경우이므로, 여기서 더미뷰를 만들어 리턴해야
                    // 프래그먼트 라이프 사이클에 의해 에러가 나지 않는다.
                    return new View(getContext());
                }
            }

            // childFragment 가 추가될 수 있게 하기 위해서 getDialog 로 가져온 다이얼로그 안의 루트 컨텐트뷰를 리턴해주어야함
            return getDialog().findViewById(R.id.dialog_root);

        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
//        MyLog.i(this.getClass().getSimpleName());

        if(mOnDismissListener != null) {
            mOnDismissListener.onDismiss(dialog);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        MyLog.i(this.getClass().getSimpleName());

        if (getDialog() == null) {
            // Tells DialogFragment to not use the fragment as a dialog, and so won't try to use mDialog
            setShowsDialog(false);
        }

        super.onActivityCreated(savedInstanceState);  // Will now complete and not crash
    }

    public void addChildFragment(int containerViewId, Fragment fragment, boolean addToBackStack) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.add(containerViewId, fragment, fragment.getClass().getSimpleName());
        if(addToBackStack)
            ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }

    public void removeThis() {
        MyLog.i();

        if(getFragmentManager() != null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.remove(this);
            ft.commitAllowingStateLoss();
        }
    }
}
