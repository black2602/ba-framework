package com.blackangel.baframework.ui.dialog.custom;

/**
 * Created by KimJeongHun on 2016-06-15.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

import com.blackangel.baframework.R;
import com.blackangel.baframework.logger.MyLog;


/**
 * 퍼미션 요청 이유 설명하는 다이얼로그 (마시멜로 이후)
 */
@TargetApi(Build.VERSION_CODES.M)
public class PermissionConfirmationDialogFragment extends DialogFragment {
    public static final String TAG = "permissionDlg";
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_PERMISSION = "permission";          // 단일 퍼미션
    private static final String ARG_PERMISSIONS = "permissions";        // 복수개의 퍼미션
    private static final String ARG_REQUEST_CODE = "requestCode";
    private static final String ARG_NEGATIVE_BUTTON_TEXT = "negaBtnText";
    private static final String ARG_IS_ACTIVITY_FINISH_ON_CANCEL = "isActFinish";

    private Fragment mTargetFragment;
    private OnPermissionConfirmationDialogListener mOnPermissionConfirmationDialogListener;

    public static PermissionConfirmationDialogFragment newInstance(String message, String permission, int requestCode, boolean isActFinish) {
        PermissionConfirmationDialogFragment dialog = new PermissionConfirmationDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_PERMISSION, permission);
        args.putInt(ARG_REQUEST_CODE, requestCode);
        args.putBoolean(ARG_IS_ACTIVITY_FINISH_ON_CANCEL, isActFinish);
        dialog.setArguments(args);

        return dialog;
    }

    public static PermissionConfirmationDialogFragment newInstance(String message, String negaBtnTxt, String permission, int requestCode, boolean isActFinish) {
        PermissionConfirmationDialogFragment dialog = new PermissionConfirmationDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_NEGATIVE_BUTTON_TEXT, negaBtnTxt);
        args.putString(ARG_PERMISSION, permission);
        args.putInt(ARG_REQUEST_CODE, requestCode);
        args.putBoolean(ARG_IS_ACTIVITY_FINISH_ON_CANCEL, isActFinish);
        dialog.setArguments(args);

        return dialog;
    }

    public static PermissionConfirmationDialogFragment newInstance(String message, String[] permissions, int requestCode, boolean isActFinish) {
        PermissionConfirmationDialogFragment dialog = new PermissionConfirmationDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        args.putStringArray(ARG_PERMISSIONS, permissions);
        args.putInt(ARG_REQUEST_CODE, requestCode);
        args.putBoolean(ARG_IS_ACTIVITY_FINISH_ON_CANCEL, isActFinish);
        dialog.setArguments(args);

        return dialog;
    }

    public void setOnPermissionConfirmationDialogListener(OnPermissionConfirmationDialogListener listener) {
        this.mOnPermissionConfirmationDialogListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle args = getArguments();
        String negaBtnTxt = args.getString(ARG_NEGATIVE_BUTTON_TEXT);
        negaBtnTxt = (negaBtnTxt == null ? getString(android.R.string.cancel) : negaBtnTxt);

        final String[] permissions;

        if(args.containsKey(ARG_PERMISSIONS)) {
            permissions = args.getStringArray(ARG_PERMISSIONS);
        } else {
            permissions = new String[]{args.getString(ARG_PERMISSION)};
        }

        setCancelable(false);

        return new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Light_Dialog_Alert)
                .setTitle(R.string.permission_access)
                .setMessage(args.getString(ARG_MESSAGE))
                .setPositiveButton(R.string.next, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OnPermissionConfirmationDialogListener listener = null;

                        if(getActivity() instanceof OnPermissionConfirmationDialogListener) {
                            listener = (OnPermissionConfirmationDialogListener) getActivity();
                        } else if(mOnPermissionConfirmationDialogListener != null) {
                            listener = mOnPermissionConfirmationDialogListener;
                        }

                        if(mTargetFragment != null) {
                            mTargetFragment.requestPermissions(permissions, args.getInt(ARG_REQUEST_CODE));
                        } else {
                            ActivityCompat.requestPermissions((Activity) getContext(), permissions, args.getInt(ARG_REQUEST_CODE));
                        }

                        if(listener != null) {
                            listener.onAllowedPermissionConfirm(permissions);
                        }
                    }
                })
                .setNegativeButton(negaBtnTxt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyLog.e("퍼미션 접근 요청 거부함");
                        OnPermissionConfirmationDialogListener listener = null;

                        if(getActivity() instanceof OnPermissionConfirmationDialogListener) {
                            listener = (OnPermissionConfirmationDialogListener) getActivity();
                        } else if(mOnPermissionConfirmationDialogListener != null) {
                            listener = mOnPermissionConfirmationDialogListener;
                        }

                        if(listener != null) {
                            listener.onDenyedPermissionConfirm(permissions);
                        }

                        if(args.getBoolean(ARG_IS_ACTIVITY_FINISH_ON_CANCEL)) {
                            getActivity().finish();
                        }
                    }
                })
                .create();
    }

    public void setTargetFragment(Fragment fragment) {
        this.mTargetFragment = fragment;
    }

    /**
     * 퍼미션 요청 이유 설명하는 팝업의 허용/거부 리스너
     */
    public interface OnPermissionConfirmationDialogListener {
        void onAllowedPermissionConfirm(String[] permissions);
        void onDenyedPermissionConfirm(String[] permissons);
    }
}