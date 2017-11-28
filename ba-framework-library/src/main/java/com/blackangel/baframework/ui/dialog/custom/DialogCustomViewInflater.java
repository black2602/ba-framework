package com.blackangel.baframework.ui.dialog.custom;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by KimJeongHun on 2017-02-07.
 */

public class DialogCustomViewInflater implements Parcelable {

    private AbstractDialogCustomViewInflater mDialogCustomViewInflater;

    protected DialogCustomViewInflater(Parcel in) {
        mDialogCustomViewInflater = in.readParcelable(AbstractDialogCustomViewInflater.class.getClassLoader());
    }

    public DialogCustomViewInflater(AbstractDialogCustomViewInflater dialogCustomViewInflater) {
        mDialogCustomViewInflater = dialogCustomViewInflater;
    }

    public AbstractDialogCustomViewInflater getDialogCustomViewInflater() {
        return mDialogCustomViewInflater;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mDialogCustomViewInflater, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DialogCustomViewInflater> CREATOR = new Creator<DialogCustomViewInflater>() {
        @Override
        public DialogCustomViewInflater createFromParcel(Parcel in) {
            return new DialogCustomViewInflater(in);
        }

        @Override
        public DialogCustomViewInflater[] newArray(int size) {
            return new DialogCustomViewInflater[size];
        }
    };
}
