package net.imoran.tv.sdk.network.requestdata;

import android.content.Context;


import net.imoran.tv.sdk.network.utils.LogUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by bobge on 2017/7/12.
 * Http请求工具类
 */
public class HttpUtil {

    public static OkHttpClient client;
    private final static String SIMPLE_DATE_PATTERN = "yyyyMMddhhmmss";
    private final static DateFormat simpleDateFormat = new SimpleDateFormat(SIMPLE_DATE_PATTERN, Locale.CHINESE);
    private static String DEVICE_INFO = "";

    public static void setDeviceInfo(String deviceInfo) {
        DEVICE_INFO = deviceInfo;
    }

    /**
     * get方式
     *
     * @param url     url
     * @param context context
     * @param track   @return 为以后埋点做预留
     */
    public static String httpGet(String url, Context context, String track) {
        Request request = new Request.Builder().url(url).build();
        Response response;
        try {
            response = getCustomClient().newCall(request).execute();

            String body = response.body().string();
            if (response.isSuccessful()) {
                return body;
            }
        } catch (IOException e) {
            LogUtils.e("HttpUtil", e.toString());
        }
        return "";
    }

    /**
     * get方式
     *
     * @param url   url
     * @param track track
     * @return String
     */
    public static String httpGet(String url, String lastModified, Context context, String track) {
        Request request = new Request.Builder()
               /* .addHeader("User-Agent", DEFAULT_USERAGENT)
                .addHeader("Cache-Control", "no-cache")
                .addHeader("If-Modified-Since", lastModified)
                .addHeader("DEVICE_INFO", DEVICE_INFO + "&" + "nowTime=" + simpleDateFormat.format(new Date()))
            */
                .url(url)
                .build();

        Response response;
        try {
            response = getCustomClient().newCall(request).execute();

            String body = response.body().string();
            LogUtils.i("HttpUtil", body);
            if (response.isSuccessful()) {
                // LastModified.saveLastModified(context, MD5Util.md5(url), response.header("Last-Modified"));
                return body;
            }

        } catch (IOException e) {
            LogUtils.e("HttpUtil", e.toString());
        }
        return "";


    }

    public static String httpGet(String url, Context context, String track, HashMap<String, String> headers) {
        Request request;
        Request.Builder builder = new Request.Builder();
     /*   builder.addHeader("User-Agent", DEFAULT_USERAGENT)
                .addHeader("Cache-Control", "no-cache")
                .addHeader("DEVICE_INFO", DEVICE_INFO + "&" + "nowTime=" + simpleDateFormat.format(new Date()));
       */
        if (headers.size() > 0) {
            Iterator iter = headers.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                builder.addHeader(key, value);
            }
        }

        request = builder.url(url).build();

