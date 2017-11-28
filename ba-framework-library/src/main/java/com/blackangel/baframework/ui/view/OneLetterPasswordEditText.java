package com.blackangel.baframework.ui.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.blackangel.baframework.logger.MyLog;
import com.blackangel.baframework.util.ViewUtil;

/**
 * Created by Finger-kjh on 2017-07-25.
 */

public class OneLetterPasswordEditText extends android.support.v7.widget.AppCompatEditText {

    private Context mContext;
    private KeyListener mKeyListener;

    public void setKeyListener(KeyListener keyListener) {
        mKeyListener = keyListener;
    }

    private TextWatcher mTextChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            MyLog.i("s = " + s);

            if(s.length() == 1) {
                int rightInputId = getNextFocusRightId();

                MyLog.i("rightInputId = " + rightInputId);

                if(rightInputId == View.NO_ID) {
                    closeSecurityKeyboard();
//                    ((View) getParent()).requestFocus();      // 이부분이 자꾸 다음 인풋이나 스위치 자동 포커스를 시키는거같음...

                } else {
                    View nextInput = focusSearch(View.FOCUS_RIGHT);

                    // 다음 입력칸이 입력되어 있지 않을때만 다음칸으로 이동
                    if(((EditText) nextInput).getText().length() == 0) {
                        nextInput.requestFocus();
                    }
                }

                if(mKeyListener != null) {
                    mKeyListener.onKeyPressed(OneLetterPasswordEditText.this, s.toString());
                }
            }
        }
    };

    public OneLetterPasswordEditText(Context context) {
        super(context);
        init(context);
    }

    public OneLetterPasswordEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public OneLetterPasswordEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;

        addTextChangedListener(mTextChangedListener);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        MyLog.i("keyCode = " + keyCode + ", editText = " + OneLetterPasswordEditText.this);
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(mKeyListener != null) {
                // 키보드 내려가면서 포커스 제거해줌
                closeSecurityKeyboard();

                return true;
            }
        }

        return false;
    }

    private void closeSecurityKeyboard() {
        if(mKeyListener != null) {
            mKeyListener.onBackKeyPressed();
        } else {
            // 보안키패드가 설정되어 있지 않은 경우
            clearFocus();
            ViewUtil.hideSoftKeyboard(mContext, this);
        }
    }

    public interface KeyListener {
        void onBackKeyPressed();
        void onKeyPressed(OneLetterPasswordEditText pwEditText, String s);
    }

}
