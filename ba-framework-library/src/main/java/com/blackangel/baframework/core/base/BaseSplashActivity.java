package com.blackangel.baframework.core.base;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.blackangel.baframework.R;
import com.blackangel.baframework.core.AppSchemeData;
import com.blackangel.baframework.logger.MyLog;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Finger-kjh on 2017-11-28.
 */

public abstract class BaseSplashActivity extends BaseActivity {

    private static final long SPLASH_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSplashHandler.sendEmptyMessageDelayed(0, SPLASH_TIME);

        if(cacheSchemeDataIfExist()) {
            // Scheme URL 을 통해 앱 시작 명령을 받았을때
            if(!isTaskRoot()) {
                // 앱이 기존에 실행중이었을 때  // todo BaseMainActivity 를 상속한 액티비티를 넣어야한다.
                AppSchemeData.sAppSchemeData.goSchemeActionToMain(this, true, BaseMainActivity.class);
                finish();

            } else {
                // 앱이 꺼져있었을 때 -> 정상적인 프로세스로 메인까지 이동 후에 MainActivity 에서 처리한다.

            }
        }

        configPush();
    }

    /**
     * Scheme URL 을 통해 Intent 를 받았는지 여부를 조사하고, 그렇다면 Scheme 데이터를 캐시한다.
     * @return Scheme URL 을 통해 앱 시작 명령을 받았는지 여부
     */
    private boolean cacheSchemeDataIfExist() {
        Intent intent = getIntent();

        if(intent != null) {
            Uri uri = intent.getData();

            if (uri != null) {
                MyLog.d("uriString = " + uri.toString());

                if (uri.getScheme().equals(getString(R.string.app_scheme))) {
                    Map<String, String> queryMap = new HashMap<>();
                    String host = uri.getHost();

                    Set<String> queryParameterNames = uri.getQueryParameterNames();
                    Iterator<String> iterator = queryParameterNames.iterator();

                    while (iterator.hasNext()) {
                        String queryKey = iterator.next();
                        String queryValue = uri.getQueryParameter(queryKey);
                        MyLog.i("fcm key = " + queryKey + ", value = " + queryValue);
                        queryMap.put(queryKey, queryValue);
                    }

                    AppSchemeData.sAppSchemeData = new AppSchemeData(host, queryMap);
                    return true;
                }
            }
        }

        return false;
    }

    private Handler mSplashHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0) {
                onSplashEnd();
            }
        }
    };

    protected abstract void configPush();
    protected abstract void onSplashEnd();
}
