package com.blackangel.baframework.ui.dialog;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ScrollView;

/**
* 최대 크기가 300dp 로 제한되는 ScrollView - 기본 다이얼로그(BaseCustomDialog) 에서 사용
* 일반 스크롤뷰로 크기 제한된 텍스트뷰를 감싸면 스크롤이 안되는 문제때문에 커스텀해서 사용 
* @author KimJeongHun
*
*/

public class DialogScrollView extends ScrollView {
	public static final int MAX_HEIGHT = 300; // 300dp

    public DialogScrollView(Context context) {
		super(context);
	}

	public DialogScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DialogScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(dpToPx(getResources(), MAX_HEIGHT), MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
	
    private int dpToPx(Resources res, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
    }
}