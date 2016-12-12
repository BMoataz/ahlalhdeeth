package com.example.master.ahlalhdeeth.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

/**
 * Created by Master on 05/11/2014.
 */

public class DataSource {

    private static final String LOGTAG = "THELOGTAGOFDATABASE";

    public SQLiteOpenHelper dbhelper;
    public SQLiteDatabase database;
    public static final String[] Columns = {SubjectsDBOpenHelper.COLUMN_TITLE, SubjectsDBOpenHelper.COLUMN_LINK};

    public DataSource(Context context) {

        dbhelper = new SubjectsDBOpenHelper(context);
    }

    public void open() {
        database = dbhelper.getWritableDatabase();
        Log.i(LOGTAG, "Database opened");
    }

    public void close() {
        Log.i(LOGTAG, "Database closed");
        dbhelper.close();
    }

    public void create(Elements Items) {
        database.delete(SubjectsDBOpenHelper.TABLE_SUBJECTS, null, null);
        for (Element Item : Items) {
            ContentValues values = new ContentValues();
            values.put(SubjectsDBOpenHelper.COLUMN_TITLE, Item.text());
            values.put(SubjectsDBOpenHelper.COLUMN_LAST_MODIFIED, "xxxx-xx-xx xx:xx:xx");
            values.put(SubjectsDBOpenHelper.COLUMN_LINK, "http://www.ahlalhdeeth.com/vb/" + Item.attr("href"));
            long insertid = database.insert(SubjectsDBOpenHelper.TABLE_SUBJECTS, null, values);
        }
    }

    public String[] FindTitles() {

        String[] data = null;

        Cursor cursor = database.query(SubjectsDBOpenHelper.TABLE_SUBJECTS, Columns
                , null, null, null, null, null);

        Log.i(LOGTAG, "Have Returned " + cursor.getCount() + " Title");

        if (cursor.getCount() > 0) {
            int i = 0;
            data = new String[cursor.getCount()];
            while (cursor.moveToNext()) {
                data[i] = cursor.getString(cursor.getColumnIndex(SubjectsDBOpenHelper.COLUMN_TITLE));
                i++;
            }
        }
        return data;
    }

    public String[] FindHrefs() {
        String[] href = null;

        Cursor cursor = database.query(SubjectsDBOpenHelper.TABLE_SUBJECTS, Columns
                , null, null, null, null, null);
        Log.i(LOGTAG, "Have Returned " + cursor.getCount() + " Href");
        if (cursor.getCount() > 0) {
            int i = 0;
            href = new String[cursor.getCount()];
            while (cursor.moveToNext()) {
                href[i] = cursor.getString(cursor.getColumnIndex(SubjectsDBOpenHelper.COLUMN_LINK));
                i++;
            }
        }
        return href;
    }

    public HashMap<String, String> findValuesAndHrefs() {
        HashMap<String, String> valuesAndHrefs = new HashMap<>();
        Cursor cursor = database.query(SubjectsDBOpenHelper.TABLE_SUBJECTS,
                new String[]{SubjectsDBOpenHelper.COLUMN_TITLE, SubjectsDBOpenHelper.COLUMN_LINK},
                null, null, null, null, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(SubjectsDBOpenHelper.COLUMN_TITLE));
                String href = cursor.getString(cursor.getColumnIndex(SubjectsDBOpenHelper.COLUMN_LINK));
                valuesAndHrefs.put(data, href);
            }
        }
        return valuesAndHrefs;
    }
}
