package com.blackangel.baframework.sns;

import android.net.Uri;
import android.os.Bundle;

import com.blackangel.baframework.core.base.BaseActivity;
import com.blackangel.baframework.logger.MyLog;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by Finger-kjh on 2017-11-28.
 */

public class FacebookUtil {

    public static boolean isLogined() {
        boolean logined = false;

        logined = AccessToken.getCurrentAccessToken() != null && Profile.getCurrentProfile() != null;
        MyLog.d("facebook logined=" + logined);
        return logined;
    }

    public static void loginFacebookViaFacebookButton(final BaseActivity activity, CallbackManager fbCallbackManager,
                                                      LoginButton btnFbLogin, final OnSnsLoginListener onSnsLoginListener) {
        btnFbLogin.setReadPermissions(Arrays.asList("email, public_profile"));
        btnFbLogin.registerCallback(fbCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                MyLog.i();
                activity.showProgress();

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                MyLog.i("facebook login result = " + object.toString());

                                try {
                                    SnsLoginResult result = new SnsLoginResult(
                                            object.getString("id"),
                                            object.getString("email"),
                                            object.getString("name"));

                                    onSnsLoginListener.onLoginCompleted(result);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                activity.hideProgress();
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,email,name,gender");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                MyLog.i();
                onSnsLoginListener.onLoginFailed("facebook login canceled");
            }

            @Override
            public void onError(FacebookException error) {
                MyLog.i();
                error.printStackTrace();
                onSnsLoginListener.onLoginFailed(error.getMessage());
            }
        });
    }

    public static void shareToFacebook(CallbackManager callbackManager, BaseActivity activity, String imageUrl, String message,
                                       final OnSnsShareListener onSnsShareListener) {
        ShareDialog shareDialog = new ShareDialog(activity);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {

            @Override
            public void onSuccess(Sharer.Result result) {
                MyLog.e("shareDialog", "onSuccess() - postId=" + result.getPostId());

                onSnsShareListener.onShareCompleted();
            }

            @Override
            public void onError(FacebookException error) {
                MyLog.e("shareDialog", "onError() - " + error.getMessage());

                onSnsShareListener.onShareFailed("페이스북으로 초대메시지 보내기 실패했습니다.");
            }

            @Override
            public void onCancel() {
                MyLog.e("shareDialog", "onCancel()");

                onSnsShareListener.onShareFailed("페이스북으로 초대메시지 보내기 실패했습니다.");
            }
        });

        if(ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setImageUrl(Uri.parse(imageUrl))	            // 상품표시 이미지 url
                    .setContentUrl(Uri.parse("market://asfd"))		// 링크 url
                    .setContentTitle("ReLe")			            // 게시물의 제목, caption : 상품이름
                    .setContentDescription(message)	                // 게시물의 내용, description : 상품설명내용
                    .build();

            shareDialog.show(content);
        }
    }

    public static void initialize(BaseActivity activity) {
        FacebookSdk.sdkInitialize(activity.getApplicationContext());
        AppEventsLogger.activateApp(activity);
    }

    public class FacebookLoginParams implements ISnsLoginParam {
        private String id = "id";
        private String name = "name";
        private String first_name = "first_name";
        private String last_name = "last_name";
        private String email = "email";
        private String gender = "gender";
        private String birthday = "birthday";
        private String link = "link";
        private String locale = "locale";
    }
}
