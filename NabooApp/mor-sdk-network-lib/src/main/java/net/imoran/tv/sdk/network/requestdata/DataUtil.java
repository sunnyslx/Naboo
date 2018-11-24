package net.imoran.tv.sdk.network.requestdata;

import android.content.Context;

import net.imoran.tv.sdk.network.requestdata.cache.SimpleCache;
import net.imoran.tv.sdk.network.utils.LogUtils;
import net.imoran.tv.sdk.network.utils.MD5Util;

import org.json.JSONException;

import java.util.HashMap;


/**
 * Created by bobge on 2017/7/12.
 * json处理工具类 仅缓存网络数据
 */
public class DataUtil {
    private static final String TAG = DataUtil.class.getSimpleName();

    public static final String KEY_TRACK = "track";
    /**
     *
     * @param url
     * @param isCache 如果为真会从服务器取数据并且进行本地存储，为假时没有本地存储
     * @param context
     * @return
     *//*
    public static String getJsonFromServer(String url, boolean isCache, Context context) throws JSONException {

		return getJsonFromServer(url, isCache, context, Constants.httpMethod.GET, null);
	}*/

    /**
     * @param url            url
     * @param context        context
     * @param method         请求方式
     * @param postParameters post方式参数
     * @return String
     * @throws JSONException
     */
    public static String getJsonFromServer(String url, Context context, FProtocol.HttpMethod method, HashMap<String, String> postParameters, HashMap<String, String> headers) throws JSONException {
        LogUtils.i(TAG, "getJsonFromServer " + url);
        // 按照约定当state == 200时说明数据正确

        String json = null;
        String track = null;
        if (postParameters != null && postParameters.size() > 0) {
            track = postParameters.get(KEY_TRACK);
            postParameters.remove(KEY_TRACK);
        }
        if (method == FProtocol.HttpMethod.POST) {
            json = HttpUtil.httpPost(url, postParameters, context, track);
        } else if (method == FProtocol.HttpMethod.PUT) {
            json = HttpUtil.httpPut(url, postParameters, context, track);
        } else if (method == FProtocol.HttpMethod.DELETE) {
            json = HttpUtil.httpDelete(url, postParameters, context, track);
        } else {
            if (headers != null && headers.size() > 0) {
                json = HttpUtil.httpGet(url, context, track, headers);
            } else {
                LogUtils.i("http", "HttpUtil.httpGet");
                json = HttpUtil.httpGet(url, context, track);
            }
        }

        return json;
    }

    public static String getJsonFromCache(String url, Context context) {
        LogUtils.i(TAG, "getJsonFromCache" + url);
        return SimpleCache.getCache(context).get(MD5Util.md5(url));
    }

    /**
     * 本地存储json到shareperference
     */
    public static void cacheJson(String url, String json, Context context) {
        LogUtils.i(TAG, "cacheJson" + url);
        SimpleCache.getCache(context).put(MD5Util.md5(url), json);
        LogUtils.i(TAG, "cacheJson end");
    }


}
