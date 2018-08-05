package com.blackangel.baframework.ui.dialog.custom;

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by KimJeongHun on 2017-02-03.
 */

public class DialogItems implements Parcelable {
    private boolean showTitle = true;
    private String title;
    private int titleImgResId;
    private String contentMessage;
    private String positiveButtonText;
    private String negativeButtonText;
    private String neutralButtonText;
    private boolean cancelable = true;        // 백키 취소가능 디폴트 값 true
    private boolean showCloseButton;
    private boolean dismissOnButtonClick = true;
    private int displayOrientation;

    protected DialogItems() {

    }

    protected DialogItems(Parcel in) {
        showTitle = in.readByte() != 0;
        title = in.readString();
        titleImgResId = in.readInt();
        contentMessage = in.readString();
        positiveButtonText = in.readString();
        negativeButtonText = in.readString();
        neutralButtonText = in.readString();
        cancelable = in.readByte() != 0;
        showCloseButton = in.readByte() != 0;
        dismissOnButtonClick = in.readByte() != 0;
        displayOrientation = in.readInt();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
    }

    public String getContentMessage() {
        return contentMessage;
    }

    public void setContentMessage(String contentMessage) {
        this.contentMessage = contentMessage;
    }

    public String getPositiveButtonText() {
        return positiveButtonText;
    }

    public void setPositiveButton(String positiveButtonText) {
        this.positiveButtonText = positiveButtonText;
    }

    public String getNeutralButtonText() {
        return neutralButtonText;
    }

    public void setNeutralButton(String neutralButtonText) {
        this.neutralButtonText = neutralButtonText;
    }

    public String getNegativeButtonText() {
        return negativeButtonText;
    }

    public void setNegativeButton(String negativeButtonText) {
        this.negativeButtonText = negativeButtonText;
    }

    public boolean isCancelable() {
        return cancelable;
    }

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    public void setCloseButton(boolean showCloseButton) {
        this.showCloseButton = showCloseButton;
    }

    public boolean isShowCloseButton() {
        return showCloseButton;
    }

    public int getTitleImageResId() {
        return titleImgResId;
    }

    public boolean isShowTitle() {
        return showTitle;
    }

    public boolean isDismissOnButtonClick() {
        return dismissOnButtonClick;
    }

    public void setDismissOnButtonClick(boolean dismissOnButtonClick) {
        this.dismissOnButtonClick = dismissOnButtonClick;
    }

    private void setDisplayOrientation(BaseCustomDialog.DisplayOrientation displayOrientation) {
        this.displayOrientation = displayOrientation.ordinal();
    }

    public static final Creator<DialogItems> CREATOR = new Creator<DialogItems>() {
        @Override
        public DialogItems createFromParcel(Parcel in) {
            return new DialogItems(in);
        }

        @Override
        public DialogItems[] newArray(int size) {
            return new DialogItems[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (this.showTitle ? 1 : 0));
        dest.writeString(this.title);
        dest.writeInt(this.titleImgResId);
        dest.writeString(this.contentMessage);
        dest.writeString(this.positiveButtonText);
        dest.writeString(this.negativeButtonText);
        dest.writeString(this.neutralButtonText);
        dest.writeByte((byte) (this.cancelable ? 1 : 0));
        dest.writeByte((byte) (this.showCloseButton ? 1 : 0));
        dest.writeByte((byte) (this.dismissOnButtonClick ? 1 : 0));
        dest.writeInt(this.displayOrientation);
    }

    public BaseCustomDialog.DisplayOrientation getDisplayOrientation() {
        return BaseCustomDialog.DisplayOrientation.valueOf(displayOrientation);
    }

    public static class Builder {
        private final Resources mRes;
        private DialogItems mDialogItems;

        public Builder(Context context) {
            this.mRes = context.getResources();
            this.mDialogItems = createInstance();
        }

        protected DialogItems createInstance() {
            return new DialogItems();
        }

        public Builder setTitle(String title) {
            mDialogItems.setTitle(title);
            return this;
        }

        public Builder setTitle(int titleResId) {
            mDialogItems.setTitle(mRes.getString(titleResId));
            return this;
        }

        public Builder setTitleImage(int titleImgResId) {
            mDialogItems.setTitleImage(titleImgResId);
            return this;
        }

        public Builder setShowTitle(boolean showTitle) {
            mDialogItems.setShowTitle(showTitle);
            return this;
        }

        public Builder setContentMessage(String contentMessage) {
            mDialogItems.setContentMessage(contentMessage);
            return this;
        }

        public Builder setContentMessage(int contentMessageResId) {
            mDialogItems.setContentMessage(mRes.getString(contentMessageResId));
            return this;
        }

        public Builder setPositiveButton(int positiveButtonTextResId) {
            mDialogItems.setPositiveButton(mRes.getString(positiveButtonTextResId));
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText) {
            mDialogItems.setPositiveButton(positiveButtonText);
            return this;
        }

        public Builder setNeutralButton(String neutralButtonText) {
            mDialogItems.setNeutralButton(neutralButtonText);
            return this;
        }

        public Builder setNeutralButton(int neutralButtonTextResId) {
            mDialogItems.setNeutralButton(mRes.getString(neutralButtonTextResId));
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText) {
            mDialogItems.setNegativeButton(negativeButtonText);
            return this;
        }

        public Builder setNegativeButton(int negativeButtonTextResId) {
            mDialogItems.setNegativeButton(mRes.getString(negativeButtonTextResId));
            return this;
        }

        public Builder setDismissOnButtonClick(boolean dismissOnButtonClick) {
            mDialogItems.setDismissOnButtonClick(dismissOnButtonClick);
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            mDialogItems.setCancelable(cancelable);
            return this;
        }

        public Builder setShowCloseButton(boolean showCloseButton) {
            mDialogItems.setCloseButton(showCloseButton);
            return this;
        }

        public Builder setDisplayOrientation(BaseCustomDialog.DisplayOrientation displayOrientation) {
            mDialogItems.setDisplayOrientation(displayOrientation);
            return this;
        }

        public DialogItems build() {
            return mDialogItems;
        }
    }

    private void setTitleImage(int titleImgResId) {
        this.titleImgResId = titleImgResId;
    }
}
