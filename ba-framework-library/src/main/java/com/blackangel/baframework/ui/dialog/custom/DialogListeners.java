package com.blackangel.baframework.ui.dialog.custom;

import android.content.DialogInterface;

/**
 * Created by KimJeongHun on 2017-02-03.
 */

public final class DialogListeners {
    /**
     * 커스텀 다이얼로그 컨텐츠를 표시하기 위한 inflater
     * CustomDialogFragment 에서 get 하여 사용한다.
     * (이것은 Parcelable 의 대상이 되지 않는다.)
     */
    private DialogCustomViewInflater dialogCustomViewInflater;
    private DialogInterface.OnClickListener buttonClickListener;
    private DialogInterface.OnShowListener onShowListener;
    private DialogInterface.OnDismissListener onDismissListener;

    protected DialogListeners() {

    }

    public DialogCustomViewInflater getDialogCustomViewInflater() {
        return dialogCustomViewInflater;
    }

    public void setDialogCustomViewInflater(DialogCustomViewInflater dialogCustomViewInflater) {
        this.dialogCustomViewInflater = dialogCustomViewInflater;
    }

    public DialogInterface.OnClickListener getButtonClickListener() {
        return buttonClickListener;
    }

    public void setButtonClickListener(DialogInterface.OnClickListener buttonClickListener) {
        this.buttonClickListener = buttonClickListener;
    }

    public DialogInterface.OnShowListener getOnShowListener() {
        return onShowListener;
    }

    public void setOnShowListener(DialogInterface.OnShowListener onShowListener) {
        this.onShowListener = onShowListener;
    }

    public DialogInterface.OnDismissListener getOnDismissListener() {
        return onDismissListener;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public static class Builder {
        private DialogListeners mDialogListeners;

        public Builder() {
            this.mDialogListeners = new DialogListeners();
        }

        public Builder setButtonClickListener(DialogInterface.OnClickListener buttonClickListener) {
            mDialogListeners.setButtonClickListener(buttonClickListener);
            return this;
        }

        public Builder setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
            mDialogListeners.setOnDismissListener(onDismissListener);
            return this;
        }

        public Builder setOnShowListener(DialogInterface.OnShowListener onShowListener) {
            mDialogListeners.setOnShowListener(onShowListener);
            return this;
        }

        public Builder setCustomViewInflater(DialogCustomViewInflater inflater) {
            mDialogListeners.setDialogCustomViewInflater(inflater);
            return this;
        }

        public DialogListeners build() {
            return mDialogListeners;
        }
    }
}
