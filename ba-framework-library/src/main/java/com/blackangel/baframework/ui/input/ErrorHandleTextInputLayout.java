package com.blackangel.baframework.ui.input;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;

import com.blackangel.baframework.util.ViewUtil;


/**
 * Created by Finger-kjh on 2017-06-23.
 */

public class ErrorHandleTextInputLayout extends TextInputLayout {
    public ErrorHandleTextInputLayout(Context context) {
        super(context);
        init(context);
    }

    public ErrorHandleTextInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if(getEditText() == null)
            throw new IllegalStateException("EditText is null!!");

        getEditText().addTextChangedListener(new ViewUtil.TextInputErrorWatcher(this));
    }

    @Override
    public void setError(@Nullable CharSequence error) {
        if(error == null) {
            super.setError(error);
            setErrorEnabled(false);
        } else {
            setErrorEnabled(true);
            super.setError(error);
        }
    }
}
