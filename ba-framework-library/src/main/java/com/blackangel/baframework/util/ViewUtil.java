package com.blackangel.baframework.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.blackangel.baframework.R;
import com.blackangel.baframework.logger.MyLog;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by KimJeongHun on 2016-05-04.
 */
public class ViewUtil {
    public static void setEditTextPhoneDash(final EditText edit) {
        edit.addTextChangedListener(new TextWatcher() {
            String mPrevText;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mPrevText = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                MyLog.i();

                try {
                    String str = s.toString();
                    if (str.length() > 0 && !str.equals(mPrevText)) {
                        if (StringUtil.isCellPhoneWithDash(str)) {
                            if (str.endsWith("-")) {
                                s.delete(s.length() - 1, s.length());
                                return;
                            }

                            String phoneNumWithDash = StringUtil.convertPhoneNumWithDash(str);
                            s.replace(0, s.length() - 1, phoneNumWithDash, 0, phoneNumWithDash.length() - 1);
                        } else {
                            // 숫자 외의 문자가 들어간 경우
                            if (str.contains("-") && StringUtils.isNumeric(str.replaceAll("-", ""))) {
                                // 숫자와 "-" 만 들어간 경우
                                String strWithoutDash = str.replaceAll("-", "");
                                s.replace(0, s.length() - 1, strWithoutDash, 0, strWithoutDash.length() - 1);
                            }
                        }
                    }
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void setEditTextCount(final EditText edit, final TextView text) {
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                text.setText("" + edit.getText().toString().length());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public static void setEditTextMoney(final EditText edit) {
        edit.addTextChangedListener(new TextWatcher() {
            private String mPrevText;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if(s.length() <= 0)
                    return;

                //TODO 백스페이스 연속으로 눌르면 지워지도록
                /* "count != 0" 이 조건을 추가로 주어야, 백스페이스 누르고있을때 연속으로 글자 지워지는게 가능
                *   but 백스페이스 시에 "," 위치 제대로 안나옴 */
//                if (count != 0 && !str.equals(mPrevText)) {
                if (!str.equals(mPrevText)) {
                    mPrevText = StringUtil.getMoney(str);
                    edit.setText(mPrevText);

                    Editable e = edit.getText();
                    Selection.setSelection(e, mPrevText.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public static void initSpinner(Context context, Spinner spinner, int resArrayId) {
        if (resArrayId <= 0)
            return;

        String[] list = context.getResources().getStringArray(resArrayId);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
    }

    public static void initSpinner(Context context, Spinner spinner, ArrayList<String> list) {
        int len = list.size();

        if (len <= 0)
            return;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
    }


    public static void initSpinner(Context context, AppCompatSpinner spinner, ArrayList<String> list) {
        int len = list.size();

        if (len <= 0)
            return;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
    }

    public static void initSpinner(Context context, Spinner spinner, String[] list) {
        int len = list.length;

        if (len <= 0)
            return;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
    }

    public static void setVisibilityHierancy(ViewGroup vg, int visiblity) {
        vg.setVisibility(View.VISIBLE);

        for(int i = 0 ; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            child.setVisibility(visiblity);

            if(child instanceof ViewGroup) {
                setVisibilityHierancy((ViewGroup) child, visiblity);
            }
        }
    }

    /**
     * 리스트 스크롤뷰를 제일 아래로 내린다.
     */
    public static void scrollToEnd(final ListView listView) {
        listView.postDelayed(new Runnable() {
            @Override
            public void run() {
                listView.setSelection(listView.getCount() - 1);
            }
        }, 100);
    }

    /**
     * 키보드의 오른쪽 아래(완료, 검색) 버튼 누를시 특정 버튼의 클릭동작을 수행하도록 셋팅한다.
     */
    public static void setEditorActionForButtonClick(EditText editText, final Button btn) {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                    case EditorInfo.IME_ACTION_SEARCH:
                        btn.performClick();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }

    /**
     * 주어진 에딧텍스트가 포커스 될 때 입력된 문자열을 선택 상태로 만든다.
     */
    public static void setEditTextOnFocusSelectAll(final EditText editText) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    editText.selectAll();
                }
            }
        });
    }

    /**
     * 텍스트뷰의 autoLink 설정되있는 기능을 커스터마이징한다.
     * http, https 등의 스킴을 주어진 커스텀 스킴으로 교체한다.
     *
     * @param tv
     * @param customScheme
     */
    public static void fixCustomSchemeToTextViewAutoLink(TextView tv, String customScheme) {
        SpannableString current = (SpannableString) tv.getText();
        URLSpan[] spans = current.getSpans(0, current.length(), URLSpan.class);

        for (URLSpan span : spans) {
            if(span.getURL().contains("mailto"))
                continue;

            int start = current.getSpanStart(span);
            int end = current.getSpanEnd(span);

            current.removeSpan(span);
            current.setSpan(new DefensiveURLSpan(replaceCustomScheme(span.getURL(), customScheme)), start, end, 0);
        }
    }

    private static String replaceCustomScheme(String url, String customScheme) {
        String customUrl;
        if(url.contains("http://")) {
            customUrl = url.replace("http://", customScheme + "://");
        } else if(url.contains("https://")) {
            customUrl = url.replace("https://", customScheme + "://");
        } else {
            customUrl = customScheme + "://" + url;
        }

        MyLog.d("url=" + url + ", replaced customUrl=" + customUrl);

        return customUrl;
    }

    public static void smoothScrollToEnd(final ListView listView, final int position) {
        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.smoothScrollToPosition(position);
            }
        });
    }

    public static boolean isBottomOfListView(RecyclerView recyclerView) {

        return false;
    }

    private static class DefensiveURLSpan extends URLSpan {
        public DefensiveURLSpan(String url) {
            super(url);
        }

        @Override
        public void onClick(View widget) {
            try {
                android.util.Log.d(getClass().getSimpleName(), "Got here!");
                super.onClick(widget);
            }
            catch (ActivityNotFoundException e) {
                // do something useful here
            }
        }
    }

    /**
     * 주어진 이미지뷰를 화면에 표시될 이미지 가로사이즈에 맞춰서 실제 이미지 비율에 맞게 사이즈를 조정한다.
     *
     * @param imgView
     * @param displayImageViewWidth 화면에 표시될 가로 픽셀 사이즈
     * @param realImageWidth        실제 이미지 픽셀 사이즈(가로)
     * @param realImageHeight       실제 이미지 픽셀 사이즈(세로)
     */
    public static void resizeImageView(ImageView imgView, int displayImageViewWidth, int realImageWidth, int realImageHeight) {
        MyLog.d("displayImageViewWidth=" + displayImageViewWidth);

        float hRatio = realImageHeight / (float) realImageWidth;
        MyLog.d("hRatio=" + hRatio);

        ViewGroup.LayoutParams layoutParams = imgView.getLayoutParams();
        layoutParams.height = (int) (displayImageViewWidth * hRatio);

        MyLog.d("newImgViewHeight=" + layoutParams.height);

        imgView.setLayoutParams(layoutParams);
        imgView.invalidate();
    }

    public static boolean isBottomOfListView(ListView listView) {
//        MyLog.d("listView.getLastVisiblePosition()=" + listView.getLastVisiblePosition()
//                + ", listView.getAdapter().getCount() - 1=" + (listView.getAdapter().getCount() - 1)
//                + ", listView.getChildAt(listView.getChildCount() - 1).getBottom()=" + listView.getChildAt(listView.getChildCount() - 1).getBottom()
//                + ", listView.getHeight()=" + listView.getHeight());

        if (listView.getAdapter().getCount() == 0 || (listView.getLastVisiblePosition() == listView.getAdapter().getCount() - 1 &&
                listView.getChildAt(listView.getChildCount() - 1).getBottom() <= listView.getHeight())) {
            MyLog.d("listview has reached bottom");
            return true;
        }

        MyLog.d("listview not reached bottom");
        return false;
    }

    /**
     * 텍스트뷰를 특정 색깔 + Bold 를 준어서 표시한다.
     * @param originStr 원본 문자열
     * @param start		데코를 시작할 인덱스
     * @param end		데코를 마칠 인덱스
     * @param textView	표시할 텍스트뷰
     */
    public static void setTextWithdecorateColorBold(String originStr, int color, int start, int end, TextView textView) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(originStr);
        ssb.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(ssb);
    }

    /**
     * 리스트뷰를 스크롤하지 않고, 짧게 아무 영역 클릭 시 이벤트 콜백해주는 터치리스너
     * 리스트뷰 안의 아무데나 클릭했을 때 키보드를 내리는 로직 등에 쓰임
     */
    public static class ListViewClickNotScrollTouchListener implements View.OnTouchListener {

        private long mLastTouchTime;
        private final ListViewNotScrollClickListener mListViewNotScrollClickListener;

        public ListViewClickNotScrollTouchListener(ListViewNotScrollClickListener listViewNotScrollClickListener) {
            this.mListViewNotScrollClickListener = listViewNotScrollClickListener;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
//            MyLog.i("event.getAction=" + event.getAction());

            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                mLastTouchTime = System.currentTimeMillis();

            } else if(event.getAction() == MotionEvent.ACTION_UP) {
                long diff = System.currentTimeMillis() - mLastTouchTime;

//                MyLog.d("touch from down to up diff time=" + diff);

                if(System.currentTimeMillis() - mLastTouchTime < 100) {

                    if(mListViewNotScrollClickListener != null) {
                        mListViewNotScrollClickListener.onListViewClickNotScroll();
                    }
                }
            }

            return false;
        }
    }


