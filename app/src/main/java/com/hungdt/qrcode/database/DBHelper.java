package com.hungdt.qrcode.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.hungdt.qrcode.model.CodeData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static DBHelper instance;

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "QRCode.db";

    public static final String TABLE_DATA = "TB_DATA";
    public static final String COLUMN_CODE_ID = "CODE_ID";
    public static final String COLUMN_CODE_DATA = "CODE_DATA";
    public static final String COLUMN_CODE_TYPE = "CODE_TYPE";
    public static final String COLUMN_CODE_CREATE_TIME = "CREATE_TIME";
    public static final String COLUMN_CODE_CREATE_AT = "CREATE_AT";
    public static final String COLUMN_CODE_SAVE = "CREATE_SAVE";
    public static final String COLUMN_CODE_LIKE = "CODE_LIKE";
    public static final String COLUMN_CODE_NOTE = "CODE_NOTE";

    public static final String SQL_CREATE_TABLE_DATA = "CREATE TABLE " + TABLE_DATA + "("
            + COLUMN_CODE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_CODE_DATA + " TEXT NOT NULL, "
            + COLUMN_CODE_TYPE + " TEXT NOT NULL, "
            + COLUMN_CODE_CREATE_TIME + " TEXT NOT NULL, "
            + COLUMN_CODE_CREATE_AT + " TEXT NOT NULL, "
            + COLUMN_CODE_SAVE + " TEXT NOT NULL, "
            + COLUMN_CODE_LIKE + " TEXT NOT NULL, "
            + COLUMN_CODE_NOTE + " TEXT " + ");";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
    }

    public void addData(String codeData, String codeType, String time, String from, String save, String like, String note) {
        SQLiteDatabase database = instance.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CODE_DATA, codeData);
        values.put(COLUMN_CODE_TYPE, codeType);
        values.put(COLUMN_CODE_CREATE_TIME, time);
        values.put(COLUMN_CODE_CREATE_AT, from);
        values.put(COLUMN_CODE_SAVE, save);
        values.put(COLUMN_CODE_LIKE, like);
        values.put(COLUMN_CODE_NOTE, note);
        database.insert(TABLE_DATA, null, values);
        database.close();
    }

    public void deleteData(String dataID) {
        SQLiteDatabase db = instance.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CODE_ID, dataID);
        db.delete(TABLE_DATA, COLUMN_CODE_ID + "='" + dataID + "'", new String[]{});

        db.close();
    }

    public List<CodeData> getCodeSaved() {
        SQLiteDatabase db = instance.getWritableDatabase();

        Cursor cursor = db.rawQuery(String.format("SELECT * FROM '%s';", TABLE_DATA), null);
        List<CodeData> codeData = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                if (cursor.getString(cursor.getColumnIndex(COLUMN_CODE_SAVE)).equals("Yes")) {
                    codeData.add(new CodeData(cursor.getString(cursor.getColumnIndex(COLUMN_CODE_ID)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_CODE_DATA)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_CODE_TYPE)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_CODE_CREATE_TIME)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_CODE_CREATE_AT)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_CODE_SAVE)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_CODE_LIKE)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_CODE_NOTE))));
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return codeData;
    }

    public List<CodeData> getAllCodeLike() {
        SQLiteDatabase db = instance.getWritableDatabase();

        Cursor cursor = db.rawQuery(String.format("SELECT * FROM '%s';", TABLE_DATA), null);
        List<CodeData> codeData = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                if (cursor.getString(cursor.getColumnIndex(COLUMN_CODE_LIKE)).equals("Love")) {
                    codeData.add(new CodeData(cursor.getString(cursor.getColumnIndex(COLUMN_CODE_ID)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_CODE_DATA)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_CODE_TYPE)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_CODE_CREATE_TIME)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_CODE_CREATE_AT)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_CODE_SAVE)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_CODE_LIKE)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_CODE_NOTE))));
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return codeData;
    }

    public List<CodeData> getCodeUnLike(String like) {
        SQLiteDatabase db = instance.getWritableDatabase();

        Cursor cursor = db.rawQuery(String.format("SELECT * FROM '%s';", TABLE_DATA), null);
        List<CodeData> codeData = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                if (cursor.getString(cursor.getColumnIndex(COLUMN_CODE_LIKE)).equals(like)) {
                    codeData.add(new CodeData(cursor.getString(cursor.getColumnIndex(COLUMN_CODE_ID)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_CODE_DATA)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_CODE_TYPE)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_CODE_CREATE_TIME)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_CODE_CREATE_AT)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_CODE_SAVE)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_CODE_LIKE)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_CODE_NOTE))));
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return codeData;
    }

    public void setLike(String id, String like) {
        SQLiteDatabase db = instance.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CODE_LIKE, like);
        db.update(TABLE_DATA, values, COLUMN_CODE_ID + "='" + id + "'", null);
        db.close();
    }

    public List<CodeData> getAllData() {
        SQLiteDatabase db = instance.getWritableDatabase();

        Cursor cursor = db.rawQuery(String.format("SELECT * FROM '%s';", TABLE_DATA), null);
        List<CodeData> data = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                data.add(new CodeData(cursor.getString(cursor.getColumnIndex(COLUMN_CODE_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CODE_DATA)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CODE_TYPE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CODE_CREATE_TIME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CODE_CREATE_AT)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CODE_SAVE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CODE_LIKE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CODE_NOTE))));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return data;
    }

    public CodeData getOneCodeData(String id) {
        SQLiteDatabase db = instance.getWritableDatabase();

        Cursor cursor = db.rawQuery(String.format("SELECT * FROM '%s';", TABLE_DATA), null);
        CodeData codeData = null;
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                if (cursor.getString(cursor.getColumnIndex(COLUMN_CODE_ID)).equals(id)) {
                    codeData = new CodeData(id,
                            cursor.getString(cursor.getColumnIndex(COLUMN_CODE_DATA)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_CODE_TYPE)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_CODE_CREATE_TIME)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_CODE_CREATE_AT)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_CODE_SAVE)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_CODE_LIKE)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_CODE_NOTE)));
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return codeData;
    }

    public void deleteOneCodeData(String id) {
        SQLiteDatabase db = instance.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CODE_ID, id);
        db.delete(TABLE_DATA, COLUMN_CODE_ID + "='" + id + "'", new String[]{});

        db.close();
    }


    public void deleteCodeData(List<String> ids) {
        SQLiteDatabase db = instance.getWritableDatabase();

        ContentValues values = new ContentValues();
        /*String[] Ids = ......; //Array of Ids you wish to delete.
        String whereClause = String.format(COLUMN_CODE_ID + " in (%s)", new Object[] { TextUtils.join(",", Collections.nCopies(listId.size(), "?")) });
        db.delete(TABLE_DATA, whereClause, Ids);*/
        for (int i = 0; i < ids.size(); i++) {
            values.put(COLUMN_CODE_ID, ids.get(i));
            db.delete(TABLE_DATA, COLUMN_CODE_ID + "='" + ids.get(i) + "'", new String[]{});
        }

        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_DATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA);
    }




}
