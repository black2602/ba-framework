package com.blackangel.baframework.network.retrofit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.blackangel.baframework.app.constants.ApiInfo;
import com.blackangel.baframework.app.constants.AppErrorCode;
import com.blackangel.baframework.core.model.BaseError;
import com.blackangel.baframework.core.model.ListModel;
import com.blackangel.baframework.logger.MyLog;
import com.blackangel.baframework.network.ApiProgressListener;
import com.blackangel.baframework.network.listener.GlobalErrorHandler;
import com.blackangel.baframework.network.listener.ListModelResultCallback;
import com.blackangel.baframework.network.listener.ModelResultCallback;
import com.blackangel.baframework.util.StringUtil;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
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
    private static BusinessErrorHandler sBusinessErrorHandler;

    public static void initGlobalRetrofit(String baseUrl, OkHttpClient okHttpClient) {
        sGlobalRetrofit = newRetrofit(baseUrl, okHttpClient);
    }

    public static void initGlobalRetrofit(OkHttpClient okHttpClient, BusinessErrorHandler businessErrorHandler) {
        sGlobalRetrofit = newRetrofit(ApiInfo.APP_SERVER_URL, okHttpClient);
        sBusinessErrorHandler = businessErrorHandler;
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

    public static <T> void executeAsync(@Nullable final GlobalErrorHandler globalErrorHandler,
                                        final boolean showProgress,
                                        Call<T> call,
                                        final ModelResultCallback<T> modelResultCallback) {

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
//                MyLog.i("http response code = " + response.code() + ", response=" + response.toString());

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        T t = response.body();
//                        MyLog.i("responseBody=" + t);
                        modelResultCallback.onSuccess(t);

                    } else {
                        // body is null
//                        MyLog.w("body is null");
                    }
                } else {
                    int errCode = response.code();
                    String errMessage;

                    String errJson;
                    try {
                        ResponseBody errorBody = response.errorBody();
                        errJson = StringUtil.getStringFromInputStream(errorBody.byteStream());
                        handleBusinessError(call.request().url().toString(), errCode, errJson,
                                globalErrorHandler, modelResultCallback);

                    } catch (JsonSyntaxException | NullPointerException e) {
                        e.printStackTrace();
                        errMessage = e.getMessage();
                        modelResultCallback.onFail(call.request().url().toString(), errCode, errMessage, new Throwable(errMessage));
                    }
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                t.printStackTrace();
                MyLog.e("errMesage=" + t.getMessage());
                handleApiCallFailture(call.request().url().toString(), t, globalErrorHandler, modelResultCallback);
            }
        });
    }

    public static <T> void executeAsyncWithProgress(@NonNull final ApiProgressListener apiProgressListener,
                                                    final boolean showProgress,
                                                    Call<T> call,
                                                    GlobalErrorHandler globalErrorHandler,
                                                    final ModelResultCallback<T> modelResultCallback) {
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

                        modelResultCallback.onSuccess(t);

                    } else {
                        // body is null
                        MyLog.w("body is null");
                        modelResultCallback.onSuccess(null);
                    }
                } else {
                    int errCode = response.code();
                    String errMessage;

                    String errJson;
                    try {
                        ResponseBody errorBody = response.errorBody();
                        errJson = StringUtil.getStringFromInputStream(errorBody.byteStream());
                        handleBusinessError(call.request().url().toString(), errCode, errJson,
                                globalErrorHandler, modelResultCallback);

                    } catch (JsonSyntaxException | NullPointerException e) {
                        e.printStackTrace();
                        errMessage = e.getMessage();
                        modelResultCallback.onFail(call.request().url().toString(), errCode, errMessage, new Throwable(errMessage));
                    }
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                if(showProgress)
                    apiProgressListener.onFinishApi();

                t.printStackTrace();
                MyLog.e("errMesage=" + t.getMessage());
                handleApiCallFailture(call.request().url().toString(), t, globalErrorHandler, modelResultCallback);
            }
        });
    }

    public static <T, LM extends ListModel<T>> void executeAsyncForList(
            Call<LM> call,
            @Nullable final GlobalErrorHandler globalErrorHandler,
            final ListModelResultCallback<T> apiModelResultListener) {

        call.enqueue(new Callback<LM>() {
            @Override
            public void onResponse(Call<LM> call, Response<LM> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        LM listModel = response.body();
                        apiModelResultListener.onSuccess(listModel);

                    } else {
                        // body is null
                    }

                } else {
                    // 응답코드가 200 대가 아닐때
                    int errCode = response.code();
                    String errMessage;

                    String errBodyString;
                    try {
                        ResponseBody errorBody = response.errorBody();
                        errBodyString = StringUtil.getStringFromInputStream(errorBody.byteStream());

                        handleBusinessError(call.request().url().toString(), errCode, errBodyString,
                                globalErrorHandler, apiModelResultListener);

                    } catch (JsonSyntaxException | NullPointerException e) {
                        e.printStackTrace();
                        errMessage = e.getMessage();
                        apiModelResultListener.onFail(call.request().url().toString(), errCode, errMessage, new Throwable(errMessage));
                    }
                }
            }

            @Override
            public void onFailure(Call<LM> call, Throwable t) {
                t.printStackTrace();
                MyLog.e("errMesage=" + t.getMessage());
                handleApiCallFailture(call.request().url().toString(), t, globalErrorHandler, apiModelResultListener);
            }
        });
    }

    private static void handleApiCallFailture(String requestUrl, Throwable t,
                                              GlobalErrorHandler globalErrorHandler,
                                              ModelResultCallback modelResultCallback) {

        MyLog.i("requestUrl = " + requestUrl + ", exception = " + t.getClass().getSimpleName() + ", apiGlobalErroHandler = " + globalErrorHandler);
        if(t instanceof IOException) {
            // 네트워크 글로벌 에러 처리
            // 디폴트로는 네트워크 에러시 글로벌 에러처리만 호출
            if(globalErrorHandler != null) {
                globalErrorHandler.onGlobalErrorResponse(new BaseError(
                        AppErrorCode.ERROR_CODE_NETWORK_ERROR, t.getMessage(), t));
            }

        } else {
            modelResultCallback.onFail(requestUrl,
                    AppErrorCode.ERROR_CODE_UNKNOWN, t.getMessage(), t);
        }
    }

    private static void handleBusinessError(String requestUrl, int errResponseCode, String errBodyString,
                                            GlobalErrorHandler globalErrorHandler,
                                            ModelResultCallback modelResultCallback) {

        if(sBusinessErrorHandler == null) {
            MyLog.w("sBusinessErrorHandler is null");
            return;
        }

        sBusinessErrorHandler.handleBusinessError(requestUrl, errResponseCode, errBodyString,
                globalErrorHandler, modelResultCallback);
    }

    public static <T> void executeAsyncWithoutUi(
            Call<T> call,
            GlobalErrorHandler globalErrorHandler,
            final ModelResultCallback<T> modelResultCallback) {
        executeAsync(globalErrorHandler, false, call, modelResultCallback);
    }


    /**
     * 각 어플리케이션 별로 서버에서 공통으로 내려주는 비즈니스 에러를 핸들하기 위한 인터페이스
     */
    public interface BusinessErrorHandler {
        void handleBusinessError(String requestUrl, int errCode, String errBodyString,
                                 GlobalErrorHandler globalErrorHandler, ModelResultCallback modelResultCallback);
    }

    public interface ApiModelResultListener<T> {
        void onSuccess(T response);
        void onFail(String url, int errCode, String message, Throwable throwable);
    }

}
