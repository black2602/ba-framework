package com.blackangel.baframework.ui.input;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blackangel.baframework.R;
import com.blackangel.baframework.logger.MyLog;
import com.blackangel.baframework.util.ScreenUtil;
import com.blackangel.baframework.util.StringUtil;
import com.blackangel.baframework.util.ViewUtil;

import java.util.Locale;

/**
 * Created by Finger-kjh on 2017-08-21.
 */

public class LabelInputView extends RelativeLayout implements View.OnFocusChangeListener {
    private Context mContext;

    private TextView mLabel;
    private EditText mInput;

    private String mLabelStr;
    private String mHintStr;
    private int mInputType;     // 0 : 일반 텍스트, 1  : 숫자(소수점x), 2 : 전화번호, 3 : 이메일, 4 : 주소
    private float mLabelTextSize;
    private float mTextSize;
    private View mNextFocusView;
    private NextFocusConditionChecker mNextFocusConditionChecker;
    private boolean mMultiline;

    public void setNextFocusView(View nextFocusView) {
        setNextFocusView(nextFocusView, null);
    }

    public void setNextFocusView(View nextFocusView, NextFocusConditionChecker nextFocusConditionChecker) {
        MyLog.i();
        mInput.setImeOptions(EditorInfo.IME_ACTION_NEXT);

        mNextFocusView = nextFocusView;
        mNextFocusConditionChecker = nextFocusConditionChecker;

        if(nextFocusView instanceof LabelInputView) {
            setNextFocusDownId(nextFocusView.findViewById(R.id.edit_input).getId());
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
        mContext = context;

        boolean innerEditClickable = true;
        mMultiline = false;

        if(attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.LabelInputView);
            mLabelStr = typedArray.getString(R.styleable.LabelInputView_label);
            mHintStr = typedArray.getString(R.styleable.LabelInputView_hint);
            mInputType = typedArray.getInt(R.styleable.LabelInputView_inputType, 0);
            innerEditClickable = typedArray.getBoolean(R.styleable.LabelInputView_innerEditClickable, true);
            mMultiline = typedArray.getBoolean(R.styleable.LabelInputView_multiline, false);

            if(context instanceof Activity) {
                mLabelTextSize = typedArray.getDimension(R.styleable.LabelInputView_labelTextSize, ScreenUtil.convertDpToPixel((Activity) context, 12));
                mTextSize = typedArray.getDimension(R.styleable.LabelInputView_textSize, ScreenUtil.convertDpToPixel((Activity) context, 14));
            }

            typedArray.recycle();
        }

        View.inflate(context, R.layout.label_input_view, this);

        mLabel = (TextView) findViewById(R.id.txt_label);
        mInput = (EditText) findViewById(R.id.edit_input);

        setEnableInnerEditText(innerEditClickable);

        if(!StringUtil.isEmptyString(mLabelStr)) {
            setLabel(mLabelStr);
        }

        if(!StringUtil.isEmptyString(mHintStr)) {
            setHint(mHintStr);
        }

        mLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, mLabelTextSize);
        mInput.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);

        setInputType(mInputType);
        setOnFocusChangeListener(this);
        mInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                MyLog.i("actionId=" + actionId);
                if(actionId == EditorInfo.IME_ACTION_NEXT) {
                    MyLog.i("mNextFocusView=" + mNextFocusView);
                    if(mNextFocusView != null) {
                        if (mNextFocusView instanceof LabelInputView) {
                            ((LabelInputView) mNextFocusView).requestInputFocus();
                        } else {
                            // 다음버튼 눌렀을 때 다음 포커스뷰로 포커스가 가도 되는지 유효성을 체크하여 괜찮으면 다음 뷰를 클릭액션 시키고,
                            // 그렇지 않으면 그냥 키보드를 닫기 처리한다.
                            if(mNextFocusConditionChecker != null && mNextFocusConditionChecker.canFocusableNextView()) {
                                mNextFocusView.performClick();
                            } else {
                                ViewUtil.hideSoftKeyboard(getContext(), mInput);
                            }
                        }
                    }

                    return true;
                }

                return false;
            }
        });

        if(mMultiline) {
            mInput.setMaxLines(10);  // 멀티라인일때 디폴트 10줄
            mInput.setLines(10);
            mInput.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
            mInput.setSingleLine(false);
            int minHeight = ScreenUtil.convertDpToPixel((Activity) getContext(), 42);
            mInput.setMinHeight(minHeight);
            mInput.setGravity(Gravity.TOP);
            mInput.setHorizontallyScrolling(false);
//            mInput.setImeOptions(EditorInfo.IME_ACTION_NEXT);

            ViewGroup.LayoutParams layoutParams = mInput.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mInput.setLayoutParams(layoutParams);
        }
    }

    private void setEnableInnerEditText(boolean enable) {
        mInput.setClickable(enable);
        mInput.setFocusable(enable);
        mInput.setFocusableInTouchMode(enable);
        mInput.setEnabled(enable);

        if(!enable) {
            mInput.setMovementMethod(null);
            mInput.setKeyListener(null);

            // 비활성화시킬때는 텍스트 표시용이므로 여러줄이 표시되어야함
            mInput.setSingleLine(false);
        }

        setClickable(!enable);
        setFocusable(!enable);
        setFocusableInTouchMode(!enable);
    }

    public void setLabel(String label) {
        mLabel.setText(label);
    }

    public void setHint(String hint) {
        mInput.setHint(hint);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        // enabled == false 이면 입력불가능, 밑줄도 사라짐
        mInput.setEnabled(enabled);
        mInput.setActivated(enabled);
    }

    public void setText(String text) {
        mInput.setText(text);
    }

    /**
     * 텍스트를 설정하고 입력을 막는다.
     * 밑줄은 표시됨
     */
    public void setTextWithLock(String text) {
        mInput.setText(text);
        mInput.setEnabled(false);
        mInput.setActivated(true);
    }

    public Editable getText() {
        return mInput.getText();
    }

    public boolean isEmpty() {
        return mInput.getText().toString().isEmpty();
    }

    public void requestInputFocus() {
        mInput.requestFocus();
    }

    public void setError(String error) {
        mInput.setError(error);
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        mInput.addTextChangedListener(textWatcher);
    }

    public void setInputType(int inputType) {
        switch (inputType) {
            case 0:     // 일반텍스트
            case 4:     // 주소
                mInput.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case 1:     // 숫자
                mInput.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_NUMBER_FLAG_SIGNED|InputType.TYPE_CLASS_NUMBER);
                break;
            case 2:     // 전화번호
                mInput.setInputType(InputType.TYPE_CLASS_PHONE);
                break;
            case 3:     // 이메일
                mInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;

        }
    }

    @Override
    public void setNextFocusDownId(int nextFocusDownId) {
        mInput.setNextFocusDownId(nextFocusDownId);
    }

    public void setMaxLength(int maxlength) {
        ViewUtil.setEditTextMaxLength(mInput, maxlength);
    }

    public int length() {
        return mInput.length();
    }

    public String getCompletedText() {
        return mInput.getText().toString().trim();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        MyLog.i("hasFocus=" + hasFocus);
        if(hasFocus) {
            mInput.requestFocus();
        }
    }

    public EditText getInputView() {
        return mInput;
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        setEnableInnerEditText(false);
        super.setOnClickListener(l);
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        mInput.setOnFocusChangeListener(l);
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

    /**
     * 키보드의 다음 버튼으로 포커스를 이동시켜도 되는지 적합성을 체크하는 인터페이스
     */
    public interface NextFocusConditionChecker {
        boolean canFocusableNextView();
    }
}
