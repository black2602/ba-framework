package com.blackangel.baframework.ui.view.input;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.blackangel.baframework.R;
import com.blackangel.baframework.logger.MyLog;
import com.blackangel.baframework.util.BuildUtil;
import com.blackangel.baframework.util.ScreenUtil;


/**
 * Created by Finger-kjh on 2017-08-21.
 *
 * 여러줄 입력가능 (with 키보드의 엔터 액션) + 별도의 완료 버튼을 가지는 입력 뷰
 * 글로벌키보드 리스너를 구현하여 ViewUtil 의 글로벌 키보드 상태 변수(sIsShownKeyboard) 를 활용한다.
 */

public class LabelMultilineInputViewWithDone extends LabelInputView {

    private static final int DONE_BUTTON_ID = 999999;
    private final int VISIBLITY_CHANGE_LOCK_DELAY_TIME = 500;
    private View mBtnDone;
    private long mInputFocusGetTime = -1;

    public LabelMultilineInputViewWithDone(Context context) {
        super(context);
        init();
    }

    public LabelMultilineInputViewWithDone(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        MyLog.i();

//        mBtnDone = new ImageButton(getContext(), null, R.style.MainButton_NoBackground);
        mBtnDone = new Button(getContext(), null, R.style.MainButton_NoBackground);
        mBtnDone.setId(DONE_BUTTON_ID);

        if(mBtnDone instanceof Button)
            ((Button) mBtnDone).setText(R.string.complete);
        else if(mBtnDone instanceof ImageButton)
            ((ImageButton) mBtnDone).setImageResource(android.R.drawable.arrow_down_float);

        mBtnDone.setOnClickListener(v -> hideInputKeyboard());
        mBtnDone.setFocusable(false);
        mBtnDone.setFocusableInTouchMode(false);
        mBtnDone.setVisibility(View.GONE);

        addOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                MyLog.i("LabelInputViewWithDone hasFocus=" + hasFocus);

                if(hasFocus && mBtnDone.getVisibility() != View.VISIBLE) {
                    showDoneButton();
                    mInputFocusGetTime = System.currentTimeMillis();
                }

                else if(!hasFocus) {
                    if(mBtnDone.getVisibility() == View.VISIBLE) {
                        long current = System.currentTimeMillis();
                        MyLog.d("current=" + current);
                        MyLog.d("focus getTime =" + mInputFocusGetTime + ", diff = " + (current - mInputFocusGetTime));

                        if(current - mInputFocusGetTime <= VISIBLITY_CHANGE_LOCK_DELAY_TIME) {
                            // 포커스를 잃은 시점과 입력이 포커스되어 "완료" 버튼이 뜬 시점의 차이가
                            // VISIBLITY_CHANGE_LOCK_DELAY_TIME 미만이면 무시하도록..
                            // 이 부분이 없으면 "완료"버튼이 뜨면서 바로 mInput 에 대한 hasFocus 가 false 로 들어오기 때문에 예외처리 해주어야함
                            return;
                        }

                        // 입력칸이 포커스를 잃을 경우 완료 버튼을 숨기기만 한다.
                        // * 키보드를 내리는 것은 완료버튼을 누를 때, 백버튼 누를때에만 반응한다.
                        hideDoneButton();
                    }
                }
            }
        });

        RelativeLayout.LayoutParams btnLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        btnLayoutParams.leftMargin = ScreenUtil.convertDpToPixel((Activity) getContext(), 8);
        btnLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        if(mBtnDone instanceof Button) {
            btnLayoutParams.addRule(RelativeLayout.ALIGN_BASELINE, R.id.layout_edit_input);

        } else if(mBtnDone instanceof ImageButton) {
            btnLayoutParams.addRule(RelativeLayout.ALIGN_TOP, R.id.layout_edit_input);
            btnLayoutParams.topMargin = mLabel.getHeight();
        }

        ((RelativeLayout) mLayoutInput.getParent()).addView(mBtnDone, btnLayoutParams);

        // 하드웨어 백키를 인터셉트하기 위한 리스너 설정
        mInput.setOnBackPressListener(new BackPressEditText.OnEditTextBackPressListener() {
            @Override
            public boolean onBackPress() {
                MyLog.i("mOnKeyboardGlobalLayoutListener.isShownKeyboard=" + mOnKeyboardGlobalLayoutListener.isShownKeyboard());

                if(mOnKeyboardGlobalLayoutListener.isShownKeyboard()) {
                    hideInputKeyboard();
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public void hideInputKeyboard() {
        super.hideInputKeyboard();

        mBtnDone.postDelayed(() -> performGoNextViewFocus(), 200);
    }

    private void showDoneButton() {
        MyLog.i();

        mBtnDone.setVisibility(View.VISIBLE);

        // 완료 버튼을 위치를 입력 editText 의 시작부분에 맞춘다.
        RelativeLayout.LayoutParams btnLayoutParams = (RelativeLayout.LayoutParams) mBtnDone.getLayoutParams();
        btnLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        btnLayoutParams.addRule(RelativeLayout.ALIGN_TOP, R.id.layout_edit_input);
        btnLayoutParams.topMargin = ((RelativeLayout.LayoutParams) mLayoutInput.getLayoutParams()).topMargin;

        mBtnDone.setLayoutParams(btnLayoutParams);

        RelativeLayout.LayoutParams inputLayoutParams = (RelativeLayout.LayoutParams) mLayoutInput.getLayoutParams();
        inputLayoutParams.addRule(RelativeLayout.LEFT_OF, DONE_BUTTON_ID);
        mLayoutInput.setLayoutParams(inputLayoutParams);
    }

    private void hideDoneButton() {
        MyLog.i();
        mBtnDone.setVisibility(View.INVISIBLE);

        RelativeLayout.LayoutParams inputLayoutParams = (RelativeLayout.LayoutParams) mLayoutInput.getLayoutParams();
        if (BuildUtil.isAboveJellyBean17()) {
            inputLayoutParams.removeRule(RelativeLayout.LEFT_OF);
        } else {
            inputLayoutParams.addRule(RelativeLayout.LEFT_OF, -1);
        }

        mLayoutInput.setLayoutParams(inputLayoutParams);

        // 다시 EditText 에 포커스가 가지않도록 한다.
        mInput.clearFocus();
    }

    @Override
    public void onShownSoftKeyboard() {
        super.onShownSoftKeyboard();

        if(mInput.hasFocus()) {
            showDoneButton();
        }
    }

    @Override
    public void onHiddenSoftKeyboard() {
        super.onHiddenSoftKeyboard();
        hideDoneButton();
    }
}