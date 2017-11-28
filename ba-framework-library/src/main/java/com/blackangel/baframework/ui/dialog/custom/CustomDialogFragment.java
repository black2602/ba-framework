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
import com.blackangel.baframework.logger.MyLog;


/**
 * Created by KimJeongHun on 2016-03-17.
 */
public class CustomDialogFragment extends DialogFragment {
    protected static final String ARG_KEY_DIALOG_ITEMS = "dialogItems";
    private static final String ARG_DISPLAY_ORIENTATION = "displayOrientation";
    private static final String ARG_KEY_IS_DISMISS_ON_BUTTON_CLICK = "isDismissOnBtnClick" ;

    private static final int BUTTON_CLOSE = 0;

    private DialogInterface.OnShowListener mOnShowListener;
    private DialogInterface.OnDismissListener mOnDismissListener;

    public static CustomDialogFragment newInstance(DialogItems dialogItems) {
        CustomDialogFragment customDialog = new CustomDialogFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_KEY_DIALOG_ITEMS, dialogItems);
        args.putBoolean(ARG_KEY_IS_DISMISS_ON_BUTTON_CLICK, true);
        customDialog.setArguments(args);

        return customDialog;
    }

    public static CustomDialogFragment newInstance(DialogItems dialogItems, BaseCustomDialog.DisplayOrientation displayOrientation) {
        CustomDialogFragment customDialog = new CustomDialogFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_KEY_DIALOG_ITEMS, dialogItems);
        args.putInt(ARG_DISPLAY_ORIENTATION, displayOrientation.ordinal());
        args.putBoolean(ARG_KEY_IS_DISMISS_ON_BUTTON_CLICK, true);
        customDialog.setArguments(args);

        return customDialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        MyLog.i(this.getClass().getSimpleName());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i(this.getClass().getSimpleName());
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MyLog.i(this.getClass().getSimpleName());

        // childFragment 가 추가될 수 있게 하기 위해서 getDialog 로 가져온 다이얼로그 안의 루트 컨텐트뷰를 리턴해주어야함
        return getDialog().findViewById(R.id.dialog_root);
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

        // 중요함. 이것을 false 로 해주어야 이 CustomDialogFragment 가 onCreateDialog 를 통해
        // Dialog 내용 표시후에 일반적인 Fragment 로 라이프사이클이 동작함.
        // 따라서 안의 childFragment 를 추가해줄 수 있음
        setShowsDialog(false);
        Bundle args = getArguments();

        final DialogItems dialogItems = args.getParcelable(ARG_KEY_DIALOG_ITEMS);
        int displayOrientation = args.getInt(ARG_DISPLAY_ORIENTATION);

        BaseCustomDialog.Builder builder = new BaseCustomDialog.Builder(getActivity());
        String title = dialogItems.getTitle();
        boolean showTitle = dialogItems.isShowTitle();

        if(showTitle) {
            int titleImgResId = R.drawable.ic_launcher_round;
            builder.setTitle(title, titleImgResId);
        } else {
            builder.setTitleBarHidden();
        }

        if(dialogItems.getContentMessage() != null) {
            builder.setMessage(dialogItems.getContentMessage());
        } else {
            // 커스텀뷰 inflater 와 초기화 로직을 받는다.
            builder.setView(dialogItems.getCustomViewInflater().getDialogCustomViewInflater().inflateContentView());
        }

        builder.setDismissOnButtonClick(args.getBoolean(ARG_KEY_IS_DISMISS_ON_BUTTON_CLICK));

        if(dialogItems.getPositiveButtonText() != null) {
            builder.setPositiveButton(dialogItems.getPositiveButtonText(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AbstractDialogButtonClickListener positiveButtonClickListener = dialogItems.getPositiveButtonClickListener();
                    if(positiveButtonClickListener != null)
                        positiveButtonClickListener.onClick(Dialog.BUTTON_POSITIVE);
                }
            });
        }

        if(dialogItems.getNeutralButtonText() != null) {
            builder.setNeutralButton(dialogItems.getNeutralButtonText(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AbstractDialogButtonClickListener neutralButtonClickListener = dialogItems.getNeutralButtonClickListener();
                    if(neutralButtonClickListener != null)
                        neutralButtonClickListener.onClick(Dialog.BUTTON_NEUTRAL);
                }
            });
        }

        if(dialogItems.getNegativeButtonText() != null) {
            builder.setNegativeButton(dialogItems.getNegativeButtonText(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AbstractDialogButtonClickListener negativeButtonClickListener = dialogItems.getNegativeButtonClickListener();
                    if(negativeButtonClickListener != null)
                        negativeButtonClickListener.onClick(Dialog.BUTTON_NEGATIVE);
                }
            });
        }

        builder.setCloseButton(dialogItems.isShowCloseButton(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                AbstractDialogButtonClickListener closeButtonClickListener = dialogItems.getCloseButtonClickListener();
                if(closeButtonClickListener != null)
                    closeButtonClickListener.onClick(BUTTON_CLOSE);
            }
        });

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

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        MyLog.i(this.getClass().getSimpleName());

        if(mOnDismissListener != null) {
            mOnDismissListener.onDismiss(dialog);
        }
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        MyLog.i(this.getClass().getSimpleName());

        if (getDialog() == null) {  // Returns mDialog
            // Tells DialogFragment to not use the fragment as a dialog, and so won't try to use mDialog
            setShowsDialog(false);
        }

        super.onActivityCreated(arg0);  // Will now complete and not crash
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.mOnDismissListener = onDismissListener;
    }

    public void setOnShowListener(DialogInterface.OnShowListener onShowListener) {
        mOnShowListener = onShowListener;
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
