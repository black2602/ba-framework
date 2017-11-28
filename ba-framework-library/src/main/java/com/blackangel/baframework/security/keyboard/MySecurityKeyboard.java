package com.blackangel.baframework.security.keyboard;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;

import com.blackangel.baframework.R;
import com.blackangel.baframework.logger.MyLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Finger-kjh on 2017-08-10.
 */

public class MySecurityKeyboard extends LinearLayout implements View.OnClickListener {
    private TableLayout mTableLayout;
    private Button mBtn1;
    private Button mBtn2;
    private Button mBtn3;
    private Button mBtn4;
    private Button mBtn5;
    private Button mBtn6;
    private Button mBtn7;
    private Button mBtn8;
    private Button mBtn9;
    private Button mBtn10;
    private Button mBtn11;
    private Button mBtn12;
    private Button mBtn0;

    private Button mBtnRearrange;
    private Button mBtnBackSpace;
    private Button mBtnComplete;

    private Button[][] mBtnRows;
    private List<MySecurityKeyboardModel> mKeyModels = new ArrayList<>();
    private MySecurityKeyboardModel[] mInputedKeyModels;
    private EditText[] mEditTexts;

    @NonNull private SecurityKeyboardListener mKeyboardActionListener;
    @Nullable private GlobalSecurityKeyboardListener mGlobalKeyboardListener;


    public void setKeyboardActionListener(SecurityKeyboardListener keyboardActionListener) {
        mKeyboardActionListener = keyboardActionListener;
    }

    public void setGlobalKeyboardListener(GlobalSecurityKeyboardListener globalKeyboardListener) {
        mGlobalKeyboardListener = globalKeyboardListener;
    }


    /**
     * 이 키보드와 연결되는 에딧 텍스트를 바인딩 한다.
     * (필수로 사용하는 클래스에서 호출해줘야함)
     *
     * @param editTexts
     */
    public void bindEditTexts(EditText[] editTexts) {
        this.mEditTexts = editTexts;
        this.mInputedKeyModels = new MySecurityKeyboardModel[mEditTexts.length];

        for (int i=0; i < mEditTexts.length; i++) {
            EditText editText = mEditTexts[i];
            editText.setMaxEms(1);
            editText.setInputType(InputType.TYPE_NULL);     // 소프트 키보드 안뜨게 해줌
        }
    }

    public MySecurityKeyboard(Context context) {
        super(context);
        init(context);
    }

    public MySecurityKeyboard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MySecurityKeyboard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setBackgroundColor(getResources().getColor(R.color.gray_text_color));
        inflate(context, R.layout.view_custom_security_keyboard, this);

        mTableLayout = (TableLayout) findViewById(R.id.table_keys);
        mBtn1 = (Button) findViewById(R.id.btn1);
        mBtn2 = (Button) findViewById(R.id.btn2);
        mBtn3 = (Button) findViewById(R.id.btn3);
        mBtn4 = (Button) findViewById(R.id.btn4);
        mBtn5 = (Button) findViewById(R.id.btn5);
        mBtn6 = (Button) findViewById(R.id.btn6);
        mBtn7 = (Button) findViewById(R.id.btn7);
        mBtn8 = (Button) findViewById(R.id.btn8);
        mBtn9 = (Button) findViewById(R.id.btn9);
        mBtn10 = (Button) findViewById(R.id.btn10);
        mBtn11 = (Button) findViewById(R.id.btn11);
        mBtn12 = (Button) findViewById(R.id.btn12);
        mBtn0 = (Button) findViewById(R.id.btn0);

        mBtnRows = new Button[][]{
                {mBtn1, mBtn2, mBtn3, mBtn4},
                {mBtn5, mBtn6, mBtn7, mBtn8},
                {mBtn9, mBtn10, mBtn11, mBtn12},
                {mBtn0}
        };

