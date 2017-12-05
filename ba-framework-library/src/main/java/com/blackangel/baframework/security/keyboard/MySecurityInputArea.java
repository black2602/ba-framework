package com.blackangel.baframework.security.keyboard;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.blackangel.baframework.R;
import com.blackangel.baframework.core.base.BaseActivity;
import com.blackangel.baframework.logger.MyLog;
import com.blackangel.baframework.ui.input.OneLetterPasswordEditText;
import com.blackangel.baframework.util.ScreenUtil;
import com.blackangel.baframework.util.ViewUtil;

/**
 * Created by Finger-kjh on 2017-08-14.
 */

public class MySecurityInputArea extends LinearLayout implements MySecurityKeyboard.SecurityKeyboardListener, OneLetterPasswordEditText.KeyListener {

    private Context mContext;
    private RelativeLayout mRootView;
    private EditText[] mEditTexts;
    private int mLength;
    private float mInputCellWidth;
    private float mInputCellHeight;

    private MySecurityKeyboard mSecurityKeyboard;
    private MySecurityKeyboard.GlobalSecurityKeyboardListener mGlobalSecurityKeyboardListener;
    private OnCompleteInputListener mOnCompleteInputListener;

    public void setOnCompleteInputListener(OnCompleteInputListener onCompleteInputListener) {
        mOnCompleteInputListener = onCompleteInputListener;
    }

    public MySecurityInputArea(Context context) {
        super(context);
        init(context, null);
    }

