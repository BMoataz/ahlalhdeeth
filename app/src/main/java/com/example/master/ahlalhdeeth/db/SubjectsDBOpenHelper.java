package com.example.master.ahlalhdeeth.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SubjectsDBOpenHelper extends SQLiteOpenHelper {

    private static final String LOGTAG = "THELOGTAGOFDATABASE";

    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_SUBJECTS = "subjects";

    public static final String COLUMN_ID = "Id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_LAST_MODIFIED = "http_last_modified";

    private static final String TABLE_CREATE_1 = " CREATE TABLE " + TABLE_SUBJECTS + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_TITLE + " TEXT, " + COLUMN_LINK + " TEXT, " + COLUMN_LAST_MODIFIED + " NUMERIC )";

    public SubjectsDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_1);
//        db.execSQL(TABLE_CREATE_2);
        Log.i(LOGTAG,"TABLES HAVE BEEN CREATED");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_SUBJECTS);
        onCreate(db);
    }
}

