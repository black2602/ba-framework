package com.angel.black.baframework.ui.dialog.custom;

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
    private DialogCustomViewInflater dialogCustomViewInflater;
    private DialogButtonClickListener positiveButtonClickListener;
    private DialogButtonClickListener neutralButtonClickListener;
    private DialogButtonClickListener negativeButtonClickListener;
    private DialogButtonClickListener closeButtonClickListener;
    private boolean cancelable = true;        // 백키 취소가능 디폴트 값 true
    private boolean showCloseButton;
    private boolean dismissOnButtonClick = true;

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

    public void setPositiveButton(String positiveButtonText, DialogButtonClickListener positiveButtonClickListener) {
        this.positiveButtonText = positiveButtonText;
        this.positiveButtonClickListener = positiveButtonClickListener;
    }

    public String getNeutralButtonText() {
        return neutralButtonText;
    }

    public void setNeutralButton(String neutralButtonText, DialogButtonClickListener neutralButtonClickListener) {
        this.neutralButtonText = neutralButtonText;
        this.neutralButtonClickListener = neutralButtonClickListener;
    }

    public String getNegativeButtonText() {
        return negativeButtonText;
    }

    public void setNegativeButton(String negativeButtonText, DialogButtonClickListener negativeButtonClickListener) {
        this.negativeButtonText = negativeButtonText;
        this.negativeButtonClickListener = negativeButtonClickListener;
    }

    public DialogButtonClickListener getPositiveButtonClickListener() {
        return positiveButtonClickListener;
    }

    public DialogButtonClickListener getNeutralButtonClickListener() {
        return neutralButtonClickListener;
    }

    public DialogButtonClickListener getNegativeButtonClickListener() {
        return negativeButtonClickListener;
    }

    public DialogCustomViewInflater getCustomViewInflater() {
        return dialogCustomViewInflater;
    }

    public void setDialogCustomViewInflater(DialogCustomViewInflater dialogCustomViewInflater) {
        this.dialogCustomViewInflater = dialogCustomViewInflater;
    }


    public boolean isCancelable() {
        return cancelable;
    }

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    public void setCloseButton(boolean showCloseButton, DialogButtonClickListener closeButtonClickListener) {
        this.showCloseButton = showCloseButton;
        this.closeButtonClickListener = closeButtonClickListener;
    }

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
        dialogCustomViewInflater = in.readParcelable(DialogCustomViewInflater.class.getClassLoader());
        positiveButtonClickListener = in.readParcelable(DialogButtonClickListener.class.getClassLoader());
        neutralButtonClickListener = in.readParcelable(DialogButtonClickListener.class.getClassLoader());
        negativeButtonClickListener = in.readParcelable(DialogButtonClickListener.class.getClassLoader());
        closeButtonClickListener = in.readParcelable(DialogButtonClickListener.class.getClassLoader());
        cancelable = in.readByte() != 0;
        showCloseButton = in.readByte() != 0;
        dismissOnButtonClick = in.readByte() != 0;
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
        dest.writeParcelable(this.dialogCustomViewInflater, flags);
        dest.writeParcelable(this.positiveButtonClickListener, flags);
        dest.writeParcelable(this.neutralButtonClickListener, flags);
        dest.writeParcelable(this.negativeButtonClickListener, flags);
        dest.writeParcelable(this.closeButtonClickListener, flags);
        dest.writeByte((byte) (this.cancelable ? 1 : 0));
        dest.writeByte((byte) (this.showCloseButton ? 1 : 0));
        dest.writeByte((byte) (this.dismissOnButtonClick ? 1 : 0));
    }

    public boolean isShowCloseButton() {
        return showCloseButton;
    }

    public int getTitleImageResId() {
        return titleImgResId;
    }

    public DialogButtonClickListener getCloseButtonClickListener() {
        return closeButtonClickListener;
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

        public Builder setPositiveButton(String positiveButtonText, DialogButtonClickListener positiveButtonClickListener) {
            mDialogItems.setPositiveButton(positiveButtonText, positiveButtonClickListener);
            return this;
        }

        public Builder setPositiveButton(int positiveButtonTextResId, DialogButtonClickListener positiveButtonClickListener) {
            mDialogItems.setPositiveButton(mRes.getString(positiveButtonTextResId), positiveButtonClickListener);
            return this;
        }

        public Builder setNeutralButton(String neutralButtonText, DialogButtonClickListener neutralButtonClickListener) {
            mDialogItems.setNeutralButton(neutralButtonText, neutralButtonClickListener);
            return this;
        }

        public Builder setNeutralButton(int neutralButtonTextResId, DialogButtonClickListener neutralButtonClickListener) {
            mDialogItems.setNeutralButton(mRes.getString(neutralButtonTextResId), neutralButtonClickListener);
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText, DialogButtonClickListener negativeButtonClickListener) {
            mDialogItems.setNegativeButton(negativeButtonText, negativeButtonClickListener);
            return this;
        }

        public Builder setNegativeButton(int negativeButtonTextResId, DialogButtonClickListener negativeButtonClickListener) {
            mDialogItems.setNegativeButton(mRes.getString(negativeButtonTextResId), negativeButtonClickListener);
            return this;
        }

        /**
         *
         * 기본 positive, negative, neutral 버튼 클릭시 팝업을 사라지게 할지 여부를 설정한다.<p>
         * true : positive, negative, neutral 버튼 클릭시 팝업 사라짐<p>
         * false : positive, negative, neutral 버튼 클릭시 팝업 사라지지 않음<p>
         * 디폴트는 true
         */
        public Builder setDismissOnButtonClick(boolean dismissOnButtonClick) {
            mDialogItems.setDismissOnButtonClick(dismissOnButtonClick);
            return this;
        }

        public Builder setCustomViewInflater(DialogCustomViewInflater inflater) {
            mDialogItems.setDialogCustomViewInflater(inflater);
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            mDialogItems.setCancelable(cancelable);
            return this;
        }

        public Builder setShowCloseButton(boolean showCloseButton, DialogButtonClickListener closeButtonClickListener) {
            mDialogItems.setCloseButton(showCloseButton, closeButtonClickListener);
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
