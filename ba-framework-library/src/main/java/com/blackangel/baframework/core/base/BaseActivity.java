package com.blackangel.baframework.core.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.blackangel.baframework.R;
import com.blackangel.baframework.app.constants.AppErrorCode;
import com.blackangel.baframework.core.model.BaseError;
import com.blackangel.baframework.logger.MyLog;
import com.blackangel.baframework.network.ApiProgressListener;
import com.blackangel.baframework.ui.dialog.PermissionConfirmationDialog;
import com.blackangel.baframework.ui.dialog.custom.AlertDialogFragment;
import com.blackangel.baframework.ui.dialog.custom.CustomDialogFragment;
import com.blackangel.baframework.ui.dialog.custom.DialogItems;
import com.blackangel.baframework.ui.dialog.custom.DialogListeners;
import com.blackangel.baframework.util.BuildUtil;

/**
 * Created by KimJeongHun on 2016-05-19.
 */
public class BaseActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener, ApiProgressListener {
    protected final String TAG = this.getClass().getSimpleName();

    protected Toolbar mToolbar;
    private ProgressDialog mLoadingProgressDialog;
    protected ViewGroup mRootLayout;        // 액티비티의 최상위 루트 레이아웃(액션바 포함)
    protected ViewGroup mContentsLayout;    // 타이틀바 아래에 들어갈 액티비티의 내용 레이아웃

    public ViewGroup getRootLayout() {
        return mRootLayout;
    }

    public ViewGroup getContentsLayout() {
        return mContentsLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyLog.i(TAG, "onCreate savedInstanceState=" + savedInstanceState);
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        initToolbar();

        mRootLayout = (ViewGroup) findViewById(R.id.layout_activity_root);
        mContentsLayout = (ViewGroup) findViewById(R.id.layout_activity_contents);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getLayoutInflater().inflate(layoutResID, mContentsLayout);
    }

