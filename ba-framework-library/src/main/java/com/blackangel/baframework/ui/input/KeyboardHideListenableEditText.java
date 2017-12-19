package com.blackangel.baframework.ui.input;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.blackangel.baframework.logger.MyLog;
import com.blackangel.baframework.util.ViewUtil;


/**
 * Created by Finger-kjh on 2017-07-25.
 */

public class KeyboardHideListenableEditText extends TextInputEditText {

    private Context mContext;
    private KeyboardBackListener mCustomKeyboardBackListener;
    private KeyboardBackListener mNormalKeyboardBackListener;

    public void setCustomKeyboardBackListener(KeyboardBackListener customKeyboardBackListener) {
        mCustomKeyboardBackListener = customKeyboardBackListener;
    }

    public void setNormalKeyboardBackListener(KeyboardBackListener normalKeyboardBackListener) {
        mNormalKeyboardBackListener = normalKeyboardBackListener;
    }

    public KeyboardHideListenableEditText(Context context) {
        super(context);
        init(context);
    }

    public KeyboardHideListenableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public KeyboardHideListenableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;

        setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    MyLog.i("action done!");
                    closeNormalSoftKeyboardInternal();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        MyLog.i("keyCode = " + keyCode + ", editText = " + KeyboardHideListenableEditText.this);
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            // 키보드 내려가면서 포커스 제거해줌
            closeKeyboard();
        }

        return super.onKeyPreIme(keyCode, event);
    }

    private boolean closeKeyboard() {

        if(mCustomKeyboardBackListener != null) {
            mCustomKeyboardBackListener.onHiddenKeyboardOnEditText();

        } else {
            // 보안키패드가 설정되어 있지 않은 경우
            if (hasFocus() && mNormalKeyboardBackListener != null) {
                closeNormalSoftKeyboardInternal();
                return true;
            }
        }

        return false;
    }

    private void closeNormalSoftKeyboardInternal() {
        ViewUtil.hideSoftKeyboard(mContext, this);
        mNormalKeyboardBackListener.onHiddenKeyboardOnEditText();
    }

    public interface KeyboardBackListener {
        void onHiddenKeyboardOnEditText();
    }

}
