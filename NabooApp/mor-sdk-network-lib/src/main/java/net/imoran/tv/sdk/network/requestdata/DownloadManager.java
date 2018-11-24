package net.imoran.tv.sdk.network.requestdata;

import android.os.Handler;
import android.os.Looper;

import net.imoran.tv.sdk.network.callback.IResultCallBack;
import net.imoran.tv.sdk.network.utils.CommonUtils;
import net.imoran.tv.sdk.network.utils.LogUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by bobge on 2017/8/5.
 */

public class DownloadManager {
    public DownloadManager() {
        mDelivery = new Handler(Looper.getMainLooper());
    }

    private Handler mDelivery;     //主线程返回

    private Call downCall;         //下载的call

    private static OkHttpClient mOkHttpClient;

    private static DownloadManager mInstance;    //单例

    private File destination;

    public static DownloadManager getInstance() {
        if (mInstance == null) {
            synchronized (DownloadManager.class) {
                if (mInstance == null) {
                    mInstance = new DownloadManager();
                }
            }
        }
        return mInstance;
    }

    //synchronized修饰的静态方法锁定的是这个类的所有对象,保证在同一时刻最多只有一个线程执行该段代码
    public synchronized OkHttpClient getOkHttpClient() {
        if (null == mOkHttpClient) {
            try {
                mOkHttpClient = newOkHttpClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mOkHttpClient;
    }

    /**
     * 创建okhttp
     *
     * @return
     * @throws Exception
     */
    private OkHttpClient newOkHttpClient() throws Exception {

        //创建okHttpClient对象
        OkHttpClient mOkHttpClient;
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
        mOkHttpClient = new OkHttpClient.Builder()
                .sslSocketFactory(sc.getSocketFactory())
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .build();

        return mOkHttpClient;
    }

    /**
     * 异步下载文件
     *
     * @param url         文件的下载地址
     * @param destFileDir 本地文件存储的文件夹
     * @param callback
     */
    private void okHttpDownload(final String url, final String destFileDir, final IResultCallBack callback) {
        try {
            long downloadLength = 0;
            File file = new File(destFileDir, CommonUtils.getFileName(url));
            if (file.exists()) {
                //如果文件存在的话，得到文件的大小
                downloadLength = file.length();
            }
            long contentLenght = getContentLength(url);
            if (contentLenght == 0) {
                sendFailedStringCallback(null, "网络异常", callback);
            } else if (downloadLength >= contentLenght) {
                //之前下载过,需要重新来一个文件
                downloadLength = 0;
                file.delete();
            }
            Request request = new Request.Builder()
                    .url(url)
                    .header("RANGE", "bytes=" + downloadLength + "-")//断点续传要用到的，指示下载的区间
                    .build();
            Response response = getOkHttpClient().newCall(request).execute();
            if (!response.isSuccessful()) {
                sendFailedStringCallback(response.request(), "网络下载失败", callback);
            }
            InputStream is = null;
            byte[] buf = new byte[1024];
            int len = 0;
            FileChannel channelOut = null;
            RandomAccessFile savedFile = null;
            try {
                is = response.body().byteStream();
                savedFile = new RandomAccessFile(file, "rw");
                savedFile.seek(downloadLength);//跳过已经下载的字节
                LogUtils.i("bobge", "downloadLength:" + downloadLength);
                while ((len = is.read(buf)) != -1) {
                    downloadLength += len;
                    savedFile.write(buf, 0, len);
                    sendProgressCallBack(contentLenght, downloadLength, callback);
                }
                //如果下载文件成功，第一个参数为文件的绝对路径
                sendSuccessResultCallback(file.getAbsolutePath(), callback);
            } catch (IOException e) {
                sendFailedStringCallback(response.request(), e.getMessage(), callback);
            } finally {
                try {
                    if (is != null) is.close();
                    if (savedFile != null) savedFile.close();
                    if (channelOut != null) channelOut.close();
                } catch (IOException e) {
                    LogUtils.e("downLoadFailed", e.toString());
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            LogUtils.e("HttpUtil", e.toString());
        }
    }

    //下载失败ui线程回调
    private void sendFailedStringCallback(final Request request, final String errorMsg, final IResultCallBack callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null)
                    callback.onError(request, errorMsg);
            }
        });
    }

    //下载成功ui线程回调
    private void sendSuccessResultCallback(final Object object, final IResultCallBack callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onResponse(object);
                }
            }
        });
    }


    /**
     * 进度信息ui线程回调
     *
     * @param total    总计大小
     * @param current  当前进度
     * @param callBack
     * @param <T>
     */
    private <T> void sendProgressCallBack(final float total, final float current, final IResultCallBack<T> callBack) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onProgress(total, current);
                }
            }
        });
    }


    /*  下面为对外开放接口 */


    /**
     * 下载文件
     *
     * @param url      文件链接
     * @param destDir  下载保存地址
     * @param callback 回调
     */
    public static void downloadFile(String url, String destDir, IResultCallBack callback) {
        getInstance().okHttpDownload(url, destDir, callback);
    }


    /**
     * 取消下载
     */
    public static void cancleDown() {
        getInstance().downCall.cancel();
    }


    /**
     * 得到下载内容的大小
     *
     * @param downloadUrl
     * @return
     */
    private long getContentLength(String downloadUrl) {
        Request request = new Request.Builder().url(downloadUrl).build();
        try {
            Response response = getOkHttpClient().newCall(request).execute();
            if (response != null && response.isSuccessful()) {
                long contentLength = response.body().contentLength();
                response.body().close();
                return contentLength;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
