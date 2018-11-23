package org.aaa.chain.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static DataBaseHelper instance;
    private static final String KEY_INFO_TABLE_CREATE = "CREATE TABLE "

            + DBColumn.KeyInfo.KEY_INFO_TABLE_NAME
            + " ("
            + DBColumn.KeyInfo.ACCOUNT
            + " CHAR, "
            + DBColumn.KeyInfo.PASSWORD
            + " VARCHAR, "
            + DBColumn.KeyInfo.PRIVATE_KEY
            + " CHAR UNIQUE, "
            + DBColumn.KeyInfo.PUBLIC_KEY
            + " CHAR, "
            + DBColumn.KeyInfo._ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT);";

    public static DataBaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DataBaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DataBaseHelper(Context context) {
        super(context, "aaaChain.db", null, DATABASE_VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL(KEY_INFO_TABLE_CREATE);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void closeDB() {
        if (instance != null) {
            try {
                SQLiteDatabase db = instance.getWritableDatabase();
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            instance = null;
        }
    }
}
