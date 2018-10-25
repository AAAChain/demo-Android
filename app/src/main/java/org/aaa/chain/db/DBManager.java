package org.aaa.chain.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import org.aaa.chain.ChainApplication;
import org.aaa.chain.entities.KeyInfoEntity;

public class DBManager {
    static private DBManager dbManager = new DBManager();
    private DataBaseHelper dbHelper;

    private DBManager() {
        dbHelper = DataBaseHelper.getInstance(ChainApplication.getInstance().getApplicationContext());
    }

    public static synchronized DBManager getInstance() {
        if (dbManager == null) {
            dbManager = new DBManager();
        }
        return dbManager;
    }

    /**
     * save key info
     */
    synchronized public void saveKeyInfo(KeyInfoEntity entity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(DBColumn.KeyInfo.ACCOUNT, entity.getAccount());
            values.put(DBColumn.KeyInfo.PASSWORD, entity.getPassword());
            values.put(DBColumn.KeyInfo.PRIVATE_KEY, entity.getPrivateKey());
            values.put(DBColumn.KeyInfo.PUBLIC_KEY, entity.getPublicKey());

            db.insert(DBColumn.KeyInfo.KEY_INFO_TABLE_NAME, null, values);
        }
    }

    /**
     * get key info
     */
    synchronized public List<KeyInfoEntity> getKeyInfo() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<KeyInfoEntity> entityList = new ArrayList<>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + DBColumn.KeyInfo.KEY_INFO_TABLE_NAME /* + " desc" */, null);
            while (cursor.moveToNext()) {
                KeyInfoEntity entity = new KeyInfoEntity();
                String account = cursor.getString(cursor.getColumnIndex(DBColumn.KeyInfo.ACCOUNT));
                String password = cursor.getString(cursor.getColumnIndex(DBColumn.KeyInfo.PASSWORD));
                String publickey = cursor.getString(cursor.getColumnIndex(DBColumn.KeyInfo.PUBLIC_KEY));
                String privatekey = cursor.getString(cursor.getColumnIndex(DBColumn.KeyInfo.PRIVATE_KEY));

                entity.setAccount(account);
                entity.setPassword(password);
                entity.setPrivateKey(privatekey);
                entity.setPublicKey(publickey);
                entityList.add(entity);
            }
            cursor.close();
        }
        return entityList;
    }
}
