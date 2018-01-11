package com.daniel.requestservices.Utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by daniel on 10/01/18.
 */

public class DBManager {
    private SQLiteDatabase database;
    private DBHandler dbHandler;

    public DBManager(Context context) {
        dbHandler = new DBHandler(context);
        database = dbHandler.getWritableDatabase();
        dbHandler.onCreate(database);
        database.close();
    }

    public void insertUrl(String urlPath) {
        String insertQuery = String.format("INSERT INTO %s (%s) " +
                        "VALUES ('%s')",
                DBHandler.URL_TABLE, DBHandler.URL_PATH,
                urlPath);
        database = dbHandler.getWritableDatabase();
        try {
            database.execSQL(insertQuery);
        } catch (Exception e) {
            e.printStackTrace();
        }

        database.close();
    }

    public Cursor getAllUrl() {
        String selectQuery = String.format(
                "SELECT * FROM %s",
                DBHandler.URL_TABLE
        );

        SQLiteDatabase db = dbHandler.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }
}
