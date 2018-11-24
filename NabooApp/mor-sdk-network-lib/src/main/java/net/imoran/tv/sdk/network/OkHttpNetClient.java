package net.imoran.tv.sdk.network;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jia on 2017/5/8.
 */

public class OkHttpNetClient implements NetClient {
    private static final String TAG = "OkHttpNetClient";

    public static final MediaType JSON
            = MediaType.parse("application/gson; charset=utf-8");

    private Context context;
    private OkHttpClient okHttpClient;
    private Gson gson;

    private static final int TIMEOUT_CONNECT = 10;
    private static final int TIMEOUT_READ = 10;

    private ExecutorService executorService;

    public OkHttpNetClient(Context context) {
        this.context = context;
        this.okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_CONNECT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
        executorService = Executors.newFixedThreadPool(3);
    }

    // TODO: 2017/6/22 添加实现
//    @Override
    public void request(int type, String url, Map<String, String> params, RequestCallBack requestCallBack) {

    }

    @Override
    public void requestString(final int type, final String url, final Map<String, String> params, final RequestCallBack requestCallBack) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "requestString run");
                String result = request(type, url, params);
                Log.d(TAG, "requestString run result: " + result);
                if (requestCallBack != null) {
                    requestCallBack.onResult(result);
                } else {
                    Log.w(TAG, "requestString run: requestCallBack is null");
                }
            }
        });
    }

    @Override
    public String request(int type, String url, Map<String, String> params) {
        Request.Builder requestBuilder = new Request.Builder()
                .url(url);
        Log.d(TAG, "request, url: " + url);

//        String paramStr = gson.toJson(params);
        switch (type) {
            case TYPE_GET: {
                break;
            }
            case TYPE_POST: {
                FormBody.Builder builder = new FormBody.Builder();

                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();

                    Log.d(TAG, "request: post params, key: " + key + " value: " + value);
                    builder.add(key, value);
                }
                FormBody formBody = builder.build();
                requestBuilder.post(formBody);

                Log.d(TAG, "request post body: " + formBody.toString());
                break;
            }
        }

        try {
            Request request = requestBuilder.build();
            Response response = okHttpClient.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            Log.e(TAG, "request: error: " + e.getMessage());
            return null;
        }
    }
}
