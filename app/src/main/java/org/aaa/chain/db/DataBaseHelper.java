package org.aaa.chain.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.aaa.chain.entities.DBColumn;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static DataBaseHelper instance;
    private static final String RESUME_RESPONSE_TABLE_CREATE = "CREATE TABLE "

            + DBColumn.Response.RESPONSE_TABLE_NAME
            + " ("
            + DBColumn.Response.HASH_ID
            + " TEXT, "
            + DBColumn.Response.ACCOUNT
            + " TEXT, "
            + DBColumn.Response.EXTRA
            + " TEXT, "
            + DBColumn.Response.__V
            + " Integer, "
            + DBColumn.Response.SIZE
            + " Long, "
            + DBColumn.Response.PUBLIC_KEY
            + " TEXT, "
            + DBColumn.Response.TIMESTAMP
            + " TEXT, "
            + DBColumn.Response._ID
            + " TEXT PRIMARY KEY);";
    private static final String RESUME_REQUEST_TABLE_CREATE = "CREATE TABLE "

            + DBColumn.Request.REQUEST_TABLE_NAME
            + " ("
            + DBColumn.Request.ACCOUNT
            + " TEXT, "
            + DBColumn.Request.FILE_NAME
            + " TEXT, "
            + DBColumn.Request.FILE_PATH
            + " TEXT, "
            + DBColumn.Request.SIGNATURE
            + " TEXT, "
            + DBColumn.Request.METADATA
            + " TEXT, "
            + DBColumn.Request.OPTIONS
            + " TEXT, "
            + DBColumn.Request._ID
            + " TEXT PRIMARY KEY);";

    public static DataBaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DataBaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DataBaseHelper(Context context) {
        super(context, "resume.db", null, DATABASE_VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL(RESUME_RESPONSE_TABLE_CREATE);
        db.execSQL(RESUME_REQUEST_TABLE_CREATE);
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
