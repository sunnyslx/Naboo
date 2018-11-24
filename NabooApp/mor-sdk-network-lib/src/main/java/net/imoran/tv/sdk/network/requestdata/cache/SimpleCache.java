package net.imoran.tv.sdk.network.requestdata.cache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.imoran.tv.sdk.network.utils.LogUtils;

/**
 * Created by bobge on 2017/7/12.
 */

public class SimpleCache {
    private static final String TAG = SimpleCache.class.getSimpleName();
    private static CacheDBHelper cacheDBHelper;

    private SimpleCache(Context context) {
        cacheDBHelper = new CacheDBHelper(context);
    }

    private static SimpleCache cache;

    public static SimpleCache getCache(Context context) {
        if (cache == null) {
            synchronized (SimpleCache.class) {
                if (cache == null) {
                    cache = new SimpleCache(context.getApplicationContext());
                }
            }
        }
        return cache;
    }

    public static void put(String key, String value) {

        SQLiteDatabase database = null;
        try {
            database = cacheDBHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(CacheDBHelper.Cache.COLUMN_KEY, key);
            values.put(CacheDBHelper.Cache.COLUMN_DATA, value);
            values.put(CacheDBHelper.Cache.COLUMN_LOG, "");
            values.put(CacheDBHelper.Cache.COLUMN_TIME, System.currentTimeMillis());
            database.replaceOrThrow(CacheDBHelper.Cache.TABLE_NAME, null, values);
        } catch (Exception e) {
            LogUtils.e(TAG, "putCache " + key + " error " + e.toString());
        } finally {
            if (database != null)
                database.close();
        }
    }

    public static String get(String key) {
        SQLiteDatabase database = null;
        Cursor c = null;
        String data = "";
        try {
            database = cacheDBHelper.getReadableDatabase();
            c = database.query(CacheDBHelper.Cache.TABLE_NAME, new String[]{CacheDBHelper.Cache.COLUMN_DATA}, CacheDBHelper.Cache.COLUMN_KEY + "=?", new String[]{key}, null, null, null);
            c.moveToFirst();
            data = c.getString(c.getColumnIndex(CacheDBHelper.Cache.COLUMN_DATA));

        } catch (Exception e) {
            LogUtils.e(TAG, "getCache " + key + " error " + e.toString());
        } finally {
            if (c != null) {
                c.close();
            }
            if (database != null) {
                database.close();
            }

        }

        return data;
    }

    public static void remove(String key) {

        SQLiteDatabase database = null;
        try {
            database = cacheDBHelper.getWritableDatabase();
            database.delete(CacheDBHelper.Cache.TABLE_NAME, CacheDBHelper.Cache.COLUMN_KEY + "=?", new String[]{key});
        } catch (Exception e) {
            LogUtils.e(TAG, "removeCache " + key + "  error " + e.toString());
        } finally {
            if (database != null) {
                database.close();
            }

        }
    }

    public static void clear() {

        SQLiteDatabase database = null;
        try {
            database = cacheDBHelper.getWritableDatabase();
            database.delete(CacheDBHelper.Cache.TABLE_NAME, null, null);
        } catch (Exception e) {
            LogUtils.e(TAG, "clearCache error " + e.toString());
        } finally {
            if (database != null) {
                database.close();
            }

        }
    }
}
