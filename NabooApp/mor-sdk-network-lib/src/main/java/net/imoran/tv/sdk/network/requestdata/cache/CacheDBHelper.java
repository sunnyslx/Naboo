package net.imoran.tv.sdk.network.requestdata.cache;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by gejiangbo
 * on 2016/5/12.
 */
public class CacheDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "flesportcache";
    private static final int DATABASE_VERSION = 1;

    public CacheDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public interface Cache extends BaseColumns {
        String TABLE_NAME = "fcache";
        String COLUMN_KEY = "key";
        String COLUMN_DATA = "data";
        String COLUMN_LOG = "log";
        String COLUMN_TIME = "time";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Cache.TABLE_NAME
                + "("
                + Cache.COLUMN_KEY + " TEXT primary key,"
                + Cache.COLUMN_DATA + " TEXT,"
                + Cache.COLUMN_LOG + " TEXT,"
                + Cache.COLUMN_TIME + " INTEGER"
                + ");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }

}