    /**
     * view 이벤트 발생시 소프트 키보드 감추기
     *
     * @param context 현재 화면
     * @param view 이벤트 발생 View (ex. Button)
     */
    public static void hideSoftKeyboard(Context context, View view) {
        InputMethodManager mgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isActionBarVisible(Activity activity) {
        return activity.getActionBar() != null && activity.getActionBar().isShowing();
    }


    public static int getActionBarHeight(Activity activity) {
        final TypedArray styledAttributes = activity.getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.actionBarSize });
        int actionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return actionBarSize;
    }

    public static void setEditTextMaxLength(EditText editText, int length) {
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(length);
        editText.setFilters(FilterArray);
    }

    public static void setEditTextInputFilter(EditText editText, final String regexPattern) {
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Pattern ps = Pattern.compile(regexPattern);
                if (!ps.matcher(source).matches()) {
                    return "";
                }
                return null;
            }
        };

        editText.setFilters(new InputFilter[]{filter});
    }

    public static int getStatusBarHeight(Activity activity) {
        Rect rectangle = new Rect();
        Window window = activity.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;
        int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight = contentViewTop - statusBarHeight;

        MyLog.i("StatusBar Height= " + statusBarHeight + " , TitleBar Height = " + titleBarHeight);

        return titleBarHeight;
    }

    public static int getActionBarHeight(AppCompatActivity activity) {
        final TypedArray styledAttributes = activity.getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.actionBarSize });
        int actionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return actionBarSize;
    }

    public static boolean isActionBarVisible(AppCompatActivity activity) {
        return activity.getSupportActionBar() != null && activity.getSupportActionBar().isShowing();
    }

    public static void resizeTotalHeightofListViewAndInvalidate(ListView listView) {
        ListAdapter mAdapter = listView.getAdapter();

        int totalHeight = 0;

        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, listView);

            mView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            totalHeight += mView.getMeasuredHeight();
