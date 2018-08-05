package com.blackangel.baframework.ui.view.input;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blackangel.baframework.R;
import com.blackangel.baframework.logger.MyLog;
import com.blackangel.baframework.ui.animation.AnimationUtil;
import com.blackangel.baframework.util.ScreenUtil;
import com.blackangel.baframework.util.StringUtil;
import com.blackangel.baframework.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Finger-kjh on 2017-08-21.
 */

public class LabelInputView extends RelativeLayout implements ViewUtil.OnGlobalKeyboardStatusListener {
    protected ViewUtil.OnKeyboardGlobalLayoutListener mOnKeyboardGlobalLayoutListener;

    @Override
    public void onShownSoftKeyboard() {
        MyLog.i(mLabelStr);
    }

    @Override
    public void onHiddenSoftKeyboard() {
        MyLog.i(mLabelStr);
    }

    enum LabelInputType {
        CAN_NOT_INPUT(-1), TEXT(0), NUMBER_DECIMAL(1), TEL_OR_MOBILE_NO(2), EMAIL(3), ADDRESS(4);

        private int value;

        LabelInputType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static LabelInputType valueOf(int value) {
            LabelInputType[] values = values();
            for (LabelInputType labelInputType : values) {
                if(labelInputType.getValue() == value) {
                    return labelInputType;
                }
            }

            // 못찾을 경우 기본값 TEXT 리턴
            return TEXT;
        }
    }

    protected TextView mLabel;
    protected ErrorHandleTextInputLayout mLayoutInput;
    protected BackPressEditText mInput;
    protected View mUnderLine;

    private String mLabelStr;
    private String mHintStr;
    private LabelInputType mInputType;     // 0 : 일반 텍스트, 1  : 숫자(소수점x), 2 : 전화번호, 3 : 이메일, 4 : 주소
    private float mLabelTextSize;
    private float mTextSize;
    protected View mNextFocusView;
    protected NextFocusConditionChecker mNextFocusConditionChecker;
    private boolean mMultiline;
    private boolean mIsLabelAnimate;

    private List<OnFocusChangeListener> mOnFocusChangeListeners;
    private boolean mIsAnimationing;

    public void setNextFocusView(LabelInputView nextFocusView) {
        setNextFocusView(nextFocusView, nextFocusView::isEmpty);
    }

    public void setNextFocusView(View nextFocusView, @Nullable NextFocusConditionChecker nextFocusConditionChecker) {
        MyLog.i();
        mInput.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        mNextFocusView = nextFocusView;
        mNextFocusConditionChecker = nextFocusConditionChecker;

        if(nextFocusView instanceof LabelInputView) {
            setNextFocusDownId(nextFocusView.findViewById(R.id.edit_input).getId());
        }

        if(nextFocusConditionChecker != null) {
            addOnFocusChangeListener((v, hasFocus) -> {
                if (nextFocusConditionChecker.canFocusableNextView()) {
                    mInput.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                } else {
                    mInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
                }
            });
        }
    }

    public LabelInputView(Context context) {
        super(context);
        if(!isInEditMode())
            init(context, null);
    }

    public LabelInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode())
            init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        boolean innerEditClickable = true;
        String labelStr = null;
        String hintStr = null;
        int inputType = LabelInputType.TEXT.getValue();
        mMultiline = false;

        if(attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.LabelInputView);
            labelStr = typedArray.getString(R.styleable.LabelInputView_label);
            hintStr = typedArray.getString(R.styleable.LabelInputView_hint);
            inputType = typedArray.getInt(R.styleable.LabelInputView_inputType, LabelInputType.TEXT.getValue());
            innerEditClickable = typedArray.getBoolean(R.styleable.LabelInputView_innerEditClickable, true);
            mMultiline = typedArray.getBoolean(R.styleable.LabelInputView_multiline, false);
            mIsLabelAnimate = typedArray.getBoolean(R.styleable.LabelInputView_isLabelAnimate, false);

            if(context instanceof Activity) {
                mLabelTextSize = typedArray.getDimension(R.styleable.LabelInputView_labelTextSize, ScreenUtil.convertDpToPixel((Activity) context, 12));
                mTextSize = typedArray.getDimension(R.styleable.LabelInputView_textSize, ScreenUtil.convertDpToPixel((Activity) context, 14));
            }

            typedArray.recycle();
        }

        View.inflate(context, R.layout.label_input_view, this);

        mLabel = findViewById(R.id.txt_label);
        mLayoutInput = findViewById(R.id.layout_edit_input);
        mInput = findViewById(R.id.edit_input);
