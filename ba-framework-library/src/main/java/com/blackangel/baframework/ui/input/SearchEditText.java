package com.blackangel.baframework.ui.input;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blackangel.baframework.R;
import com.blackangel.baframework.logger.MyLog;
import com.blackangel.baframework.util.StringUtil;
import com.blackangel.baframework.util.ViewUtil;


/**
 * Created by Finger-kjh on 2017-06-20.
 */

public class SearchEditText extends RelativeLayout implements View.OnClickListener {

    private Context mContext;

    private EditText mEditText;
    private ImageButton mBtnDelete;

    private SearchActionListener mSearchActionListener;
    private String mHintStr;

    public SearchEditText(Context context) {
        super(context);
        init(context, null);
    }

    public SearchEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void setSearchActionListener(SearchActionListener searchActionListener) {
        mSearchActionListener = searchActionListener;
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    mSearchActionListener.onSearchKeyword(mEditText.getText().toString().trim());
                    ViewUtil.hideSoftKeyboard(getContext(), mEditText);
                    return true;
                }
                return false;
            }
        });
    }

    private void init(Context context, AttributeSet attrs) {
        this.mContext = context;

        if(attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SearchEditText);
            mHintStr = typedArray.getString(R.styleable.SearchEditText_searchHint);

            typedArray.recycle();
        }

        View.inflate(mContext, R.layout.view_search_edit_text, this);

        mEditText = (EditText) findViewById(R.id.edit_keyword);
        mBtnDelete = (ImageButton) findViewById(R.id.btn_del);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0) {
                    mBtnDelete.setVisibility(View.VISIBLE);
                } else {
                    mBtnDelete.setVisibility(View.GONE);
                }
            }
        });

        mBtnDelete.setOnClickListener(this);

        MyLog.d("mHintStr = " + mHintStr);

        if(!StringUtil.isEmptyString(mHintStr)) {
            mEditText.setHint(mHintStr);
        }
    }

    public void setBackground(int resourceId) {
        View bgView = findViewById(R.id.search_edit_background);

        if(resourceId > 0)
            bgView.setBackgroundResource(resourceId);
        else
            bgView.setBackgroundResource(android.R.color.transparent);

    }

    public void addEditTextChangedListener(TextWatcher textWatcher) {
        mEditText.addTextChangedListener(textWatcher);
    }

    public EditText getSearchEditText() {
        return mEditText;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_del) {
            mEditText.setText("");

            if(mSearchActionListener != null)
                mSearchActionListener.onDeleteKeyword();
        }
    }

    public interface SearchActionListener  {
        void onSearchKeyword(String keyword);
        void onDeleteKeyword();
    }


}
