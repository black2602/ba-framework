package com.blackangel.baframework.ui.view.input;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;
import android.view.KeyEvent;

/**
 * Created by Finger-kjh on 2018-03-15.
 */

public class BackPressEditText extends TextInputEditText
{
    private OnEditTextBackPressListener mOnEditTextBackPressListener;


    public BackPressEditText(Context context)
    {
        super(context);
    }


    public BackPressEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }


    public BackPressEditText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }


    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && mOnEditTextBackPressListener != null)
        {
            if(mOnEditTextBackPressListener.onBackPress()) {
                return true;
            }
        }

        return super.onKeyPreIme(keyCode, event);
    }


    public void setOnBackPressListener(OnEditTextBackPressListener $listener)
    {
        mOnEditTextBackPressListener = $listener;
    }

    public interface OnEditTextBackPressListener
    {
        boolean onBackPress();
    }
}