//        mUnderLine = findViewById(R.id.underline);

        setEnableInnerEditText(innerEditClickable);

        if(!StringUtil.isEmptyString(labelStr)) {
            setLabel(labelStr);
        }

        if(!StringUtil.isEmptyString(hintStr)) {
            setHint(hintStr);
        }

        mLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, mLabelTextSize);
        mInput.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);

        setInputTypeAndMaxLength(inputType, -1);
        mInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                MyLog.i("actionId=" + actionId + ", keycode=" + (event == null ? "null" : event.getKeyCode()));
                if(actionId == EditorInfo.IME_ACTION_NEXT) {
//                    MyLog.i("mNextFocusView=" + mNextFocusView);
                    if(mNextFocusView != null) {
                        performGoNextViewFocus();

                        return true;
                    }
                }

                return false;
            }
        });

        if(mMultiline) {
            int minHeight = ScreenUtil.convertDpToPixel((Activity) getContext(), 42);
            setMultiline(7, (int) (minHeight * 3f));
        }

        mOnKeyboardGlobalLayoutListener = new ViewUtil.OnKeyboardGlobalLayoutListener(this, this);
        ViewUtil.addOnGlobalKeyboardStatusListener(this, mOnKeyboardGlobalLayoutListener);

        if(mIsLabelAnimate) {
            setLabelAnimation();
        } else {
            mLabel.setVisibility(View.VISIBLE);
        }

        mInput.setOnFocusChangeListener((v, hasFocus) -> {
            if(mOnFocusChangeListeners != null) {
                for (OnFocusChangeListener onFocusChangeListener : mOnFocusChangeListeners) {
                    onFocusChangeListener.onFocusChange(v, hasFocus);
                }
            }
        });
    }

    /**
     * 다음 포커스뷰로 포커스가 가도 되는지 유효성을 체크하여 괜찮으면 다음 뷰를 클릭액션 시키고,
     * 그렇지 않으면 그냥 키보드를 닫기 처리한다.
     */
    public void performGoNextViewFocus() {
//        MyLog.i("performGoNextViewFocus mNextFocusView = " + mNextFocusView
//                    + ", canFocusableNextView=" + mNextFocusConditionChecker.canFocusableNextView());

        if(mNextFocusView != null && mNextFocusConditionChecker != null && mNextFocusConditionChecker.canFocusableNextView()) {
            if (mNextFocusView instanceof LabelInputView) {
                ((LabelInputView) mNextFocusView).requestInputFocus();
            } else {
                mNextFocusView.performClick();
            }
        } else {
            ViewUtil.hideSoftKeyboard(getContext(), mInput);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mOnKeyboardGlobalLayoutListener != null) {
            mOnKeyboardGlobalLayoutListener.detach();
            mOnKeyboardGlobalLayoutListener = null;
        }
    }

    private void setLabelAnimation() {
        OnFocusChangeListener focusChangeListenerLabelAnimOperator = new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
//                MyLog.i(mLabelStr, "hasFocus=" + hasFocus + ", isEmpty=" + isEmpty() + ", mIsAnimationing=" + mIsAnimationing);

                if(hasFocus && !mIsAnimationing && mLabel.getVisibility() != View.VISIBLE) {
                    if(isEmpty()) {
                        startAppearAnim();

                    } else {
                        mLabel.setVisibility(View.VISIBLE);
                    }
                }
                else if(!hasFocus && isEmpty() && !mIsAnimationing && mLabel.getVisibility() == View.VISIBLE) {
                    startDisappearAnim();
                }
            }
        };

        addOnFocusChangeListener(focusChangeListenerLabelAnimOperator);
    }

    public void addOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        if(mOnFocusChangeListeners == null) {
            mOnFocusChangeListeners = new ArrayList<>();
        }

        mOnFocusChangeListeners.add(onFocusChangeListener);
    }

    private void startDisappearAnim() {
        AnimationUtil.startSlideBottomOutAnim(getContext(), mLabel, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mIsAnimationing = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIsAnimationing = false;

                // 애니메이션이 끝나기전에 포커스가 다시 바뀌는 경우 있으므로 여기서 포커스 다시 확인
                if(LabelInputView.this.hasFocus() &&
                        mOnKeyboardGlobalLayoutListener != null &&
                        mOnKeyboardGlobalLayoutListener.isShownKeyboard()) {
                    startAppearAnim();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void startAppearAnim() {
        AnimationUtil.startSlideBottomInAnim(getContext(), mLabel, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mIsAnimationing = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIsAnimationing = false;

                // 애니메이션이 끝나기전에 포커스가 다시 바뀌는 경우 있으므로 여기서 포커스 다시 확인
                if(!LabelInputView.this.hasFocus()) {
                    startDisappearAnim();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void setEnableInnerEditText(boolean enable) {
        mInput.setClickable(enable);
        mInput.setFocusable(enable);
        mInput.setFocusableInTouchMode(enable);
        mInput.setEnabled(enable);

        if(!enable) {
            // 비활성화시킬때는 텍스트 표시용이므로 여러줄이 표시되어야함
            mInput.setSingleLine(false);
            mInput.setLines(4);         // 디폴트 화면에 4줄 자리차지
            mInput.setMaxLines(7);      // 디폴트 최대 7줄
            mInput.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_FLAG_MULTI_LINE |
                    InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            mInput.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        }

        setClickable(!enable);
        setFocusable(!enable);
        setFocusableInTouchMode(!enable);
    }

    public void setLabel(String label) {
        mLabelStr = label;
        mLabel.setText(label);
    }

    public void setHint(String hint) {
        mHintStr = hint;
        mInput.setHint(hint);
    }

//    @Override
//    public void setEnabled(boolean enabled) {
//        super.setEnabled(enabled);
//
//        // enabled == false 이면 입력불가능, 밑줄도 사라짐
//        mInput.setEnabled(enabled);
//        mInput.setActivated(enabled);
//    }

    public void setText(@NonNull String text) {
        mInput.setText(text);

        if(mIsLabelAnimate) {
            mLabel.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 텍스트를 설정하고 입력을 막는다.(밑줄은 표시됨)
     */
    public void setTextWithLock(String text) {
//        MyLog.i("text=" + text);
        mInput.setText(text);
        mInput.setTextColor(getResources().getColor(R.color.gray_text_color));
        mInput.setEnabled(false);
        mInput.setActivated(true);

        mLabel.setVisibility(View.VISIBLE);
    }

    public boolean isEmpty() {
        return mInput.getText().toString().isEmpty();
    }

    public void requestInputFocus() {
        mInput.requestFocus();
        ViewUtil.showSoftKeyboard(getContext(), mInput);
    }

    public void setError(String error) {
        mLayoutInput.setError(error);
    }

    public void clearError() {
        mInput.setError(null);
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        mInput.addTextChangedListener(textWatcher);
    }

    public void setInputTypeAndMaxLength(int inputTypeValue, int maxLength) {
//        MyLog.i("inputTypeValue = " + inputTypeValue);
        mInputType = LabelInputType.valueOf(inputTypeValue);

        if(mInputType == LabelInputType.TEXT) {
            mInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            setMaxLength(maxLength);

        } else if(mInputType == LabelInputType.NUMBER_DECIMAL) {
            mInput.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER);
            setInputFilters(StringUtil.RegularExpressions.REGEX_ONLY_NUMBER, maxLength);

        } else if(mInputType == LabelInputType.TEL_OR_MOBILE_NO) {
            mInput.setInputType(InputType.TYPE_CLASS_PHONE);
            // 디폴트는 숫자키패드가 뜨지만 "*#(); 공백" 등의 특수문자 입력 가능하다.
            // 숫자만 받고싶으면 setInputFilter(StringUtil.REGEX_ONLY_NUMBER) 를 호출해주도록 한다.
            setInputFilters(StringUtil.RegularExpressions.REGEX_ONLY_NUMBER, maxLength);

        } else if(mInputType == LabelInputType.EMAIL) {
            mInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        } else if(mInputType == LabelInputType.ADDRESS) {
            // 줄바꿈되도록 하는 소스 (LabelMultilineInputViewWithDone 사용시 별도의 완료버튼을 둔다.)
            int minHeight = ScreenUtil.convertDpToPixel((Activity) getContext(), 42);
            setMultiline(3, (int) (minHeight * 1.5f));

            // 영어로 inputFilter 를 제한하지는 말고, 다 입력이 되고 유효성 검증에서 걸러내도록 한다.
            setMaxLength(maxLength);
        }
    }

    private void setMultiline(int maxLine, int minHeight) {
        mInput.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_FLAG_MULTI_LINE |
                InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        mInput.setGravity(Gravity.TOP);

        // 라벨과 입력 레이아웃 사이의 마진 조정 (Gravity 를 TOP 으로 줌으로써 너무 붙게되므로)
        RelativeLayout.LayoutParams layoutParams = (LayoutParams) mLayoutInput.getLayoutParams();
        layoutParams.topMargin = ScreenUtil.convertDpToPixel((Activity) getContext(), 16);
        mLayoutInput.setLayoutParams(layoutParams);

        mInput.setMinHeight(minHeight);

        ViewGroup.LayoutParams inputLayoutParams = mInput.getLayoutParams();
        inputLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        mInput.setLayoutParams(inputLayoutParams);
        mInput.setMaxLines(maxLine);
    }

    @Override
    public void setNextFocusDownId(int nextFocusDownId) {
        mInput.setNextFocusDownId(nextFocusDownId);
    }

    /**
     * 최대 입력 길이 설정.
     * @param maxLength 0보다 큰 경우만 설정한다.
     */
    public void setMaxLength(int maxLength) {
        if(maxLength > 0)
            ViewUtil.setEditTextMaxLength(mInput, maxLength);
    }

    public int length() {
        return mInput.length();
    }

    public String getCompletedText() {
        return mInput.getText().toString().trim();
    }

    public void setAllCapSentences() {
        mInput.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        ViewUtil.setEditTextAllCaps(mInput);
    }

    public void setLabelVisiblity(int visibility) {
        mLabel.setVisibility(visibility);
    }

    public void setKeyboardLanguage(Locale locale) {
        if (locale == null) {
            mInput.setPrivateImeOptions(null);
        } else {
            mInput.setPrivateImeOptions("defaultInputmode=" + locale.getDisplayLanguage(Locale.US).toLowerCase() + ";");
        }
    }

    public void setInputFilter(String regex) {
        ViewUtil.setEditTextInputFilter(mInput, regex);
    }

    public void setInputFilters(String regex, int maxLength) {
        ViewUtil.setEditTextInputFilters(mInput, regex, maxLength);
    }

    public void showInputKeyboard() {
        MyLog.i();

        mInput.postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewUtil.showSoftKeyboard(getContext(), mInput);
            }
        }, 100);
    }

    public void hideInputKeyboard() {
        MyLog.i();
        ViewUtil.hideSoftKeyboard(getContext(), mInput);
    }

    /**
     * 키보드의 다음 버튼으로 포커스를 이동시켜도 되는지 적합성을 체크하는 인터페이스
     */
    public interface NextFocusConditionChecker {
        boolean canFocusableNextView();
    }
}