    public MySecurityInputArea(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MySecurityInputArea(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.mContext = context;

        if(attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MySecurityInputArea);
            mLength = typedArray.getInt(R.styleable.MySecurityInputArea_length, 4);
            mInputCellWidth = typedArray.getDimension(R.styleable.MySecurityInputArea_inputCellWidth, ScreenUtil.convertDpToPixel((Activity) context, 46));
            mInputCellHeight = typedArray.getDimension(R.styleable.MySecurityInputArea_inputCellHeight, ScreenUtil.convertDpToPixel((Activity) context, 62));

            typedArray.recycle();
        }

        int leftMargin = ScreenUtil.convertDpToPixel((Activity) mContext, 10);

        setFocusable(true);
        setFocusableInTouchMode(true);

        mEditTexts = new EditText[mLength];
        for(int i=0; i < mLength; i++) {
            OneLetterPasswordEditText editText = (OneLetterPasswordEditText) View.inflate(mContext, R.layout.view_pw_edit, null);
            editText.setId(i);
            editText.setKeyListener(this);

            if( i > 0) {
                mEditTexts[i - 1].setNextFocusRightId(editText.getId());
            }

            final int focusIndex = i;
            editText.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    MyLog.d("focusIndex = " + focusIndex + ", hasFocus=" + hasFocus + ", area = " + MySecurityInputArea.this);

                    if(hasFocus) {
                        if(mSecurityKeyboard == null) {
                            // 키보드가 초기화 안되어있는경우 (처음 포커스)
                            loadKeyboard(focusIndex);

                        } else if (!mSecurityKeyboard.isShown()) {
                            // 키보드가 초기화 되었는데 내려가 있는 경우 -> 다시 띄우고 기존입력값 모두 지움
                            mSecurityKeyboard.show(focusIndex, true);
                            mSecurityKeyboard.removeAllInputKeys();

//                            mSecurityKeyboard.removeInputKey(focusIndex);
                        } else {
                            // 키보드가 띄워져 있고, 다른 입력 칸을 다시 누른경우 -> 기존입력값 모두 지움 (안됨) 다른 EditText 도 포커스되기때문
//                            mSecurityKeyboard.removeAllInputKeys();
                            mSecurityKeyboard.removeInputKey(focusIndex);
                        }
                    }
                }
            });

            LayoutParams params = new LayoutParams((int) mInputCellWidth, (int) mInputCellHeight);
            if(i > 0)
                params.setMargins(leftMargin, 0, 0, 0);

            addView(editText, params);

            setGravity(Gravity.CENTER_HORIZONTAL);

            mEditTexts[i] = editText;
        }
    }

    private void loadKeyboard(int focusIndex) {
        MyLog.i("focusIndex = " + focusIndex);

        if(mRootView == null) {
            MyLog.w("to attach root view of keyboard still not attached !!");
            return;
        }

        RelativeLayout relativeLayout = mRootView;

        mSecurityKeyboard = new MySecurityKeyboard(mContext);
        mSecurityKeyboard.setKeyboardActionListener(this);
        mSecurityKeyboard.setGlobalKeyboardListener(mGlobalSecurityKeyboardListener);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        relativeLayout.addView(mSecurityKeyboard, layoutParams);

        mSecurityKeyboard.bindEditTexts(mEditTexts);
        mSecurityKeyboard.show(focusIndex, false);
    }

    private String getInputValues() {
        return mSecurityKeyboard.getPlainInputKeys();
    }

    @Override
    public void setToAttachKeyboardViewParent(RelativeLayout parentView) {
        MyLog.i("parentView = " + parentView);
        mRootView = parentView;

        // 키보드뷰가 위치할 상위 뷰에 보이고, 사라질때 애니메이션 처리 (xml 의 animateLayoutChanges="true" 와 동일)
        LayoutTransition layoutTransition = new LayoutTransition();
//        layoutTransition.enableTransitionType(LayoutTransition.APPEARING);
//        layoutTransition.enableTransitionType(LayoutTransition.DISAPPEARING);
        mRootView.setLayoutTransition(layoutTransition);
    }

    @Override
    public void onCompleteInput(MySecurityKeyboard keyboard) {
        hideSecurityKeyboard();

        if(mOnCompleteInputListener != null)
            mOnCompleteInputListener.onCompleteInput(this);
    }

    @Override
    public void onShownKeyboard(int top, boolean firstShown) {
        MyLog.d("getBottom = " + getBottom() + ", keyboard top = " + top);

        // 키보드가 보일 때 키보드의 위치와 포커스될 입력 뷰 위치 계산하여 입력 뷰를 보이도록 offset 이동시킴
        int[] location = new int[2];
        getLocationOnScreen(location);

        MyLog.d("location[0] = " + location[0] + ", location[1] = " + location[1]);

        int hiddenActionBarHeight = 0;
        if(!ViewUtil.isActionBarVisible((Activity) mContext)) {
            MyLog.d("actionbar is not visible");

            hiddenActionBarHeight = ViewUtil.getActionBarHeight((Activity) mContext);
        }

        int diff = Math.abs(location[1] - top) + hiddenActionBarHeight;

        // 키보드 뷰와 형제 뷰(Root RelativeLayout 의 첫번째 자식) 인 키보드 외의 뷰 영역을 위로 끌어올린다.
        View child = mRootView.getChildAt(0);
        child.offsetTopAndBottom(-diff);

        mSecurityKeyboard.bringToFront();
    }

    public String getEncryptedInputValuesToServer() {
        return mSecurityKeyboard.getEncryptedInputValuesToServer();
    }

    public boolean isEmpty() {
        for (EditText editText : mEditTexts) {
            if(editText.getText().length() == 0)
                return true;
        }

        return false;
    }

    public void hideSecurityKeyboard() {
        for (EditText editText : mEditTexts) {
            editText.clearFocus();
        }

        requestFocus();     // 모든 EditText 포커스를 클리어하고 자기자신에게 포커스를 주어, 다른 뷰로 포커스가 넘어가지 못하게함
        mSecurityKeyboard.hide();
    }

    @Override
    public void onBackKeyPressed() {
        MyLog.i();

        if(mSecurityKeyboard.isShown()) {
            hideSecurityKeyboard();
        } else {
            if(!((BaseActivity) getContext()).isDialogShown())
                ((Activity) getContext()).onBackPressed();
        }
    }

    @Override
    public void onKeyPressed(OneLetterPasswordEditText pwEditText, String s) {
        // 하나의 키를 입력 후 나머지 입력칸의 입력상태에 따라 동작을 결정한다.
        MyLog.i();
        boolean checkNext = false;
        boolean allInputed = true;
        boolean lastInput = false;

        for (int i=0; i < mEditTexts.length; i++) {
            EditText editText = mEditTexts[i];

            if(editText.length() == 0) {
                allInputed = false;
                editText.requestFocus();
                break;
            }

            if (editText == pwEditText) {
                checkNext = true;
                lastInput = (i == mEditTexts.length - 1);
            }
        }

        // 모두 입력했고, 마지막 칸을 입력했을때만 키보드 내린다. 모두 입력했지만, 마지막 칸이 아닌 경우 내리지 않는다.
        if(allInputed && lastInput) {
            onCompleteInput(mSecurityKeyboard);
        }
    }

    public MySecurityKeyboard getSecurityKeyboard() {
        return mSecurityKeyboard;
    }

    public boolean isShownSecurityKeyboard() {
        return mSecurityKeyboard != null && mSecurityKeyboard.isShown();
    }

    public void setGlobalKeyboardListener(MySecurityKeyboard.GlobalSecurityKeyboardListener globalSecurityKeyboardListener) {
        this.mGlobalSecurityKeyboardListener = globalSecurityKeyboardListener;
    }

    public void refreshKeyboard() {
        loadKeyboard(0);
    }

    public void clear() {
        for (EditText editText : mEditTexts) {
            editText.setText("");
        }

        mSecurityKeyboard.clear();
    }

    public interface OnCompleteInputListener {
        void onCompleteInput(MySecurityInputArea mySecurityInputArea);
    }
}
