package com.blackangel.baframework.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.blackangel.baframework.R;
import com.blackangel.baframework.core.base.BaseMainActivity;
import com.blackangel.baframework.core.base.BaseWebActivity;
import com.blackangel.baframework.intent.IntentConst;
import com.blackangel.baframework.logger.MyLog;
import com.blackangel.baframework.sns.FacebookUtil;
import com.blackangel.baframework.sns.ISnsLoginParam;
import com.blackangel.baframework.sns.OnSnsLoginListener;
import com.blackangel.baframework.sns.SnsLoginResult;
import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;

/**
 * Created by Finger-kjh on 2017-11-28.
 */

public class SampleMainActivity extends BaseMainActivity {

    private CallbackManager fbCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookUtil.initialize(this);
        fbCallbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_sample_main);

        LoginButton btnFbLogin = (LoginButton) findViewById(R.id.btn_fb_login);
        if (!FacebookUtil.isLogined()) {
            FacebookUtil.loginFacebookViaFacebookButton(this, fbCallbackManager, btnFbLogin, new OnSnsLoginListener() {
                @Override
                public void onLoginCompleted(SnsLoginResult snsLoginResult, ISnsLoginParam... moreLoginParams) {
                    MyLog.i("snsLoginResult=" + snsLoginResult + "\n moreLoginParams=" + moreLoginParams);
                    showToast("snsLoginResult=" + snsLoginResult + "\n moreLoginParams=" + moreLoginParams);
                }

                @Override
                public void onLoginFailed(String message) {
                    MyLog.i();
                }
            });
        }

        Intent intent = new Intent(this, BaseWebActivity.class);
        intent.putExtra(IntentConst.KEY_TITLE, "ReLe 관리자");
        intent.putExtra(IntentConst.KEY_IS_AVAILABLE_LANDSCAPE, true);
        intent.setData(Uri.parse("http://sandbox.f-mt.co.kr:9100/login"));
        startActivity(intent);
        finish();
    }

    @Override
    protected void processScheme(Bundle extras) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fbCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
