package com.dmitryvoronko.news.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import lombok.NonNull;

/**
 *
 * Created by Dmitry on 04/11/2016.
 */

abstract class DatabaseManagerBase
{
    private static final String TAG = "DatabaseManagerBase";

    private final NewsDBOpenHelper dbHelper;

    DatabaseManagerBase(@NonNull final Context context) throws SQLiteException
    {
        this.dbHelper = new NewsDBOpenHelper(context);
    }

    final class Request<T>
    {
        T executeRequest(@NonNull final com.dmitryvoronko.news.data.Request<T> request)
        {
            T result = null;
            try
            {
                final SQLiteDatabase database = dbHelper.getWritableDatabase();
                result = request.executed(database);
            } catch (final SQLiteException e)
            {
                Log.d(TAG, "executeRequest: SQLite Exception = " + e);
            } finally
            {
                dbHelper.close();
            }
            return result;
        }
    }
}