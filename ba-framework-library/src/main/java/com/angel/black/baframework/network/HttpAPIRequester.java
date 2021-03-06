package com.angel.black.baframework.network;

import android.os.AsyncTask;

import com.angel.black.baframework.BaApplication;
import com.angel.black.baframework.R;
import com.angel.black.baframework.core.base.BaseActivity;
import com.angel.black.baframework.core.base.BaseFragment;
import com.angel.black.baframework.core.base.BaseListActivity;
import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.util.NetworkUtil;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Vector;

/**
 * Created by KimJeongHun on 2016-06-06.
 */
public class HttpAPIRequester extends AsyncTask<JSONObject, Void, HttpAPIRequester.APICallResult> {
    private final String NOT_CONNECTED_NETWORK = "NotConnectNetwork";

    // 커스텀 에러코드
    public static final int ERROR_CODE_JSON_PARSING = 80000;
    public static final int ERROR_CODE_NULL_RESULT = 80001;

    public HttpRequestStrategy mHttpRequestStrategy;

    private BaseActivity activity;
    private boolean showCenterLoading;
    private String APIUrl;
    private String method;
    private OnAPIResponseListener onAPIResponseListener;

    public HttpAPIRequester(BaseActivity activity, boolean showCenterLoading, String APIUrl, String method, OnAPIResponseListener onAPIResponseListener) {
        this.activity = activity;
        this.mHttpRequestStrategy = ((BaApplication) activity.getApplication()).getHttpRequestStrategy();
        this.showCenterLoading = showCenterLoading;
        this.APIUrl = APIUrl;
        this.method = method;
        this.onAPIResponseListener = onAPIResponseListener;
    }

