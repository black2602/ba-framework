package com.blackangel.baframework.ui.dialog.custom;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

/**
 * Created by Finger-kjh on 2017-11-10.
 */

public abstract class AbstractDialogCustomViewInflater implements Parcelable {
    public abstract View inflateContentView();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
