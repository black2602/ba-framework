package com.angel.black.baframework.ui.dialog.custom;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.angel.black.baframework.logger.BaLog;


/**
 * Created by KimJeongHun on 2016-03-17.
 */
public class CustomDialogFragment extends DialogFragment {
    public static final String TAG = "custom_dialog";
    private static final String ARG_KEY_DIALOG_ITEMS = "dialogItems";
    private static final int BUTTON_CLOSE = 0;

    private DialogInterface.OnDismissListener mOnDismissListener;
    private DialogFragmentViewAttachListener mDialogFragmentAttachListener;


    public void setDialogFragmentAttachListener(DialogFragmentViewAttachListener dialogFragmentAttachListener) {
        mDialogFragmentAttachListener = dialogFragmentAttachListener;
    }

    public static CustomDialogFragment newInstance(DialogItems dialogItems) {
        CustomDialogFragment customDialog = new CustomDialogFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_KEY_DIALOG_ITEMS, dialogItems);
        customDialog.setArguments(args);

        return customDialog;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BaLog.i();

        if(mDialogFragmentAttachListener != null) {
            mDialogFragmentAttachListener.onAttachView();
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BaLog.i();
        Bundle args = getArguments();

        final DialogItems dialogItems = args.getParcelable(ARG_KEY_DIALOG_ITEMS);

        BaseCustomDialog.Builder builder = new BaseCustomDialog.Builder(getActivity());
        String title = dialogItems.getTitle();
        boolean showTitle = dialogItems.isShowTitle();

        if(showTitle) {
            //todo 타이틀 이미지
//            int titleImgResId = R.drawable.app_logo_title;
            int titleImgResId = 0;
            builder.setTitle(title, titleImgResId);
        }

        if(dialogItems.getContentMessage() != null) {
            if(dialogItems instanceof HtmlContentMessageDialogItem) {
                DialogContentMessageDecorator decorator = ((HtmlContentMessageDialogItem) dialogItems).getDialogContentMessageDecorator();
                builder.setMessage(decorator.decorateMessage(dialogItems.getContentMessage()));
            } else {
                builder.setMessage(dialogItems.getContentMessage());
            }
        } else {
            builder.setView(dialogItems.getCustomViewInflater().inflateContentView());
        }

        if(!dialogItems.isDismissOnButtonClick()) {
            builder.setDismissOnButtonClick(false);
        }

        if(dialogItems.getPositiveButtonText() != null) {
            builder.setPositiveButton(dialogItems.getPositiveButtonText(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DialogButtonClickListener positiveButtonClickListener = dialogItems.getPositiveButtonClickListener();
                    if(positiveButtonClickListener != null)
                        positiveButtonClickListener.onClick(Dialog.BUTTON_POSITIVE);
                }
            });
        }

        if(dialogItems.getNeutralButtonText() != null) {
            builder.setNeutralButton(dialogItems.getNeutralButtonText(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DialogButtonClickListener neutralButtonClickListener = dialogItems.getNeutralButtonClickListener();
                    if(neutralButtonClickListener != null)
                        neutralButtonClickListener.onClick(Dialog.BUTTON_NEUTRAL);
                }
            });
        }

        if(dialogItems.getNegativeButtonText() != null) {
            builder.setNegativeButton(dialogItems.getNegativeButtonText(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DialogButtonClickListener negativeButtonClickListener = dialogItems.getNegativeButtonClickListener();
                    if(negativeButtonClickListener != null)
                        negativeButtonClickListener.onClick(Dialog.BUTTON_NEGATIVE);
                }
            });
        }

        builder.setCloseButton(dialogItems.isShowCloseButton(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                DialogButtonClickListener closeButtonClickListener = dialogItems.getCloseButtonClickListener();
                if(closeButtonClickListener != null)
                    closeButtonClickListener.onClick(BUTTON_CLOSE);
            }
        });

        setCancelable(dialogItems.isCancelable());
        BaseCustomDialog dialog = builder.create();
        dialog.setOnDismissListener(mOnDismissListener);

        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        BaLog.i();

        if(mOnDismissListener != null) {
            mOnDismissListener.onDismiss(dialog);
        }
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        BaLog.i();
        if (getDialog() == null) {  // Returns mDialog
            // Tells DialogFragment to not use the fragment as a dialog, and so won't try to use mDialog
            setShowsDialog(false);
        }

        if(mDialogFragmentAttachListener != null)
            mDialogFragmentAttachListener.onAttachView();

        super.onActivityCreated(arg0);  // Will now complete and not crash
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.mOnDismissListener = onDismissListener;
    }

    public interface DialogFragmentViewAttachListener {
        void onAttachView();
    }
}
