package com.greenshadow.costbook.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CostDbHelper extends SQLiteOpenHelper {
    private static final String TAG = CostDbHelper.class.getSimpleName();
    private static final int DB_VERSION = 1;

    /*package*/ CostDbHelper(Context context) {
        super(context, Constants.DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + Constants.TABLE_COST + "(" +
                Constants.CostList._ID + "           INTEGER        PRIMARY KEY AUTOINCREMENT," +
                Constants.CostList.TITLE + "         [TEXT TEXT]    NOT NULL," +
                Constants.CostList.TOTAL + "         [TEXT NUMERIC] NOT NULL," +
                Constants.CostList.PRICE + "         [TEXT NUMERIC] NOT NULL," +
                Constants.CostList.CURRENCY_TYPE + " INTEGER        NOT NULL," +
                Constants.CostList.BUY_TIME + "      [TEXT NUMERIC]," +
                Constants.CostList.NOTE + "          TEXT," +
                Constants.CostList.TIME + "          INTEGER        NOT NULL" +
                ");";
        Log.d(TAG, "sql = " + createTable);

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