        Response response;
        try {
            response = getCustomClient().newCall(request).execute();

            String body = response.body().string();
            if (response.isSuccessful()) {
                return body;
            }
        } catch (IOException e) {
            LogUtils.e("HttpUtil", e.toString());
        }
        return "";


    }

    /**
     * post方式
     *
     * @param url            url
     * @param postParameters postParameters
     * @param context        context
     * @param track          @return
     */
    public static String httpPost(String url,
                                  HashMap<String, String> postParameters, Context context, String track) {
        try {
            FormBody.Builder builder = new FormBody.Builder();
            if (postParameters == null) {
                postParameters = new HashMap<>();
            }
            for (Map.Entry<String, String> entry : postParameters.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                builder.add(key, value);
            }
            FormBody formBody = builder.build();
            Request request = new Request.Builder()
                    .addHeader("Cache-Control", "no-cache")
                    .addHeader("DEVICE_INFO", DEVICE_INFO + "&" + "nowTime=" + simpleDateFormat.format(new Date()))
                    .url(url)
                    .post(formBody)
                    .build();

            Response response = getCustomClient().newCall(request).execute();
            String body = response.body().string();
            if (response.isSuccessful()) {
                return body;
            }
        } catch (Exception e) {
            LogUtils.e("HttpUtil", e.toString());
        }
        return "";

    }

    /**
     * put方式
     *
     * @param url            url
     * @param postParameters postParameters
     * @param context        context
     * @param track          @return
     */
    public static String httpPut(String url,
                                 HashMap<String, String> postParameters, Context context, String track) {
        try {
            FormBody.Builder builder = new FormBody.Builder();
            if (postParameters == null) {
                postParameters = new HashMap<>();
            }
            for (Map.Entry<String, String> entry : postParameters.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                builder.add(key, value);
            }
            FormBody formBody = builder.build();
            Request request = new Request.Builder()
                    .addHeader("Cache-Control", "no-cache")
                    .addHeader("DEVICE_INFO", DEVICE_INFO + "&" + "nowTime=" + simpleDateFormat.format(new Date()))
                    .url(url)
                    .put(formBody)
                    .build();

            Response response = getCustomClient().newCall(request).execute();

            String body = response.body().string();
            if (response.isSuccessful()) {
                return body;
            }
        } catch (Exception e) {
            LogUtils.e("HttpUtil", e.toString());
        }
        return "";

    }

    /**
     * delete方式
     *
     * @param url     url
     * @param context context
     * @param track   @return
     */
    public static String httpDelete(String url, HashMap<String, String> postParameters, Context context, String track) {
        try {
            FormBody.Builder builder = new FormBody.Builder();
            if (postParameters == null) {
                postParameters = new HashMap<>();
            }
            for (Map.Entry<String, String> entry : postParameters.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                builder.add(key, value);
            }
            FormBody formBody = builder.build();
            Request request = new Request.Builder()
                    .addHeader("Cache-Control", "no-cache")
                    .addHeader("DEVICE_INFO", DEVICE_INFO + "&" + "nowTime=" + simpleDateFormat.format(new Date()))
                    .url(url)
                    .method("DELETE", formBody)
                    .build();
            Response response = getCustomClient().newCall(request).execute();

            String body = response.body().string();
            if (response.isSuccessful()) {
                return body;
            }
        } catch (Exception e) {
            LogUtils.e("HttpUtil", e.toString());
        }
        return "";

    }

    /**
     * @param url  下载地址
     * @param file 保存的文件名
     * @return
     */
    public static boolean httpSyncDownLoadFile(String url, String file) {
        LogUtils.i("HttpUtil", "getFile url to path\n" + " ::: " + file);
        try {
            int byteread = 0;
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = getCustomClient().newCall(request).execute();
            if (!response.isSuccessful()) {
                return false;
            }
            InputStream inStream = response.body().byteStream();
            FileOutputStream fs = new FileOutputStream(file);

            byte[] buffer = new byte[1204];
            while ((byteread = inStream.read(buffer)) != -1) {
                fs.write(buffer, 0, byteread);
            }
            return true;
        } catch (Exception e) {
            LogUtils.e("HttpUtil", e.toString());
        }
        return false;

    }

    private static OkHttpClient getCustomClient() {
        if (client == null) {
            try {
                synchronized (HttpUtil.class) {
                    if (client == null) {
                        X509TrustManager xtm = new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(X509Certificate[] chain, String authType) {
                            }

                            @Override
                            public void checkServerTrusted(X509Certificate[] chain, String authType) {
                            }

                            @Override
                            public X509Certificate[] getAcceptedIssuers() {
                                X509Certificate[] x509Certificates = new X509Certificate[0];
                                return x509Certificates;
                            }
                        };
                        SSLContext sc = SSLContext.getInstance("SSL");
                        sc.init(null, new TrustManager[]{xtm}, new SecureRandom());
                        client = new OkHttpClient.Builder()
                                .readTimeout(20, TimeUnit.SECONDS)
                                .writeTimeout(20, TimeUnit.SECONDS)
                                .connectTimeout(20, TimeUnit.SECONDS)
                                .sslSocketFactory(sc.getSocketFactory())
                                .hostnameVerifier(new HostnameVerifier() {
                                    @Override
                                    public boolean verify(String hostname, SSLSession session) {
                                        return true;
                                    }
                                })
                                .build();
                    }
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }

        }
        return client;
    }

    public static Response httpGet(String url, HashMap<String, String> headers) {
        Request request;
        Request.Builder builder = new Request.Builder();
        builder.addHeader("Cache-Control", "no-cache").addHeader("DEVICE_INFO", DEVICE_INFO + "&" + "nowTime=" + simpleDateFormat.format(new Date()));
        if (headers != null && headers.size() > 0) {
            Iterator iter = headers.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                builder.addHeader(key, value);
            }
        }
        request = builder.url(url).build();
        Response response;
        try {
            OkHttpClient okHttpClient = getCustomClient();
            response = okHttpClient.newCall(request).execute();
            return response;
        } catch (IOException e) {
            LogUtils.e("HttpUtil", e.toString());
        }
        return null;
    }
}
