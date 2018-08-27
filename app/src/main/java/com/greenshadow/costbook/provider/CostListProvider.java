package com.greenshadow.costbook.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.greenshadow.costbook.utils.Log;

public class CostListProvider extends ContentProvider {
    private static final int TRACKS = 0;
    private static final int THREAD = 1;
    private static final int THREAD_LIST = 2;
    private static final int RECORD = 3;

    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(Constants.CostList.AUTHORITY, "tracks", TRACKS);
        sUriMatcher.addURI(Constants.CostList.AUTHORITY, "thread", THREAD);
        sUriMatcher.addURI(Constants.CostList.AUTHORITY, "thread/*", THREAD_LIST);
        sUriMatcher.addURI(Constants.CostList.AUTHORITY, "record/#", RECORD);
    }

    private CostDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new CostDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
            @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        String groupBy = null;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case TRACKS:
                groupBy = Constants.CostList.TITLE;
                break;
            case THREAD_LIST:
                selection = Constants.CostList.TITLE + "=?";
                selectionArgs = new String[]{ uri.getLastPathSegment() };
                break;
            case RECORD:
                selection = Constants.CostList._ID + "=?";
                selectionArgs = new String[]{ uri.getLastPathSegment() };
                break;
            default:
                Log.w(this, "query : Unknown uri : " + uri.toString());
                return null;
        }

        if (projection != null && projection.length == 1
                && BaseColumns._COUNT.equals(projection[0])) {
            long count = DatabaseUtils.queryNumEntries(mDbHelper.getReadableDatabase(),
                    Constants.TABLE_COST, selection, selectionArgs);
            MatrixCursor cursor = new MatrixCursor(new String[]{ "COUNT(*)" });
            cursor.addRow(new String[]{ String.valueOf(count) });
            return cursor;
        }

        return mDbHelper.getReadableDatabase().query(Constants.TABLE_COST, projection, selection,
                selectionArgs, groupBy, null, sortOrder);
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (sUriMatcher.match(uri) < 0) {
            Log.e(this, "insert : Unknown uri : " + uri.toString());
            return null;
        }

        long id = mDbHelper.getWritableDatabase().insert(Constants.TABLE_COST, null, values);
        if (id < 0) {
            Log.e(this, "insert : insert error, values = " + values);
            return null;
        }

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case THREAD_LIST:
                selection = Constants.CostList.TITLE + "=?";
                selectionArgs = new String[]{ uri.getLastPathSegment() };
                break;
            case RECORD:
                selection = Constants.CostList._ID + "=?";
                selectionArgs = new String[]{ uri.getLastPathSegment() };
                break;
            default:
                Log.w(this, "delete : Unknown uri : " + uri.toString());
                return -1;
        }

        return mDbHelper.getWritableDatabase().delete(Constants.TABLE_COST, selection, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
            @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case THREAD_LIST:
                break;
            case RECORD:
                selection = Constants.CostList._ID + "=?";
                selectionArgs = new String[]{ uri.getLastPathSegment() };
                break;
            default:
                Log.w(this, "update : Unknown uri : " + uri.toString());
                return -1;
        }

        return mDbHelper.getWritableDatabase().update(Constants.TABLE_COST, values, selection, selectionArgs);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
