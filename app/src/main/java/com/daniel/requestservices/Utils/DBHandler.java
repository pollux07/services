package com.daniel.requestservices.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by daniel on 10/01/18.
 */

public class DBHandler extends SQLiteOpenHelper{
    private static final String DB_NAME = "services.db";
    private static final int DB_VERSION = 2;

    static final String URL_TABLE = "url";
    static final String URL_ID = "_id";
    static final String URL_PATH = "url_path";

    private static final String CREATE_TABLE_URL = String.format(
            "CREATE TABLE IF NOT EXISTS %s (" +
                    "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "%s TEXT NOT NULL)",
            URL_TABLE,
            URL_ID,
            URL_PATH
    );

    DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_URL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < DB_VERSION) {
            String stmtDropUrl = String.format("drop table if exists %s; %s",
                    URL_TABLE,
                    CREATE_TABLE_URL);

            db.execSQL(stmtDropUrl);
            onCreate(db);
        }
    }
}
