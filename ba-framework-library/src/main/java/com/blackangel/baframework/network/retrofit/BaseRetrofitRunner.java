package com.blackangel.baframework.network.retrofit;

import android.support.annotation.NonNull;

import com.blackangel.baframework.logger.MyLog;
import com.blackangel.baframework.network.ApiProgressListener;
import com.blackangel.baframework.network.ErrorCode;
import com.blackangel.baframework.util.StringUtil;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Finger-kjh on 2017-06-14.
 */

public class BaseRetrofitRunner {

    private static Retrofit sGlobalRetrofit;

    public static void initGlobalRetrofit(String baseUrl, OkHttpClient okHttpClient) {
        sGlobalRetrofit = newRetrofit(baseUrl, okHttpClient);
    }

    public static Retrofit getGlobalRetrofit() {
        return sGlobalRetrofit;
    }

    public static Retrofit newRetrofit(String baseUrl, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    public static <T> void executeAsync(@NonNull final ApiProgressListener apiProgressListener, final boolean showProgress, Call<T> call,
                                        final ApiModelResultListener<T> apiModelResultListener) {
        if(showProgress)
            apiProgressListener.onStartApi(null);

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if(showProgress)
                    apiProgressListener.onFinishApi();

                MyLog.i("http response code = " + response.code() + ", response=" + response.toString());

                if(response.isSuccessful()) {
                    if (response.body() != null) {
                        T t = response.body();
                        MyLog.i("responseBody=" + t);
                        apiModelResultListener.onSuccess(t);

                    } else {
                        // body is null
                        MyLog.w("body is null");
                    }
                } else {
                    int errCode = response.code();
                    String errMessage = response.message();

                    if(errMessage.isEmpty()) {
                        String errJson;
                        try {
                            errJson = StringUtil.getStringFromInputStream(response.errorBody().byteStream());

                            if(apiProgressListener.isGlobalError(errCode)) {
                                apiProgressListener.onGlobalErrorResponse(errCode, errMessage, errJson);
                                return;
                            }

                        } catch (JsonSyntaxException | NullPointerException e) {
                            e.printStackTrace();
                            errMessage = e.getMessage();
                        }
                    }

                    apiModelResultListener.onFail(call.request().url().toString(), errCode, errMessage, new Throwable(errMessage));
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                t.printStackTrace();
                MyLog.e("errMesage=" + t.getMessage());

                if(showProgress)
                    apiProgressListener.onFinishApi();

                if(t instanceof IOException) {
                    apiProgressListener.onGlobalErrorResponse(ErrorCode.ERROR_CODE_NETWORK_ERROR, t.getMessage());
                } else {
                    apiModelResultListener.onFail(call.request().url().toString(), ErrorCode.ERROR_CODE_UNKNOWN, t.getMessage(), t);
                }
            }
        });
    }

    public interface ApiModelResultListener<T> {
        void onSuccess(T response);
        void onFail(String url, int errCode, String message, Throwable throwable);
    }

}
