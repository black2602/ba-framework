package com.angel.black.baframework.ui.dialog.custom;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by KimJeongHun on 2017-02-07.
 */

public abstract class DefaultDialogButtonClickListener implements DialogButtonClickListener {

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}

interface DialogButtonClickListener extends Parcelable {
    void onClick(int button);
}

