package com.blackangel.baframework.core.base;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

import com.blackangel.baframework.R;
import com.blackangel.baframework.intent.IntentConst;
import com.blackangel.baframework.logger.MyLog;
import com.blackangel.baframework.ui.dialog.custom.AbstractDialogButtonClickListener;
import com.blackangel.baframework.ui.dialog.custom.BaseCustomDialog;
import com.blackangel.baframework.ui.dialog.custom.DialogButtonClickListener;
import com.blackangel.baframework.util.MyPackageManager;
import com.blackangel.baframework.util.StringUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * Created by LeeHyeonJae on 2017-02-23.
 *
 * WebView 를 사용하는 Activity들의 공통기능을 모아놓을 목적으로 생성한 클래스
 *
 */
public class BaseWebActivity extends BaseActivity {
    protected WebView mWebView;
    private ProgressBar mProgressBar;
    private Button mBtnConfirm;
    private String[] mAllowedDomainUrls;
    private boolean mIsHideTitleBar;
    private String mTitle;

    @Override
    protected void onPreCreateContentView() {
        super.onPreCreateContentView();
        Intent intent = getIntent();
        boolean availableLandscape = intent.getBooleanExtra(IntentConst.KEY_IS_AVAILABLE_LANDSCAPE, false);
        setScreenOrientation(availableLandscape);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i();

        Intent intent = getIntent();
        String url = intent.getDataString();
        mTitle = intent.getStringExtra(IntentConst.KEY_TITLE);
        mIsHideTitleBar = intent.getBooleanExtra(IntentConst.KEY_IS_HIDE_TITLE_BAR, false);
        boolean showConfirmButton = intent.getBooleanExtra(IntentConst.KEY_IS_SHOW_BUTTON, false);
        String confirmTxt = intent.getStringExtra(IntentConst.KEY_BUTTON_TEXT);
        mAllowedDomainUrls = intent.getStringArrayExtra(IntentConst.KEY_ALLOWED_URLS);

        if(mIsHideTitleBar) {
            hideToolbar();
        } else {
            if(mTitle != null) {
                initToolbar(mTitle, true);
            } else {
                initToolbar(true);
            }
        }

        setContentView(R.layout.activity_base_webview);

        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.setScrollbarFadingEnabled(true);
        mWebView.setBackgroundColor(0);
        mProgressBar = (ProgressBar) findViewById(R.id.loading_progress_bar);
        mBtnConfirm = findViewById(R.id.btn_confirm);

        if(showConfirmButton) {
            mBtnConfirm.setVisibility(View.VISIBLE);
            mBtnConfirm.setText(confirmTxt);
            mBtnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setResult(RESULT_OK);
                    finish();
                }
            });
        }

        if(url == null) {
            finish();
            return;
        }

        MyLog.d("getIntent().getDataString()=" + getIntent().getDataString() + ", url=" + url);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setSupportZoom(true);

        // 이미지나 페이지를 화면에 딱 맞게 표시되게게하는 옵션
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);

        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setWebViewClient(buildWebViewClient());

        mWebView.setWebChromeClient(buildWebChromeClient());
        mWebView.loadUrl(url);
    }

    protected WebViewClient buildWebViewClient() {
        return new CustomWebViewClient();
    }

    protected WebChromeClient buildWebChromeClient() {
        return new CustomWebChromeClient();
    }

    protected class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            MyLog.d("url=" + url);

            // 허용되지 않는 도메인 url 은 무시한다.
            if(mAllowedDomainUrls != null && !isAllowedDomains(url)) {
                MyLog.i("url is not allowed");
                return true;
            }

            String scheme = null;
            int schemeLastIndex = url.indexOf("://");

            MyLog.d("schemeLastIndex=" + schemeLastIndex);
            if(schemeLastIndex > 0) {
                scheme = url.substring(0, schemeLastIndex);
            }

            MyLog.d("scheme=" + scheme);

            // 조건 1. scheme 문자열이 http, https 가 아닌 다른 문자열 일때
            // 조건 2. url 이  play.google.com 을 포함하는 문자열일때
            if( (scheme != null && !(scheme.equals("http") || scheme.equals("https")))
                    || url.startsWith("http://play.google.com") || url.startsWith("https://play.google.com") || url.startsWith("play.google.com") ) {
                try {
                    MyPackageManager.executeBrowser(BaseWebActivity.this, url);
                } catch (ActivityNotFoundException e) {
                    // 에러처리 > 어떤 이유에서건 외부 브라우저로 띄우는 도중 에러가 나면 그냥 현재 웹뷰에서 url 이동
                    view.loadUrl(url);
                }

            } else if (url.endsWith("png") || url.endsWith("jpeg") || url.endsWith("jpg")) {
                // url 이 이미지일 경우 html 에 css 까지 입혀서 화면에 꽉 맞도록 로드되도록 한다.
                String htmlData = "<html><head></head><body><img src=\"" + url + "\"></body></html>";
                htmlData = "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />" + htmlData;
                view.loadDataWithBaseURL("file:///android_asset/css/", htmlData, "text/html", "UTF-8", null);
            }

            else {
                view.loadUrl(url);
            }

            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mProgressBar.setProgress(0);

//                setTitle(mWebView.getTitle());
        }

        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            // 신뢰할 수 없는 인증서라는 메시지를 띄우고 사용자에게 입력을 받아 다음을 진행한다.
            showYesNoDialog(R.string.NOT_SAFE_SSL_SERVER, R.string.CONTINUE, android.R.string.cancel, new DialogButtonClickListener(new AbstractDialogButtonClickListener() {
                @Override
                public void onClick(int button) {
                    if (button == BaseCustomDialog.BUTTON_POSITIVE) {
                        handler.proceed();
                    } else {
                        handler.cancel();
                    }
                }
            }));
        }
    }

    protected class CustomWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            mProgressBar.setProgress(newProgress);
            if(newProgress >= 100) {
                mProgressBar.setProgress(0);
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            MyLog.d("title=" + title);

            // 액티비티 시작시 파라미터로 넘겨받은 타이틀이 없으면 웹뷰의 타이틀 셋팅
            if (StringUtils.isEmpty(mTitle) && !StringUtil.isEmptyString(title)) {
                setTitle(title);
            }
        }
    }

    private boolean isAllowedDomains(String url) {
        return mAllowedDomainUrls != null && Arrays.binarySearch(mAllowedDomainUrls, url) >= 0;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        MyLog.i("mWebView.canGoBack() = " + mWebView.canGoBack());
        if(keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack() ) {
            mWebView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setScreenOrientation(boolean allowLandscape) {
        if(allowLandscape)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}