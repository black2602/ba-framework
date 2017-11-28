package com.blackangel.baframework.ui.dialog.custom;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by KimJeongHun on 2017-02-07.
 */

public class DialogButtonClickListener implements Parcelable {

    private AbstractDialogButtonClickListener mDialogButtonClickListener;

    protected DialogButtonClickListener(Parcel in) {
        mDialogButtonClickListener = (AbstractDialogButtonClickListener) in.readSerializable();
    }

    public DialogButtonClickListener(AbstractDialogButtonClickListener dialogButtonClickListener) {
        mDialogButtonClickListener = dialogButtonClickListener;
    }

    public AbstractDialogButtonClickListener getDialogButtonClickListener() {
        return mDialogButtonClickListener;
    }

    public static final Creator<DialogButtonClickListener> CREATOR = new Creator<DialogButtonClickListener>() {
        @Override
        public DialogButtonClickListener createFromParcel(Parcel in) {
            return new DialogButtonClickListener(in);
        }

        @Override
        public DialogButtonClickListener[] newArray(int size) {
            return new DialogButtonClickListener[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeSerializable(mDialogButtonClickListener);
    }
}
