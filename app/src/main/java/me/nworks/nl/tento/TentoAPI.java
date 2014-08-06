package me.nworks.nl.tento;

import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

public class TentoAPI {
    public interface TentoCallback<T> {
        public void success(T result);
        public void error(String message);
    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();
    private String host = "10.0.3.2"; // localhost 는 에뮬레이터 자체를 가르키므로
    private int port = 5000;
    private String getUrl(String url) {
        return "http://" + host + ":" + String.valueOf(port) + url;
    }

    private void makeRequest(Request request, final TentoCallback<String> callback) {
        new AsyncTask<Request, String, String>() {
            @Override
            protected String doInBackground(Request... requests) {
                Response response = null;
                try {
                    response = client.newCall(requests[0]).execute();
                    callback.success("");
                    ResponseBody body = response.body();
                    return "";
                } catch (IOException e) {
                    callback.error(e.getMessage());
                    return null;
                }
            }
        }.execute(request);
    }

    public void postJSON(String url, String json, TentoCallback<String> callback) {
        RequestBody body = RequestBody.create(JSON, json);
        String u = getUrl(url);
        Log.d("hello", u);
        Request request = new Request.Builder()
                .url(u)
                .post(body)
                .build();
        makeRequest(request, callback);
    }
}
