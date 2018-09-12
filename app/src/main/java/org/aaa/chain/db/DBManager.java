package org.aaa.chain.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import org.aaa.chain.ChainApplication;
import org.aaa.chain.entities.DBColumn;
import org.aaa.chain.entities.ResumeRequestEntity;
import org.aaa.chain.entities.ResumeResponseEntity;
import org.json.JSONException;
import org.json.JSONObject;

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
     * save resume upload return response
     */
    synchronized public void saveResumeResponse(ResumeResponseEntity entity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(DBColumn.Response._ID, entity.get_id());
            values.put(DBColumn.Response.HASH_ID, entity.getHashId());
            values.put(DBColumn.Response.ACCOUNT, entity.getAccount());
            values.put(DBColumn.Response.SIZE, entity.getSize());
            values.put(DBColumn.Response.PUBLIC_KEY, entity.getPublicKey());
            values.put(DBColumn.Response.TIMESTAMP, entity.getTimestamp());
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name:", entity.getExtra().getName());
                jsonObject.put("desc:", entity.getExtra().getDesc());
                jsonObject.put("workours:", entity.getExtra().getWorkours());
                jsonObject.put("company:", entity.getExtra().getCompany());
                jsonObject.put("birthday:", entity.getExtra().getBirthday());
                jsonObject.put("latestWorkHours:", entity.getExtra().getLatestWorkHours());
                jsonObject.put("latestCompany:", entity.getExtra().getLatestCompany());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            values.put(DBColumn.Response.EXTRA, jsonObject.toString());
            values.put(DBColumn.Response.__V, entity.get__v());

            db.insert(DBColumn.Response.RESPONSE_TABLE_NAME, null, values);
        }
    }

    /**
     * save resume upload request info
     */
    synchronized public void saveResumeRequest(ResumeRequestEntity entity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(DBColumn.Request.ACCOUNT, entity.getAccount());
            values.put(DBColumn.Request.FILE_NAME, entity.getFilename());
            values.put(DBColumn.Request.FILE_PATH, entity.getFilepath());
            values.put(DBColumn.Request.METADATA, entity.getMetadata());
            values.put(DBColumn.Request.OPTIONS, entity.getOptions());
            values.put(DBColumn.Request.SIGNATURE, entity.getSignature());
            values.put(DBColumn.Request._ID, entity.get_id());

            db.insert(DBColumn.Request.REQUEST_TABLE_NAME, null, values);
        }
    }

    /**
     * get resume upload request info
     */
    synchronized public ResumeRequestEntity getResumeRequest() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ResumeRequestEntity requestEntity = new ResumeRequestEntity();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + DBColumn.Request.REQUEST_TABLE_NAME /* + " desc" */, null);
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(DBColumn.Request._ID));
                String account = cursor.getString(cursor.getColumnIndex(DBColumn.Request.ACCOUNT));
                String filename = cursor.getString(cursor.getColumnIndex(DBColumn.Request.FILE_NAME));
                String filepath = cursor.getString(cursor.getColumnIndex(DBColumn.Request.FILE_PATH));
                String metadata = cursor.getString(cursor.getColumnIndex(DBColumn.Request.METADATA));
                String signature = cursor.getString(cursor.getColumnIndex(DBColumn.Request.SIGNATURE));
                String options = cursor.getString(cursor.getColumnIndex(DBColumn.Request.OPTIONS));
                requestEntity.set_id(id);
                requestEntity.setAccount(account);
                requestEntity.setFilename(filename);
                requestEntity.setFilepath(filepath);
                requestEntity.setMetadata(metadata);
                requestEntity.setOptions(options);
                requestEntity.setSignature(signature);
            }
            cursor.close();
        }
        return requestEntity;
    }

    public void updateResumeResponse(ResumeResponseEntity resumeResponseEntity) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(DBColumn.Response._ID, resumeResponseEntity.get_id());
            values.put(DBColumn.Response.HASH_ID, resumeResponseEntity.getHashId());
            values.put(DBColumn.Response.ACCOUNT, resumeResponseEntity.getAccount());
            values.put(DBColumn.Response.SIZE, resumeResponseEntity.getSize());
            values.put(DBColumn.Response.PUBLIC_KEY, resumeResponseEntity.getPublicKey());
            values.put(DBColumn.Response.TIMESTAMP, resumeResponseEntity.getTimestamp());
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name:", resumeResponseEntity.getExtra().getName());
                jsonObject.put("desc:", resumeResponseEntity.getExtra().getDesc());
                jsonObject.put("workours:", resumeResponseEntity.getExtra().getWorkours());
                jsonObject.put("company:", resumeResponseEntity.getExtra().getCompany());
                jsonObject.put("birthday:", resumeResponseEntity.getExtra().getBirthday());
                jsonObject.put("latestWorkHours:", resumeResponseEntity.getExtra().getLatestWorkHours());
                jsonObject.put("latestCompany:", resumeResponseEntity.getExtra().getLatestCompany());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            values.put(DBColumn.Response.EXTRA, jsonObject.toString());
            values.put(DBColumn.Response.__V, resumeResponseEntity.get__v());

            db.update(DBColumn.Response.RESPONSE_TABLE_NAME, values, DBColumn.Response._ID + " = ?", new String[] { resumeResponseEntity.get_id() });
        }
    }

    /**
     * get resume upload return response
     */
    synchronized public List<ResumeResponseEntity> getResumeResponse() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<ResumeResponseEntity> entityList = new ArrayList<>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + DBColumn.Response.RESPONSE_TABLE_NAME /* + " desc" */, null);
            while (cursor.moveToNext()) {
                ResumeResponseEntity entity = new ResumeResponseEntity();
                String id = cursor.getString(cursor.getColumnIndex(DBColumn.Response._ID));
                String account = cursor.getString(cursor.getColumnIndex(DBColumn.Response.ACCOUNT));
                String publickey = cursor.getString(cursor.getColumnIndex(DBColumn.Response.PUBLIC_KEY));
                int v = cursor.getInt(cursor.getColumnIndex(DBColumn.Response.__V));
                String timestamp = cursor.getString(cursor.getColumnIndex(DBColumn.Response.TIMESTAMP));
                long size = cursor.getLong(cursor.getColumnIndex(DBColumn.Response.SIZE));
                String extra = cursor.getString(cursor.getColumnIndex(DBColumn.Response.EXTRA));
                String hashid = cursor.getString(cursor.getColumnIndex(DBColumn.Response.HASH_ID));
                entity.set__v(v);
                entity.set_id(id);
                entity.setAccount(account);
                entity.setTimestamp(timestamp);
                entity.setSize(size);
                entity.setHashId(hashid);
                entity.setExtra(new Gson().fromJson(extra, ResumeResponseEntity.ExtraEntity.class));
                entity.setPublicKey(publickey);
                entityList.add(entity);
            }
            cursor.close();
        }
        return entityList;
    }
}