        mBtnRearrange = (Button) findViewById(R.id.btn_rearrange) ;
        mBtnRearrange.setOnClickListener(this);
        mBtnBackSpace = (Button) findViewById(R.id.btn_backspace);
        mBtnBackSpace.setOnClickListener(this);
        mBtnComplete = (Button) findViewById(R.id.btn_complete);
        mBtnComplete.setOnClickListener(this);
    }

    public void show(final int startFocusIndex, final boolean rearrange) {
        MyLog.d("startFocusIndex = " + startFocusIndex);
        mBtnRearrange.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(rearrange)
                    rearrangeKeys();

                mTableLayout.setVisibility(View.VISIBLE);
                mEditTexts[startFocusIndex].requestFocus();
            }
        }, 200);

        setVisibility(View.VISIBLE);
    }

    public void hide() {
        mTableLayout.setVisibility(View.GONE);
        setVisibility(View.GONE);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        MyLog.i("changed=" + changed + ", " + l + ", " + t + ", " + r + ", " + b);

        if(b - t > 0) {
            // 키보드 나타남
            boolean firstShown = mKeyModels.size() == 0;
            if(firstShown) {
                arrangeKeys();
            }

            mKeyboardActionListener.onShownKeyboard(t, firstShown);

            if(mGlobalKeyboardListener != null)
                mGlobalKeyboardListener.onShownKeyboard(this);
        }
    }

    public void removeInputKey(int index) {
        mInputedKeyModels[index] = null;
        mEditTexts[index].setText("");
    }

    public void rearrangeKeys() {
        mKeyModels.clear();
        arrangeKeys();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_rearrange) {
            rearrangeKeys();

        } else if (i == R.id.btn_backspace) {
            int index = findFocusEditTextIndex();

            if (index >= 0) {
                boolean removed = false;

                if (mEditTexts[index].length() > 0) {
                    // 입력되어 있으면 지움
                    // 입력되어 있지 않다면, 아래에서 이전 입력칸으로 이동 후 이전 입력칸 지움
                    removeInputKey(index);
                    removed = true;
                }

                // 이전 입력칸으로 이동
                if (index > 0) {
                    mEditTexts[index - 1].requestFocus();

                    if (!removed) {
                        removeInputKey(index - 1);
                    }
                }

            } else {
                MyLog.w("no focused EditText..");
            }


        } else if (i == R.id.btn_complete) {
            mKeyboardActionListener.onCompleteInput(this);

        }

    }

    private int findFocusEditTextIndex() {
        for(int i=0; i < mEditTexts.length; i++) {
            EditText editText = mEditTexts[i];

            if(editText.hasFocus()) {
                return i;
            }
        }

        return -1;
    }

    private void arrangeKeys() {
        Random random = new Random(System.currentTimeMillis());

        int num = 0;
        for(int i = 0; i < 3; i++) {
            int rand = random.nextInt(4);

            for(int j = 0; j < 4; j++) {
//                MyLog.d("i=" + i + ", j=" + j + ", rand=" + rand + ", num=" + num);

                if( j == rand ) {
                    mBtnRows[i][j].setBackgroundResource(android.R.color.transparent);
                    mBtnRows[i][j].setEnabled(false);
                    mBtnRows[i][j].setText("");
                } else {
                    num++;
                    mBtnRows[i][j].setBackgroundResource(R.drawable.shape_main_color_round_box);
                    mBtnRows[i][j].setEnabled(true);
                    mBtnRows[i][j].setText(String.valueOf(num));

                    bindModel(mBtnRows[i][j], num);

//                    MyLog.d("키 생성 = " +  mBtnRows[i][j].getTag() + ", num=" + num);
                }

                mBtnRows[i][j].setVisibility(View.VISIBLE);
            }
        }

        // 키보드 0 바인드
        mBtnRows[3][0].setText("0");
        bindModel(mBtnRows[3][0], 0);

//        debugKeyModels();
    }

    private void debugKeyModels() {
        for (MySecurityKeyboardModel model : mKeyModels) {
            MyLog.d("model = " + model);
        }
    }

    private void debugInputKeys() {
        for (MySecurityKeyboardModel model : mInputedKeyModels) {
            MyLog.d("model = " + model);
        }
    }

    private void bindModel(Button button, int num) {
        MySecurityKeyboardModel model = new MySecurityKeyboardModel(num);
        button.setTag(model);
        button.getHitRect(model.getArea());

        View tableRow = (View) button.getParent();
        model.getArea().offset(0, tableRow.getTop());

        MyLog.d(num + " button area = " + model.getArea().toString());

        mKeyModels.add(model);
    }

    private MySecurityKeyboardModel findTouchedModel(int x, int y) {
//        MyLog.d("x=" + x + ", y=" + y);

        for (MySecurityKeyboardModel model : mKeyModels) {
//            MyLog.d("model.getArea()=" + model.getArea().toString());

            if(model.getArea().contains(x, y)) {
                MyLog.i("found input key model!! > " + model);
                return model;
            }
        }

        return null;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        MyLog.i("action = " + ev.getAction());

        if(ev.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) ev.getX();
            int y = (int) ev.getY();

            MySecurityKeyboardModel touchedModel = findTouchedModel(x, y);

            String text = x + ", " + y;
            if (touchedModel != null) {
//                text = text + " 터치 " + touchedModel.getDecryptedValue();
                inputKey(touchedModel);
//                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();

                //todo 버튼 눌렸을때 처리
//                Button btn = getPressedButton(touchedModel.getArea(), x, y);
//                btn.dispatchTouchEvent(ev);

//                if(btn != null)
//                    btn.onTouchEvent(ev);

                return false;
            }

        } else if(ev.getAction() == MotionEvent.ACTION_UP) {

        }

        return super.onInterceptTouchEvent(ev);
    }

    private Button getPressedButton(Rect area, int x, int y) {

        for (Button[] btnRow : mBtnRows) {
            for (Button btn : btnRow) {
                Rect r = new Rect();
                btn.getHitRect(r);

                if(r.contains(x, y)) {
                    MyLog.i("found touched Button >> " + btn.getText().toString());
                    return btn;
                }
            }
        }

        return null;
    }

    private void inputKey(MySecurityKeyboardModel touchedModel) {
        MyLog.i("touchedModel = " + touchedModel);

        for(int i=0; i < mEditTexts.length; i++) {
            EditText editText = mEditTexts[i];

            MyLog.d("editText " + i + " hasFocus = " + editText.hasFocus());
            if(editText.hasFocus() && (editText.hasSelection() || editText.length() == 0)) {
                mInputedKeyModels[i] = touchedModel;
                editText.setText("*");
                break;
            }
        }

//        debugInputKeys();
    }

