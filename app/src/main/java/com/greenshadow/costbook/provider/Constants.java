package com.greenshadow.costbook.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public interface Constants {
    String AUTHORITY_BASE = "com.greenshadow.cost.";

    String DB_NAME = "cost_data.db";
    String TABLE_COST = "cost";
    String TABLE_COST_TMP = "cost_tmp"; // used for down grade db

    interface CostList extends BaseColumns {
        String AUTHORITY = AUTHORITY_BASE + "list";

        Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
        Uri TRACKS_URI = Uri.withAppendedPath(CONTENT_URI, "tracks");
        Uri TRACKS_COST_URI = Uri.withAppendedPath(TRACKS_URI, "cost");
        Uri THREAD_URI = Uri.withAppendedPath(CONTENT_URI, "thread");
        Uri RECORD_URI = Uri.withAppendedPath(CONTENT_URI, "record");

        String TITLE = "title";
        String TOTAL = "total";
        String PRICE = "price";
        String PRICE_TYPE = "price_type";
        String CURRENCY_TYPE = "currency_type";
        String BUY_TIME = "buy_time";
        String NOTE = "note";
        String TIME = "time";

        String PRICE_SUM = "sum";
    }
}