    public HttpAPIRequester(BaseFragment fragment, boolean showCenterLoading, String APIUrl, String method, OnAPIResponseListener onAPIResponseListener) {
        this((BaseActivity) fragment.getActivity(), showCenterLoading, APIUrl, method, onAPIResponseListener);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (!NetworkUtil.isOnline(activity)) {
            activity.showOkDialog(R.string.error_not_connected_network);
            this.cancel(true);
        } else {
            if(activity instanceof BaseListActivity && !showCenterLoading) {
                ((BaseListActivity) activity).showLoadingFooter();
            } else if(activity instanceof BaseActivity && showCenterLoading) {
                activity.showProgress();
            }
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        BaLog.w("request " + APIUrl + " is Cancelled");
        activity.showToast("요청 취소됨");
        activity.hideProgress();
    }

    @Override
    public APICallResult doInBackground(JSONObject... params) {
        InputStream inputStream;
        String result;
        int retCode = 0;

        String serverUrl = mHttpRequestStrategy.getServerUrl();
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = null;

            if (method.equalsIgnoreCase("POST")) {
                HttpPost httpPost = new HttpPost(serverUrl + APIUrl);
                setHeader(httpPost);

                String json = params[0].toString();
                StringEntity se = new StringEntity(json, HTTP.UTF_8);
                httpPost.setEntity(se);

                BaLog.w("request " + serverUrl + APIUrl + " POST, jsonParams >> " + json);
                BaLog.w("requestDirectString=" + se.toString());

                httpResponse = httpClient.execute(httpPost);
                BaLog.i("retCode >> " + httpResponse.getStatusLine().getStatusCode());

            } else if (method.equalsIgnoreCase("GET")) {
                HttpGet httpGet = makeHttpGet(serverUrl + APIUrl);
                setHeader(httpGet);

                BaLog.w("request " + serverUrl + APIUrl + " GET");

                httpResponse = httpClient.execute(httpGet);
                BaLog.i("retCode >> " + httpResponse.getStatusLine().getStatusCode());
            }

            if (httpResponse != null) {
                retCode = httpResponse.getStatusLine().getStatusCode();
                inputStream = httpResponse.getEntity().getContent();

                if (inputStream != null) {
                    result = convertStreamToString(inputStream);
                    BaLog.d("API(" + APIUrl + ") result >> " + result);
                } else {
                    result = "Did not work!";
                    BaLog.d("API(" + APIUrl + ") result >> " + result);
                }

                return new APICallResult(retCode, result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            onAPIResponseListener.onErrorResponse(APIUrl, retCode, e.getMessage(), e.getCause());
            return new APICallResult(retCode, e.getClass().getSimpleName());
        }

        return null;
    }

    private void setHeader(HttpRequestBase httpRequestBase) {
//        httpRequestBase.setHeader("Accept", "application/json");
//        BaLog.d("setHeader(Accept, application/json)");
//        httpRequestBase.setHeader("Content-type", "application/json");
//        BaLog.d("setHeader(Content-type, application/json)");
//
//        if (isNeedUserAuthToken()) {
//            httpRequestBase.setHeader("Token", UserHelper.userAccessToken);
//            BaLog.d("setHeader(Token, " + UserHelper.userAccessToken + ")");
//        }

        mHttpRequestStrategy.setHeader(httpRequestBase);
    }

    @Override
    protected void onPostExecute(APICallResult result) {
        if(activity instanceof BaseListActivity && !showCenterLoading) {
            ((BaseListActivity) activity).hideLoadingFooter();
        } else if(activity instanceof BaseActivity && showCenterLoading) {
            activity.hideProgress();
        }

        if (HttpHostConnectException.class.getSimpleName().equals(result.resultString)) {
            if(!activity.isFinishing()) {
                activity.showOkDialog(R.string.error_not_responsed_server);
            }
            return;
        }

        try {
            if(!(result.retCode == HttpURLConnection.HTTP_OK || result.retCode == HttpURLConnection.HTTP_CREATED)) {
                // 200, 201 응답외의 것들은 모두 에러 처리!!
                onAPIResponseListener.onErrorResponse(APIUrl, result.retCode, result.resultString, null);
                return;
            }

            String resultString = result.resultString;
            if (resultString.startsWith("{")) {
                JSONObject jsonResult = new JSONObject(resultString);
                onAPIResponseListener.onResponse(APIUrl, HttpURLConnection.HTTP_OK, jsonResult);
            } else if (resultString.startsWith("[")) {
//                resultString = "{\"json_array\":[{\"comments_count\": 0, \"title\": \"hello\", \"author_id\": 1, \"comments\":["
//                + "{\"list\":[{\"test\": \"a\"}, {\"test\": \"a\"}, {\"test\": \"a\"}]}], \"recruit_status\": \"\", \"author_name\": \"test\", \"content\": \"alskdf\", \"registered_date\": \"2016-06-19T10:31:04.724Z\", \"id\": 1, \"recruit_count\": 6, \"attend_count\": 0}, {\"comments_count\": 0, \"title\": \"hh\", \"author_id\": 1, \"comments\": [], \"recruit_status\": \"\", \"author_name\": \"test\", \"content\": \"ff\", \"registered_date\": \"2016-06-19T11:26:22.978Z\", \"id\": 2, \"recruit_count\": 4, \"attend_count\": 0}, {\"comments_count\": 0, \"title\": \"jj\", \"author_id\": 1, \"comments\": [], \"recruit_status\": \"\", \"author_name\": \"test\", \"content\": \"gg\", \"registered_date\": \"2016-06-19T11:27:00.959Z\", \"id\": 3, \"recruit_count\": 4, \"attend_count\": 0}]}";

                resultString = "{\"json_array\":" + resultString + "}";
                BaLog.d("appended Result=" + resultString);
                JSONObject jsonResult = new JSONObject(resultString);
                JSONArray jsonArrayResult = jsonResult.getJSONArray("json_array");
                onAPIResponseListener.onResponse(APIUrl, HttpURLConnection.HTTP_OK, jsonArrayResult);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            onAPIResponseListener.onErrorResponse(APIUrl, ERROR_CODE_JSON_PARSING, "result is not valid JSON", e.getCause());
        } catch (NullPointerException e) {
            e.printStackTrace();
            onAPIResponseListener.onErrorResponse(APIUrl, ERROR_CODE_NULL_RESULT, "result is null", e.getCause());
        }
    }

    private HttpGet makeHttpGet(String url) {
        Vector<NameValuePair> nameValue = new Vector<NameValuePair>();
//        nameValue.add( new BasicNameValuePair( "user_id", user_id ) ) ;
//        nameValue.add( new BasicNameValuePair( "user_pwd", user_pwd ) ) ;

//        String my_url = url + "?" + URLEncodedUtils.format( nameValue, null) ;
        HttpGet request = new HttpGet(url);
        return request;
    }

    public static String convertStreamToString(InputStream is1) {
        BufferedReader rd = new BufferedReader(new InputStreamReader(is1), 4096);
        String line;
        StringBuilder sb = new StringBuilder();
        try {
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        String contentOfMyInputStream = sb.toString();
        return contentOfMyInputStream;
    }

    public interface OnAPIResponseListener {
        void onResponse(String APIUrl, int retCode, JSONObject response) throws JSONException;

        void onResponse(String APIUrl, int retCode, JSONArray response) throws JSONException;

        void onErrorResponse(String APIUrl, int retCode, String message, Throwable cause);
    }

    class APICallResult {
        int retCode;
        String resultString;

        public APICallResult(int retCode, String resultString) {
            this.retCode = retCode;
            this.resultString = resultString;
        }
    }

    public interface HttpRequestStrategy {
        String getServerUrl();
        void setHeader(HttpRequestBase httpRequestBase);
    }
}