//            MyLog.w("listview item " + i + " height = " + mView.getMeasuredHeight() + ", totalHeight = " + String.valueOf(totalHeight));
        }

        MyLog.w("totalHeight = " + totalHeight);

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (mAdapter.getCount() - 1)) + listView.getPaddingTop() + listView.getPaddingBottom();
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            //listItem.measure(0, 0);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight;
        listView.setLayoutParams(params);

        listView.requestLayout();
    }

    /**
     * 스크롤뷰 안에 여러개(혹은 한개) 의 텍스트뷰가 각자 스크롤 가능하도록 셋팅한다.
     * @param scrollView
     * @param textViews
     */
    public static void setEnableScrolllingTextViewInScrollView(ScrollView scrollView, final View... textViews) {

        scrollView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                for (View textView : textViews) {
                    textView.getParent().requestDisallowInterceptTouchEvent(false);
                }

                return false;
            }
        });


        for (final View textView : textViews) {
            if(textView instanceof TextView) {
                ((TextView) textView).setMovementMethod(new ScrollingMovementMethod());
            }

            textView.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    textView.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
        }
    }

    public static class TextInputErrorWatcher implements TextWatcher {
        private TextInputLayout mTextInputLayout;

        public TextInputErrorWatcher(TextInputLayout textInputLayout) {
            this.mTextInputLayout = textInputLayout;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(s.length() > 0) {
                mTextInputLayout.setError(null);
            }
        }
    };

    public interface ListViewNotScrollClickListener {
        void onListViewClickNotScroll();
    }
}
