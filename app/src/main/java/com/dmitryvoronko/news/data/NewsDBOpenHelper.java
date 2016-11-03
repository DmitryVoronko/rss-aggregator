package com.dmitryvoronko.news.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dmitry on 21/10/2016.
 */

final class NewsDBOpenHelper
        extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "newsDatabase.db";
    private static final int DATABASE_VERSION = 1;

    public NewsDBOpenHelper(final Context context,
                            final String name,
                            final SQLiteDatabase.CursorFactory factory,
                            final int version)
    {
        super(context,
              name,
              factory,
              version);
    }

    NewsDBOpenHelper(final Context context)
    {
        super(context,
              DATABASE_NAME,
              null,
              DATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db)
    {
        db.execSQL(NewsContract.DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db,
                          final int oldVersion,
                          final int newVersion)
    {
        db.execSQL(NewsContract.DATABASE_DELETE);
        onCreate(db);
    }
}
