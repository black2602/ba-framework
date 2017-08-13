package com.angel.black.baframework.ui.dialog.custom;

import android.content.Context;
import android.os.Parcel;

/**
 * Created by KimJeongHun on 2017-02-07.
 */

public class HtmlContentMessageDialogItem extends DialogItems {
    private DialogContentMessageDecorator mDialogContentMessageDecorator;

    public HtmlContentMessageDialogItem() {

    }

    public HtmlContentMessageDialogItem(Parcel in) {
        super(in);
        mDialogContentMessageDecorator = in.readParcelable(DialogContentMessageDecorator.class.getClassLoader());
    }

    public void setContentMessageDecorator(DialogContentMessageDecorator dialogContentMessageDecorator) {
        mDialogContentMessageDecorator = dialogContentMessageDecorator;
    }

    public DialogContentMessageDecorator getDialogContentMessageDecorator() {
        return mDialogContentMessageDecorator;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(mDialogContentMessageDecorator, flags);
    }

    public static class Builder extends DialogItems.Builder {

        public Builder(Context context) {
            super(context);
        }

        @Override
        protected DialogItems createInstance() {
            return new HtmlContentMessageDialogItem();
        }
    }
}