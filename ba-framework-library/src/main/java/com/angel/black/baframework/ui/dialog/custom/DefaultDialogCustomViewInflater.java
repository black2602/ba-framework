package com.angel.black.baframework.ui.dialog.custom;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

/**
 * Created by KimJeongHun on 2017-02-07.
 */

public abstract class DefaultDialogCustomViewInflater implements DialogCustomViewInflater {

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}

interface DialogCustomViewInflater extends Parcelable {
    View inflateContentView();
}