    protected void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if(mToolbar != null) {
            setSupportActionBar(mToolbar);

            ActionBar actionBar = getSupportActionBar();

            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setDefaultDisplayHomeAsUpEnabled(true);
            mToolbar.setOnMenuItemClickListener(this);
        }
    }

    protected void initToolbarWithOnBackPressed() {
        initToolbar(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    protected void initToolbar(View.OnClickListener naviClick) {
        if(mToolbar != null) {
            mToolbar.setNavigationOnClickListener(naviClick);
        }
    }

    protected void initToolbar(int naviDrawableResId, View.OnClickListener naviClick) {
        if(mToolbar != null) {
            mToolbar.setNavigationIcon(naviDrawableResId);
            mToolbar.setNavigationOnClickListener(naviClick);
        }
    }

    protected void hideToolbar() {
        if(mToolbar != null) {
            mToolbar.setVisibility(View.GONE);
        }
    }

    public void addFragment(int resId, Fragment fragment) {
        addFragment(resId, fragment, fragment.getClass().getSimpleName());
    }

    private void addFragment(int resId, Fragment fragment, String tag) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(resId, fragment, tag);
        ft.commitAllowingStateLoss();
    }

    public void addFragment(int resId, Fragment fragment, boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(resId, fragment, fragment.getClass().getSimpleName());
        if(addToBackStack)
            ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }

    public void addFragmentWithAnim(int resId, Fragment fragment, boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out);
        ft.add(resId, fragment, fragment.getClass().getSimpleName());
        if(addToBackStack)
            ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }

    public void replaceFragment(int resId, Fragment fragment) {
        replaceFragment(resId, fragment, fragment.getClass().getSimpleName(), false);
    }

    public void replaceFragment(int resId, Fragment fragment, boolean addToBackStack) {
        replaceFragment(resId, fragment, fragment.getClass().getSimpleName(), addToBackStack);
    }

    public void replaceFragment(int resId, Fragment fragment, String tag, boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(resId, fragment, tag);
        if(addToBackStack)
            ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }

    public void replaceFragmentWithAnim(int resId, Fragment fragment) {
        this.replaceFragmentWithAnim(resId, fragment, false);
    }

    public void replaceFragmentWithTransition(int resId, Fragment fragment, View view, String transition) {
        this.replaceFragmentWithTransition(resId, fragment, false, view, transition);
    }

    public void replaceFragmentWithUpAnim(int resId, Fragment fragment) {
        replaceFragmentWithUpAnim(resId, fragment, false);
    }

    public void replaceFragmentWithUpAnim(int resId, Fragment fragment, boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_up_in, R.anim.slide_up_out, R.anim.slide_down_in, R.anim.slide_down_out);
        ft.replace(resId, fragment, fragment.getClass().getSimpleName());
        if(addToBackStack)
            ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }

    public void replaceFragmentWithAnim(int resId, Fragment fragment, boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out);
        ft.replace(resId, fragment, fragment.getClass().getSimpleName());
        if(addToBackStack)
            ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
        getSupportFragmentManager().executePendingTransactions();
    }

    public void replaceFragmentWithTransition(int resId, Fragment fragment, boolean addToBackStack, View view, String transition) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(resId, fragment, fragment.getClass().getSimpleName());
        if(addToBackStack)
            ft.addToBackStack(null);
        ft.addSharedElement(view, transition);
        ft.commitAllowingStateLoss();
    }

    public void replaceFragmentWithBackAnim(int resId, Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_right_out, R.anim.slide_right_in, R.anim.slide_left_out);
        ft.replace(resId, fragment, fragment.getClass().getSimpleName());
        ft.commitAllowingStateLoss();
    }

    protected Fragment getCurrentFragment(String tag) {
        return getSupportFragmentManager().findFragmentByTag(tag);
    }

    public void removeFragment(String tag) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(tag);

        MyLog.d("found fragment = " + fragment);

        if(fragment != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            ft.remove(fragment);
            ft.commitAllowingStateLoss();
        }
    }

    public void removeFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();

        if(fragment != null) {
            MyLog.d("fragment = " + fragment.getClass().getSimpleName());

            FragmentTransaction ft = fm.beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            ft.remove(fragment);
            ft.commitAllowingStateLoss();
        }
    }

    public void removeFragment(Fragment fragment, int enterAnim, int exitAnim) {
        FragmentManager fm = getSupportFragmentManager();

        if(fragment != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(enterAnim, exitAnim);
            ft.remove(fragment);
            ft.commitAllowingStateLoss();
        }
    }

    public Fragment findFragment(Class<? extends Fragment> fragmentClass) {
        return getSupportFragmentManager().findFragmentByTag(fragmentClass.getSimpleName());
    }

    public ProgressDialog createProgressDialog() {
        return new ProgressDialog(this, R.style.LoadingProgressStyle);
    }

    public void showProgress() {
        if(!isFinishing()) {
            if(mLoadingProgressDialog == null) {
                mLoadingProgressDialog = createProgressDialog();
                mLoadingProgressDialog.setCancelable(false);
                mLoadingProgressDialog.setCanceledOnTouchOutside(false);
            }

            if(!isFinishing() && !mLoadingProgressDialog.isShowing()) {
                mLoadingProgressDialog.setMessage("");
                mLoadingProgressDialog.dismiss();
                mLoadingProgressDialog.show();
            }
        }
    }

    public void showProgress(String message) {
        if(!isFinishing()) {
            if(mLoadingProgressDialog == null) {
                mLoadingProgressDialog = createProgressDialog();
                mLoadingProgressDialog.setCancelable(false);
                mLoadingProgressDialog.setCanceledOnTouchOutside(false);
            }

            if(mLoadingProgressDialog.isShowing()) {
                mLoadingProgressDialog.setMessage(message);
            } else {
                mLoadingProgressDialog.dismiss();
                mLoadingProgressDialog.setMessage(message);
                mLoadingProgressDialog.show();
            }
        }
    }

    public void hideProgress() {
        if(!isFinishing()) {
            if(mLoadingProgressDialog != null && mLoadingProgressDialog.isShowing()) {
                mLoadingProgressDialog.dismiss();
            }
        }
    }

    public void showErrorDialog(String message) {
        showErrorDialog(message, true, null);
    }

    public void showErrorDialogNotCancelable(String message,
                                             DialogInterface.OnClickListener buttonClickListener) {
        showErrorDialog(message, false, buttonClickListener);
    }

    public void showErrorDialog(String message, boolean cancelable) {
        showErrorDialog(message, cancelable, null);
    }

    public void showErrorDialogOnClickFinish(int msgResId) {
        showErrorDialogOnClickFinish(getString(msgResId));
    }

    public void showErrorDialogOnClickFinish(String msg) {
        AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(getString(R.string.alert), msg,
                false);

        showGlobalAlertDialog(alertDialogFragment, alertDialogFragment.getClass().getSimpleName(),
                (dialog, which) -> finish());
    }

    public void showErrorDialog(String message, boolean cancelable,
                                DialogInterface.OnClickListener onClickListener) {
        AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(getString(R.string.alert), message,
                cancelable);

        showGlobalAlertDialog(alertDialogFragment, alertDialogFragment.getClass().getSimpleName(), onClickListener);
    }

    public void showAlertDialog(int msgResId) {
        showAlertDialog(getString(msgResId), null);
    }

    public void showAlertDialog(String msg) {
        showAlertDialog(msg, null);
    }

    protected void showAlertDialogNotCancelable(String msg) {
        showAlertDialogNotCancelable(msg, (DialogInterface.OnClickListener) null);
    }

    protected void showAlertDialog(String msg, DialogInterface.OnClickListener onOKClick) {
        DialogItems dialogItems = new DialogItems.Builder(this)
                .setContentMessage(msg)
                .setPositiveButton(android.R.string.ok)
                .build();

        showCustomDialog(dialogItems, onOKClick);
    }

    public void showAlertDialogNotCancelable(int msgResId, DialogInterface.OnClickListener onOKClick) {
        DialogItems dialogItems = new DialogItems.Builder(this)
                .setContentMessage(msgResId)
                .setPositiveButton(android.R.string.ok)
                .setCancelable(false)
                .build();

        showCustomDialog(dialogItems, onOKClick);
    }

    public void showAlertDialogNotCancelable(String msg, DialogInterface.OnClickListener btnClickListener) {
        DialogItems dialogItems = new DialogItems.Builder(this)
                .setContentMessage(msg)
                .setPositiveButton(android.R.string.ok)
                .setCancelable(false)
                .build();

        showCustomDialog(dialogItems, btnClickListener);
    }

    public void showAlertDialogNotCancelable(String msg, DialogInterface.OnShowListener onShowListener) {
        DialogItems dialogItems = new DialogItems.Builder(this)
                .setContentMessage(msg)
                .setPositiveButton(android.R.string.ok)
                .setCancelable(false)
                .build();

        showCustomDialog(dialogItems, onShowListener, null);
    }

    public void showAlertDialogNotCancelable(String msg, DialogInterface.OnShowListener onShowListener, DialogInterface.OnClickListener onBtnClickListener) {
        DialogItems dialogItems = new DialogItems.Builder(this)
                .setContentMessage(msg)
                .setPositiveButton(android.R.string.ok)
                .setCancelable(false)
                .build();

        showCustomDialog(dialogItems, onShowListener, onBtnClickListener);
    }

    public void showAlertDialogOnClickFinish(String msg) {
        DialogItems dialogItems = new DialogItems.Builder(this)
                .setContentMessage(msg)
                .setPositiveButton(android.R.string.ok)
                .setCancelable(false)
                .build();

        showCustomDialog(dialogItems, (dialog, which) -> finish());
    }

    public void showAlertDialogOnClickFinish(int msgResId) {
        DialogItems dialogItems = new DialogItems.Builder(this)
                .setContentMessage(msgResId)
                .setPositiveButton(android.R.string.ok)
                .setCancelable(false)
                .build();

        showCustomDialog(dialogItems, (dialog, which) -> finish());
    }

    public void showYesNoDialog(int msgResId, int positiveBtnMsgId, int negativeBtnMsgId,
                                DialogInterface.OnClickListener dialogButtonClickListener) {
        DialogItems dialogItems = new DialogItems.Builder(this)
                .setContentMessage(msgResId)
                .setPositiveButton(positiveBtnMsgId)
                .setNegativeButton(negativeBtnMsgId)
                .build();

        showCustomDialog(dialogItems, dialogButtonClickListener);
    }

    public void showYesNoDialog(String msg, int positiveMsgId, int negativeMsgId, DialogInterface.OnClickListener dialogButtonClickListener) {
        DialogItems dialogItems = new DialogItems.Builder(this)
                .setContentMessage(msg)
                .setPositiveButton(positiveMsgId)
                .setNegativeButton(negativeMsgId)
                .build();

        showCustomDialog(dialogItems, dialogButtonClickListener);
    }

    public void showYesNoDialogNotCancelable(int msgResId, int positiveMsgId, int negativeMsgId, DialogInterface.OnClickListener dialogButtonClickListener) {
        showYesNoDialogNotCancelable(getString(msgResId), positiveMsgId, negativeMsgId, dialogButtonClickListener);
    }

    public void showYesNoDialogNotCancelable(String msg, int positiveMsgId, int negativeMsgId, DialogInterface.OnClickListener dialogButtonClickListener) {
        DialogItems dialogItems = new DialogItems.Builder(this)
                .setContentMessage(msg)
                .setPositiveButton(positiveMsgId)
                .setNegativeButton(negativeMsgId)
                .setCancelable(false)
                .build();

        showCustomDialog(dialogItems, dialogButtonClickListener);
    }

    public void showCustomDialog(DialogItems dialogItems, String tag, DialogInterface.OnClickListener btnClickListener) {
        DialogListeners dialogListeners = new DialogListeners.Builder()
                .setButtonClickListener(btnClickListener)
                .build();

        this.showCustomDialog(tag, dialogItems, dialogListeners);
    }

    public void showCustomDialog(DialogItems dialogItems, String tag,
                                 DialogInterface.OnClickListener btnClickListener,
                                 DialogInterface.OnDismissListener onDismissListener) {

        DialogListeners dialogListeners = new DialogListeners.Builder()
                .setButtonClickListener(btnClickListener)
                .setOnDismissListener(onDismissListener)
                .build();

        this.showCustomDialog(tag, dialogItems, dialogListeners);
    }

    public void showCustomDialog(DialogItems dialogItems, DialogInterface.OnClickListener btnClickListener) {
        DialogListeners dialogListeners = new DialogListeners.Builder()
                .setButtonClickListener(btnClickListener)
                .build();

        this.showCustomDialog(dialogItems, dialogListeners);
    }

    public void showCustomDialog(DialogItems dialogItems, DialogInterface.OnShowListener onShowListener,
                                 DialogInterface.OnClickListener btnClickListener) {
        DialogListeners dialogListeners = new DialogListeners.Builder()
                .setButtonClickListener(btnClickListener)
                .setOnShowListener(onShowListener)
                .build();

        this.showCustomDialog(dialogItems, dialogListeners);
    }

    public void showCustomDialog(DialogItems dialogItems, DialogListeners dialogListeners) {
        showCustomDialog(CustomDialogFragment.TAG, dialogItems, dialogListeners);
    }

    public void showCustomDialog(String tag, DialogItems dialogItems, @NonNull DialogListeners dialogListeners) {
        CustomDialogFragment customDialog = CustomDialogFragment.newInstance(dialogItems);
        customDialog.setDialogCustomViewInflater(dialogListeners.getDialogCustomViewInflater());
        customDialog.setButtonClickListener(dialogListeners.getButtonClickListener());
        customDialog.setOnShowListener(dialogListeners.getOnShowListener());
        customDialog.setOnDismissListener(dialogListeners.getOnDismissListener());

        showCustomDialogInternal(customDialog, tag);
    }

    private void showCustomDialogInternal(CustomDialogFragment customDialog, String tag) {
        if (!isFinishing()) {
            FragmentManager fm = getSupportFragmentManager();
            Fragment prev = fm.findFragmentByTag(tag);

//            if(prev != null) {
//                CustomDialogFragment prevCustomDialog = (CustomDialogFragment) prev;
//                try {
//                    prevCustomDialog.dismissAllowingStateLoss();
//                } catch (IllegalStateException e) {
//                    e.printStackTrace();
//                }
//            }

            if (prev == null) {
                MyLog.w(this.getClass().getSimpleName() + " " + tag + " dialog prev no exist " + System.currentTimeMillis());
            } else {
                MyLog.w(this.getClass().getSimpleName() + " " + tag + " dialog prev exist!! " + System.currentTimeMillis());
            }

            // 만약 기존에 떠있는 팝업이 있었다면 다시 띄우지 않는다.
            if (prev == null) {
                FragmentTransaction ft = fm.beginTransaction();
                ft.add(customDialog, tag);
                ft.commitNowAllowingStateLoss();
                // uiThread 에서 동기화하여 커밋함. 이 때문에 팝업을 띄울때 조심해야함
                // 프래그먼트 트랜잭션 이 완료되지 않았을때, (예를 들면 onActivityCreated 에서 바로)
                // 팝업을 띄우려고 이 부분이 호출되면 크래쉬난다.
                // 이를 피하려면 호출부에서 view.post() 메서드 안에서 호출하야한다.

                MyLog.w(this.getClass().getSimpleName() + " " + tag + " dialog shown!! " + System.currentTimeMillis());
            }
        }
    }

    public void showCustomDialog(DialogItems dialogItems, String tag,
                                 DialogInterface.OnDismissListener onDismissListener) {
        DialogListeners dialogListeners = new DialogListeners.Builder()
                .setOnDismissListener(onDismissListener)
                .build();

        showCustomDialog(tag, dialogItems, dialogListeners);
    }

    public void showCustomDialog(DialogItems dialogItems, String tag, DialogInterface.OnShowListener onShowListener) {
        DialogListeners dialogListeners = new DialogListeners.Builder()
                .setOnShowListener(onShowListener)
                .build();

        showCustomDialog(tag, dialogItems, dialogListeners);
    }

    public void showCustomDialog(CustomDialogFragment customDialogFragment) {
        showCustomDialog(customDialogFragment, customDialogFragment.getClass().getSimpleName());
    }

    public void showCustomDialog(CustomDialogFragment customDialogFragment, String tag) {
        if (!isFinishing()) {

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            Fragment prev = fm.findFragmentByTag(tag);
            if (prev != null) {
//                removeCustomDialog(tag);
                CustomDialogFragment customDialog = (CustomDialogFragment) prev;
                try {
                    customDialog.dismissAllowingStateLoss();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }

            ft.add(customDialogFragment, tag);
            ft.commitNowAllowingStateLoss();
        }
    }

    public void showGlobalAlertDialog(AlertDialogFragment dialogFragment, String tag, DialogInterface.OnClickListener onClickListener) {
        if (!isFinishing()) {
            dialogFragment.setOnButtonClickListener(onClickListener);

            try {
                dialogFragment.show(getSupportFragmentManager(), tag);

            } catch (IllegalStateException e) {
                e.printStackTrace();

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                Fragment prev = fm.findFragmentByTag(tag);
                if (prev != null) {
                    DialogFragment prevDialogFragment = (DialogFragment) prev;
                    try {
                        prevDialogFragment.dismissAllowingStateLoss();
                    } catch (IllegalStateException e1) {
                        e1.printStackTrace();
                    }
                }

                ft.add(dialogFragment, tag);
                ft.commitAllowingStateLoss();
            }
        }
    }

    public void removeCustomDialog() {
        this.removeCustomDialog(CustomDialogFragment.class.getSimpleName());
    }

    public void removeCustomDialog(String tag) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment prev = fm.findFragmentByTag(tag);

        if (prev != null) {
            CustomDialogFragment customDialog = (CustomDialogFragment) prev;
            try {
                customDialog.dismissAllowingStateLoss();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }


    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void showToast(int msgResId) {
        Toast.makeText(this, msgResId, Toast.LENGTH_LONG).show();
    }

    /**
     * 툴바 메뉴 아이템 클릭 콜백
     * 툴바가 있을 때는 이 메소드를 오버라이드 한다.
     * @param item
     * @return
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                if(getParent() != null) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    public void hideCurrentFocusKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void requestPermission(String permission, int requestCode, int permissonReqMsgId, boolean isCancelActFinish) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            PermissionConfirmationDialog.newInstance(getResources().getString(permissonReqMsgId),
                    permission, requestCode, isCancelActFinish)
                    .show(getSupportFragmentManager(), PermissionConfirmationDialog.TAG);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    public boolean isDialogShown() {
        Fragment dialogFragment = getSupportFragmentManager().findFragmentByTag(CustomDialogFragment.class.getSimpleName());
        return dialogFragment != null && dialogFragment.isVisible();
    }

    public void startActivity(Class<? extends Activity> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    protected void startActivity(Class<? extends Activity> activityClass, int... flags) {
        Intent intent = new Intent(this, activityClass);
        for (int flag : flags) {
            intent.addFlags(flag);
        }
        startActivity(intent);
    }

    public void startActivity(@NonNull IntentExtraProvider intentExtraProvider, Class<? extends Activity> activityClass) {
        Intent intent = new Intent(this, activityClass);
        intentExtraProvider.putExtras(intent);
        startActivity(intent);
    }

    protected void startActivityForResult(Class<? extends Activity> activityClass, int requestCode) {
        Intent intent = new Intent(this, activityClass);
        startActivityForResult(intent, requestCode);
    }

    protected void startActivityForResult(@NonNull IntentExtraProvider intentExtraProvider, Class<? extends Activity> activityClass, int requestCode) {
        Intent intent = new Intent(this, activityClass);
        intentExtraProvider.putExtras(intent);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MyLog.d("requestCode=" + requestCode + ", resultCode=" + resultCode, data);
    }

    @Override
    public void onStartApi(String progressMsg) {
        if(progressMsg == null || progressMsg.isEmpty())
            showProgress();
        else
            showProgress(progressMsg);
    }

    @Override
    public void onFinishApi() {
        if(BuildUtil.isAboveJellyBean17() && this.isDestroyed()) {
            return;
        }

        hideProgress();
    }

    /**
     * 기본 글로벌 에러 핸들 로직 - 에러코드를 맵핑하여 사용자에게 보여줄 메시지를 다시 정의한 후 팝업만 띄운다.
     * 다른 처리가 필요하면 오버라이드 하여 처리한다.
     *
     * @param globalError   액티비티단에서 공통으로 잡아서 처리해야할 글로벌 에러
     * @param extras        추가적으로 넘어오는 파라미터
     */
    public void handleGlobalError(BaseError globalError, Object... extras) {
        MyLog.i();

        if (globalError.getErrCode() == AppErrorCode.ERROR_CODE_UNKNOWN) {
            globalError.setErrMessage(getString(R.string.error_unknown));

        } else if (globalError.getErrCode() == AppErrorCode.ERROR_CODE_NETWORK_ERROR
                || globalError.getErrCode() == AppErrorCode.ERROR_CODE_BAD_GATE_WAY) {
            globalError.setErrMessage(getString(R.string.error_not_connected_network));
        }

        showAlertDialog(globalError.getErrMessage());
    }

    public interface IntentExtraProvider {
        void putExtras(Intent intent);
    }
}
