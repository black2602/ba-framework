package com.blackangel.baframework.core.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.blackangel.baframework.R;
import com.blackangel.baframework.logger.MyLog;
import com.blackangel.baframework.ui.dialog.DialogClickListener;
import com.blackangel.baframework.ui.dialog.PermissionConfirmationDialog;
import com.blackangel.baframework.ui.dialog.custom.AlertDialogFragment;

/**
 * Created by KimJeongHun on 2016-06-24.
 */
public abstract class BaseFragment extends Fragment {
    public final String TAG = this.getClass().getSimpleName();

    private String mTitle;
    protected BaseActivity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity)
            mActivity = (BaseActivity) context;

        MyLog.i("mActivity = " + (mActivity == null ? "null" : mActivity.getClass().getSimpleName()));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i(this.getClass().getSimpleName() + ", savedInstanceState=" + savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MyLog.i(this.getClass().getSimpleName());
        View view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.setClickable(true);    // 이 옵션을 주어야 새로 뜨는 프래그먼트가 클릭이벤트를 받게되어, 뒤에 깔려있는 상위 프래그먼트로 터치이벤트가 가지 않는다.
        super.onViewCreated(view, savedInstanceState);
        MyLog.i(this.getClass().getSimpleName());
        view.setContentDescription(this.getClass().getSimpleName());       // UI 테스트를 위해
    }

    /**
     * BaseFragment 가 BaseActivity 에 확실하게 종속되었을 때 호출됨
     * mActivity 를 참조하는 어떤 객체나 로직이 있다면 여기서 오버라이드 하는 것을 추천함
     * (onAttach 에서는 activity 객체가 메모리 leak 된 객체일 수 있음)
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i(this.getClass().getSimpleName() + " savedInstanceState = " + savedInstanceState);
        setTitleProperty(getFragmentInitialTitle());
    }

    /**
     * 액션바에 표시할 최초 프래그먼트의 타이틀을 리턴한다.
     * null 을 리턴할 경우 기본적으로 앱 이름이 액션바에 표시됨
     * 필요시 오버라이드 한다.
     */
    protected String getFragmentInitialTitle() {
        return null;
    }

    protected ViewGroup getActivityContentsLayout() {
        return getBaseActivity().getContentsLayout();
    }

    protected BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    protected void startActivity(Class clazz) {
        this.startActivity(clazz, false);
    }

    protected void startActivity(Class clazz, boolean finish) {
        Intent intent = new Intent(getActivity(), clazz);
        startActivity(intent);
        if(finish) getActivity().finish();
    }

    public void hideCurrentFocusKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    public void showOkDialog(int strResId) {
        this.showOkDialog(getString(strResId));
    }

    protected void showOkDialog(String message) {
        AlertDialogFragment dialogFragment = AlertDialogFragment.newInstance(null, message);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "okDialog");
    }

    public void showAlertDialog(int msgResId, DialogInterface.OnClickListener positiveClick) {
        getBaseActivity().showAlertDialog(getString(msgResId), positiveClick);
    }

    protected void showAlertDialog(String msg) {
        if (mActivity == null)
            return;

        mActivity.showAlertDialog(msg);
    }

    public void showAlertDialog(String message, DialogClickListener positiveClick) {
        getBaseActivity().showAlertDialog(message, positiveClick);
    }

    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    public void showToast(int msgResId) {
        Toast.makeText(getActivity(), msgResId, Toast.LENGTH_LONG).show();
    }


    protected void showProgress() {
        getBaseActivity().showProgress();
    }

    protected void hideProgress() {
        getBaseActivity().hideProgress();
    }

    protected void addChildFragment(int containerResId, Fragment fragment, int enterAnim, int exitAnim) {
        FragmentManager fm = getChildFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(enterAnim, exitAnim);
        ft.add(containerResId, fragment);
        ft.commit();
    }

    protected void addChildFragment(int containerResId, Fragment fragment) {
        addChildFragment(containerResId, fragment, 0, 0);
    }

    protected void removeChildFragment(Fragment fragment, int enterAnim, int exitAnim) {
        FragmentManager fm = getChildFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(enterAnim, exitAnim);
        ft.remove(fragment);
        ft.commit();
    }

    protected void removeChildFragment(Fragment fragment) {
        removeChildFragment(fragment, 0, 0);
    }

    /**
     * 퍼미션(권한) 이 있는지 체크한다.
     * @param permission Manifest.Permisson 클래스 상수
     * @return  권한 있으면 true
     */
    protected boolean checkPermission(String permission) {
        return ActivityCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 퍼미션을 요청한다.
     * 처음 퍼미션을 요청하는 경우 퍼미션 요청 이유를 보여주는 팝업 띄운다.
     *
     * @param permission                    요청할 퍼미션(한개)
     * @param permissionReqReasonMsgId      퍼미션 요청 이유 문자 리소스 id
     * @param permissionRequestCode         퍼미션 요청 코드(PermissionConstants 에 정의)
     * @param finishIfCancel                퍼미션 요청 이유 보여주는 팝업에서 취소할 때 액티비티 종료할지 여부
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermission(String permission, int permissionReqReasonMsgId, int permissionRequestCode, boolean finishIfCancel) {
        if (shouldShowRequestPermissionRationale(permission)) {
            PermissionConfirmationDialog.newInstance(getResources().getString(permissionReqReasonMsgId),
                    permission, permissionRequestCode, finishIfCancel)
                    .show(getActivity().getSupportFragmentManager(), PermissionConfirmationDialog.TAG);
        } else {
//            ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, permissionRequestCode);
            requestPermissions(new String[]{permission}, permissionRequestCode);
        }
    }


    protected void showCurrentTitle() {
        showTitleInternal(mTitle);
    }

    /**
     * 임시적으로 타이틀을 셋팅한다. (프로퍼티에 저장안함)
     */
    public void setTitleTemporarily(String title) {
        showTitleInternal(title);
    }

    /**
     * 타이틀을 셋팅한다. (프로퍼티에 저장)
     */
    protected void setTitleProperty(String title) {
        mTitle = title;
    }

    /**
     * 타이틀을 셋팅 후 타이틀바에 표시한다.
     */
    protected void setTitlePropertyAndShow(String title) {
        mTitle = title;
        showTitleInternal(mTitle);
    }

    public String getTitleProperty() {
        MyLog.i("mTitle=" + mTitle);
        return mTitle;
    }

    public void setDefaultAppNameTitle() {
        mActivity.setTitle(getString(R.string.app_name));
    }

    private void showTitleInternal(String title) {
        if (TextUtils.isEmpty(title)) {
            setDefaultAppNameTitle();
        } else {
            mActivity.setTitle(title);
        }
    }
}
