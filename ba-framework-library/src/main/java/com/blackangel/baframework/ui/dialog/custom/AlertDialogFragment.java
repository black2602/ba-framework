package com.blackangel.baframework.ui.dialog.custom;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.blackangel.baframework.R;
import com.blackangel.baframework.logger.MyLog;


/**
 * Created by KimJeongHun on 2016-09-16.
 */
public class AlertDialogFragment extends DialogFragment {
    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_CANCELABLE = "cancelable";
    private static final String ARG_NEGATIVE_BUTTON_TEXT = "negativeBtnText";

    private DialogInterface.OnClickListener mOnClickListener;

    public static AlertDialogFragment newInstance(String title, String message) {
        AlertDialogFragment dialogFragment = new AlertDialogFragment();

        Bundle args = new Bundle();
        if(title != null) args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    public static AlertDialogFragment newInstance(String title, String message, String negativeBtnTxt) {
        AlertDialogFragment dialogFragment = new AlertDialogFragment();

        Bundle args = new Bundle();
        if(title != null) args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_NEGATIVE_BUTTON_TEXT, negativeBtnTxt);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    public static AlertDialogFragment newInstance(String title, String message, boolean cancelable) {
        AlertDialogFragment dialogFragment = new AlertDialogFragment();

        Bundle args = new Bundle();
        if(title != null) args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putBoolean(ARG_CANCELABLE, cancelable);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        outState.putBoolean("isAliveRecreate", mOnClickListener == null);
        super.onSaveInstanceState(outState);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialog);

        String title = getArguments().getString(ARG_TITLE);
        String message = getArguments().getString(ARG_MESSAGE);
        String negativeBtnTxt = getArguments().getString(ARG_NEGATIVE_BUTTON_TEXT);

        if(title != null)
            builder.setTitle(title);

        MyLog.i("onClickListener = " + mOnClickListener);

        builder.setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, mOnClickListener);

        if(!TextUtils.isEmpty(negativeBtnTxt)) {
            builder.setNegativeButton(android.R.string.cancel, mOnClickListener);
        }

        builder.setCancelable(getArguments().getBoolean(ARG_CANCELABLE));
        setCancelable(getArguments().getBoolean(ARG_CANCELABLE));

        return builder.create();
    }

    public void setOnButtonClickListener(DialogInterface.OnClickListener onClickListener) {
        MyLog.i("onClickListener = " + onClickListener);
        mOnClickListener = onClickListener;
    }
}
