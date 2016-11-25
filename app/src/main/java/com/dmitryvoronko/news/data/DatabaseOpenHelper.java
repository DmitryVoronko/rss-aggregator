package com.dmitryvoronko.news.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import lombok.NonNull;

/**
 *
 * Created by Dmitry on 21/10/2016.
 */

final class DatabaseOpenHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "newsDatabase.db";
    private static final int DATABASE_VERSION = 1;

    DatabaseOpenHelper(@NonNull final Context context)
    {
        super(context,
              DATABASE_NAME,
              null,
              DATABASE_VERSION);
    }

    @Override
    public void onCreate(@NonNull final SQLiteDatabase db)
    {
        db.execSQL(NewsContract.ChannelsTable.CREATE_TABLE);
        db.execSQL(NewsContract.EntryTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(@NonNull final SQLiteDatabase db,
                          @NonNull final int oldVersion,
                          @NonNull final int newVersion)
    {
        db.execSQL(NewsContract.DATABASE_DELETE);
        onCreate(db);
    }
}
