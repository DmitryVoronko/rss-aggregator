package com.dmitryvoronko.news.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.dmitryvoronko.news.util.log.Logger;

import lombok.NonNull;

/**
 *
 * Created by Dmitry on 04/11/2016.
 */

abstract class DatabaseManagerBase
{
    private static final String TAG = "DatabaseManagerBase";

    private final DatabaseOpenHelper databaseHelper;

    DatabaseManagerBase(@NonNull final Context context) throws SQLiteException
    {
        this.databaseHelper = new DatabaseOpenHelper(context);
    }


    <T> T executeRequest(@NonNull final com.dmitryvoronko.news.data.Request<T> request)
    {
        T result = null;
        try
        {
            final SQLiteDatabase database = databaseHelper.getWritableDatabase();
            result = request.executed(database);
        } catch (final SQLiteException e)
        {
            Logger.e(TAG, "executeRequest: SQLite Exception ", e);
        } finally
        {
            databaseHelper.close();
        }
        return result;
    }
}