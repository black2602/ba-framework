package com.blackangel.baframework.ui.dialog.custom;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Finger-kjh on 2017-11-10.
 */
public abstract class AbstractDialogButtonClickListener implements Serializable, Parcelable {
    public abstract void onClick(int button);

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}