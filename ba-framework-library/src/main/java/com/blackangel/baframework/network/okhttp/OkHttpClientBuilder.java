package com.blackangel.baframework.network.okhttp;

import android.support.annotation.NonNull;

import com.blackangel.baframework.BuildConfig;
import com.blackangel.baframework.app.constants.ApiInfo;
import com.blackangel.baframework.logger.MyLog;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.ConnectionSpec;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by Finger-kjh on 2017-08-21.
 */

public class OkHttpClientBuilder {

    public static class HttpsInfo {
        private TrustManager mTrustManager;
        private SSLContext mSSLContext;

        public HttpsInfo(TrustManager trustManager, SSLContext SSLContext) {
            mTrustManager = trustManager;
            mSSLContext = SSLContext;
        }

        public TrustManager getTrustManager() {
            return mTrustManager;
        }

        public SSLContext getSSLContext() {
            return mSSLContext;
        }
    }

    public static long DEFAULT_TIMEOUT = 30;

    private long timeout;
    private boolean isLogging;
    private Map<String, String> customHeaderMap;
    private HttpsInfo httpsInfo;

    public OkHttpClientBuilder setTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    public OkHttpClientBuilder setLogging(boolean logging) {
        this.isLogging = logging;
        return this;
    }

    public OkHttpClientBuilder setCustomHeaderMap(Map<String, String> customHeaderMap) {
        this.customHeaderMap = customHeaderMap;
        return this;
    }

    public OkHttpClientBuilder setHttpsInfo(HttpsInfo httpsInfo) {
        this.httpsInfo = httpsInfo;
        return this;
    }

    public OkHttpClient build() {
        OkHttpClient client;
        // set logger
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        if(customHeaderMap != null) {
            // 커스텀 헤더 빌드 로직 추가
            Interceptor headerInterceptor = new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request request = buildRequest(original, customHeaderMap);
                    return chain.proceed(request);
                }
            };

            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .addInterceptor(headerInterceptor)
                    .connectTimeout(timeout, TimeUnit.SECONDS)
                    .readTimeout(timeout, TimeUnit.SECONDS)
                    .writeTimeout(timeout, TimeUnit.SECONDS);

            if(ApiInfo.APP_SERVER_URL.startsWith("https")) {
                if(httpsInfo == null)
                    throw new IllegalArgumentException("HttpsInfo must be not null");

                ConnectionSpec.Builder connectionSpecBuilder = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .allEnabledCipherSuites().allEnabledTlsVersions();

                ConnectionSpec spec = connectionSpecBuilder.build();
                builder.connectionSpecs(Collections.singletonList(spec));

                MyLog.i("MyApplication.sX509TrustManager = " + httpsInfo.getTrustManager().getClass().getName());
                builder.sslSocketFactory(httpsInfo.getSSLContext().getSocketFactory(), (X509TrustManager) httpsInfo.getTrustManager());
            }

            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    MyLog.i("hostName = " + hostname);
                    return ApiInfo.APP_SERVER_URL.contains(hostname);
                }
            });

            if(BuildConfig.DEBUG) {
                builder.addInterceptor(loggingInterceptor);
            }

            client = builder.build();

        } else {
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(timeout, TimeUnit.SECONDS)
                    .readTimeout(timeout, TimeUnit.SECONDS)
                    .writeTimeout(timeout, TimeUnit.SECONDS);

            if(ApiInfo.APP_SERVER_URL.startsWith("https")) {
                builder.sslSocketFactory(httpsInfo.getSSLContext().getSocketFactory(), (X509TrustManager) httpsInfo.getTrustManager());
            }

            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    MyLog.i("hostName = " + hostname);
                    return ApiInfo.APP_SERVER_URL.contains(hostname);
                }
            });

            if(BuildConfig.DEBUG) {
                builder.addInterceptor(loggingInterceptor);
            }

            client = builder.build();
        }

        return client;
    }

    private static Request buildRequest(Request original, @NonNull Map<String, String> customHeaderMap) {
        Request.Builder builder = original.newBuilder();

        Set<String> keySet = customHeaderMap.keySet();
        Iterator<String> keyIter = keySet.iterator();

        while(keyIter.hasNext()) {
            String headerKey = keyIter.next();
            String headerValue = customHeaderMap.get(headerKey);

            MyLog.i(headerKey + "=" + headerValue);

            builder.header(headerKey, headerValue);
        }

        return builder.method(original.method(), original.body()).build();
    }

    public interface HttpCustomHeaderAddable {
        Map<String, String> getCustomHeaderMap();
    }
}
