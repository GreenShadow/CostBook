package com.greenshadow.costbook.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.greenshadow.costbook.BuildConfig;
import com.greenshadow.costbook.utils.Log;

public class CostDbHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 2;

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
                Constants.CostList.PRICE_TYPE + "    INTEGER        NOT NULL," +
                Constants.CostList.CURRENCY_TYPE + " INTEGER        NOT NULL," +
                Constants.CostList.BUY_TIME + "      [TEXT NUMERIC]," +
                Constants.CostList.NOTE + "          TEXT," +
                Constants.CostList.TIME + "          INTEGER        NOT NULL" +
                ");";
        if (BuildConfig.LOG_DEBUG) {
            Log.d(this, "sql = " + createTable);
        }

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == newVersion && newVersion == DB_VERSION) {
            return;
        }

        int currentVersion = oldVersion;

        // upgrade to version 2
        if (currentVersion == 1) {
            String addPriceType = "ALTER TABLE " + Constants.TABLE_COST + " ADD COLUMN " +
                    Constants.CostList.PRICE_TYPE + " INTEGER NOT NULL DEFAULT (1);";
            if (BuildConfig.LOG_DEBUG) {
                Log.d(this, "addPriceType = " + addPriceType);
            }
            db.execSQL(addPriceType);
            currentVersion++;
            Log.d(this, "upgraded db to version " + currentVersion);
        }

        // Add upgrade logic here in future.

        if (currentVersion == newVersion && currentVersion == DB_VERSION) {
            Log.d(this, "db upgrade finish");
        } else {
            Log.w(this, "could not upgrade db to latest version!");
            Log.w(this, "DB_VERSION = " + DB_VERSION + ", oldVersion = " + oldVersion
                    + ", newVersion = " + newVersion + ", currentVersion = " + currentVersion);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == newVersion) {
            return;
        }

        int currentVersion = oldVersion;

        // Add downgrade logic here in future.

        if (currentVersion == 2) {
            String sql = "CREATE TABLE " + Constants.TABLE_COST_TMP + " AS SELECT " +
                    Constants.CostList._ID + "          , " +
                    Constants.CostList.TITLE + "        , " +
                    Constants.CostList.TOTAL + "        , " +
                    Constants.CostList.PRICE + "        , " +
                    Constants.CostList.CURRENCY_TYPE + ", " +
                    Constants.CostList.BUY_TIME + "     , " +
                    Constants.CostList.NOTE + "         , " +
                    Constants.CostList.TIME + "         FROM " +
                    Constants.TABLE_COST + "            ;" +
                    "\n" +
                    "DROP TABLE IF EXISTS " + Constants.TABLE_COST + ";" +
                    "\n" +
                    "ALTER TABLE " + Constants.TABLE_COST_TMP + " RENAME TO " + Constants.TABLE_COST + ";";
            db.execSQL(sql);

            if (BuildConfig.LOG_DEBUG) {
                Log.d(this, "sql = " + sql);
            }

            currentVersion--;
        }

        if (currentVersion == newVersion) {
            Log.d(this, "db downgrade finish");
        } else {
            Log.w(this, "could not downgrade db to current version!");
            Log.w(this, "oldVersion = " + oldVersion + ", newVersion = " + newVersion
                    + ", currentVersion = " + currentVersion);
        }
    }
}
