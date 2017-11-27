package com.angel.black.baframework.core.base;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.angel.black.baframework.R;
import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.preference.MyPreferenceManager;
import com.angel.black.baframework.ui.dialog.AlertDialogFragment;
import com.angel.black.baframework.ui.dialog.DialogClickListener;
import com.angel.black.baframework.ui.dialog.PermissionConfirmationDialog;
import com.angel.black.baframework.ui.dialog.custom.CustomDialogFragment;
import com.angel.black.baframework.ui.dialog.custom.DialogItems;

/**
 * Created by KimJeongHun on 2016-05-19.
 */
public class BaseActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {
    protected final String TAG = this.getClass().getSimpleName();

    protected Toolbar mToolbar;
    protected ProgressBar mLoadingProgress;
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
        BaLog.i(TAG, "onCreate savedInstanceState=" + savedInstanceState);
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
            actionBar.setDefaultDisplayHomeAsUpEnabled(true);
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

    public void addFragment(int resId, Fragment fragment, String tag, boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(resId, fragment, tag);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if(addToBackStack)
            ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }

    public void replaceFragment(int resId, Fragment fragment, String tag, boolean addToBackStack, boolean isAni) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if(isAni)
            ft.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out);
        if(addToBackStack)
            ft.addToBackStack(null);
        ft.replace(resId, fragment, tag);
        ft.commitAllowingStateLoss();
    }

    public void removeFragment(String tag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
//		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
//        ft.setCustomAnimations(R.anim.slide_down_to_up, R.anim.slide_up_to_down);
        ft.remove(fm.findFragmentByTag(tag));
        ft.commitAllowingStateLoss();
    }

    public void removeFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();

        if(fragment != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            ft.remove(fragment);
            ft.commitAllowingStateLoss();
        }
    }

    protected void startActivity(Class clazz) {
        this.startActivity(clazz, false);
    }

    protected void startActivity(Class clazz, boolean finish) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        if(finish) finish();
    }

    protected void startActivityForResult(Class clazz, int requestCode) {
        Intent intent = new Intent(this, clazz);
        startActivityForResult(intent, requestCode);
    }

    public void showProgress() {
        if(mLoadingProgress == null) {
            mLoadingProgress = (ProgressBar) findViewById(R.id.loading_progress);
        }
        mLoadingProgress.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        if(mLoadingProgress != null && mLoadingProgress.isShown()) {
            mLoadingProgress.setVisibility(View.GONE);
        }
    }

    public void showOkDialog(int strResId) {
        this.showOkDialog(getString(strResId));
    }

    public void showOkDialog(String message) {
        AlertDialogFragment dialogFragment = AlertDialogFragment.newInstance(null, message);
        showDialogFragment(dialogFragment, "okDlg");
    }

    public void showAlertDialog(int msgResId, DialogClickListener positiveClick) {
        this.showAlertDialog(getString(msgResId), positiveClick);
    }

    public void showAlertDialog(String message, DialogClickListener positiveClick) {
        AlertDialogFragment dialogFragment = AlertDialogFragment.newInstance(null, message, positiveClick);
        showDialogFragment(dialogFragment, "altDlgWithPosi");
    }

    public void showAlertDialogNotCancelable(int msgResId, DialogClickListener positiveClick) {
        AlertDialogFragment dialogFragment = AlertDialogFragment.newInstance(null, getString(msgResId), true, positiveClick);
        showDialogFragment(dialogFragment, "altDlgWithPosiNotCancel");
    }

    public void showAlertDialog(int msgResId) {
        showAlertDialog(getString(msgResId), null);
    }

    public void showAlertDialog(String msg) {
        showAlertDialog(msg, null);
    }


    private void showDialogFragment(DialogFragment dialogFragment, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.add(dialogFragment, tag);
        ft.commitAllowingStateLoss();
    }


    public void showCustomDialog(DialogItems dialogItems) {
        this.showCustomDialog(dialogItems, CustomDialogFragment.TAG);
    }

    public void showCustomDialog(DialogItems dialogItems, String tag) {
        if(!isFinishing()) {
            CustomDialogFragment customDialog;

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            Fragment prev = fm.findFragmentByTag(tag);
            if(prev != null) {
                removeCustomDialog();
            }

            customDialog = CustomDialogFragment.newInstance(dialogItems);
            ft.add(customDialog, tag);
            ft.commitAllowingStateLoss();
        }
    }


    public void showCustomDialog(DialogItems dialogItems, String tag, DialogInterface.OnDismissListener onDismissListener) {
        if(!isFinishing()) {
            CustomDialogFragment customDialog;

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            Fragment prev = fm.findFragmentByTag(tag);
            if(prev != null) {
                removeCustomDialog();
            }

            customDialog = CustomDialogFragment.newInstance(dialogItems);
            customDialog.setOnDismissListener(onDismissListener);

            ft.add(customDialog, tag);
            ft.commitAllowingStateLoss();
        }
    }

    public void showCustomDialog(CustomDialogFragment customDialogFragment) {
        showCustomDialog(customDialogFragment, customDialogFragment.getClass().getSimpleName());
    }

    public void showCustomDialog(CustomDialogFragment customDialogFragment, String tag) {
        if(!isFinishing()) {

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            Fragment prev = fm.findFragmentByTag(tag);
            if(prev != null) {
                removeCustomDialog();
            }

            ft.add(customDialogFragment, tag);
            ft.commitAllowingStateLoss();
        }
    }

    public void removeCustomDialog() {
        this.removeCustomDialog(CustomDialogFragment.TAG);
    }

    public void removeCustomDialog(String tag) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment prev = fm.findFragmentByTag(tag);

        if (prev != null) {
            CustomDialogFragment customDialog = (CustomDialogFragment) prev;
            try {
                customDialog.dismiss();
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

    public MyPreferenceManager getPreferenceManager() {
        return new MyPreferenceManager(this);
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
        Fragment dialogFragment = getSupportFragmentManager().findFragmentByTag(CustomDialogFragment.TAG);
        return dialogFragment != null && dialogFragment.isVisible();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        BaLog.d("requestCode=" + requestCode + ", resultCode=" + resultCode, data);
    }
}