//    public String getEncryptedInputKeys() {
//        String input = "";
//        for (MySecurityKeyboardModel model : mInputedKeyModels) {
//            if(model != null) {
//                input += model.getEncryptedValue() + ", ";
//            } else {
//                input += "null, ";
//            }
//        }
//
//        return input;
//    }

    //todo 나중에 삭제
    public String getInputPrintKeys() {
        String str = "";
        for (MySecurityKeyboardModel model : mInputedKeyModels) {
            if(model != null) {
                str += model.getPrintKey() + ", ";
            } else {
                str += "null, ";
            }
        }

        return str;
    }

    //todo 나중에 삭제
    public String getPlainInputKeys() {
        String input = "";
        for (MySecurityKeyboardModel model : mInputedKeyModels) {
            if(model != null) {
                input += model.getDecryptedValue() + ", ";
            } else {
                input += "null, ";
            }
        }

        return input;
    }

    /**
     * 서버로 최종 암호화해서 보낼때의 암호값
     * @return
     */
    public String getEncryptedInputValuesToServer() {
        String[] encKeyArr = new String[mInputedKeyModels.length];
        String[] encValueArr = new String[mInputedKeyModels.length];

        for(int i=0; i < mInputedKeyModels.length; i++) {
            MySecurityKeyboardModel inputedKeyModel = mInputedKeyModels[i];
            MyLog.d("i=" + i + ", inputKeyModel = " + inputedKeyModel);
            encKeyArr[i] = inputedKeyModel.getEncryptKey();
            encValueArr[i] = inputedKeyModel.getEncryptedValue();
        }

        String twoDepthEncValue = MySecurityKeyboardModel.getTwoDepthEncryptedValue(getContext(), encKeyArr, encValueArr);
        MyLog.i("twoDepthEncValue = " + twoDepthEncValue);
        return twoDepthEncValue;
    }

    public void clear() {
        for(int i = 0; i < mEditTexts.length; i++)
            mInputedKeyModels[i] = null;
    }

    public void removeAllInputKeys() {
        for (int i = 0; i < mEditTexts.length; i++) {
            removeInputKey(i);
        }
    }

    interface SecurityKeyboardListener {

        /**
         * 보안키보드가 위치할 부모 뷰를 정의해준다. 필수로 RelativeLayout 이여야 한다.
         * (필수로 호출해야함)
         *
         * <RelativeLayout>
         *     <View /> ==> 사용할 뷰(그룹)은 이 위치에 정의함
         *
         *     ==> 이 위치에 MySecurityKeyboard 가 들어감
         * </RelativeLayout>
         *
         *
         * @param parentView
         */
        void setToAttachKeyboardViewParent(RelativeLayout parentView);

        /**
         * 입력 완료 명령을 받고 키보드를 내릴때 호출되는 콜백
         * @param keyboard
         */
        void onCompleteInput(MySecurityKeyboard keyboard);

        /**
         * 키보드가 보여질 때 호출
         * @param top 상단 좌표값
         * @param firstShown
         */
        void onShownKeyboard(int top, boolean firstShown);
    }

    /**
     * 입력할 CustomSecurityInputArea 가 여려개일때,
     * 사용하는 클라이언트에서 등록한다.
     * 어떤 입력 영역에서 키보드가 호출되었는지 구분하기 위함.
     */
    public interface GlobalSecurityKeyboardListener {
        void onShownKeyboard(MySecurityKeyboard keyboard);
    }
}
