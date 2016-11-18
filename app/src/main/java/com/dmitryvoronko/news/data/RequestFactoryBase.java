package com.dmitryvoronko.news.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.dmitryvoronko.news.model.data.Channel;

import lombok.NonNull;

/**
 *
 * Created by Dmitry on 15/11/2016.
 */

abstract class RequestFactoryBase
{
    static long insertBase(@NonNull final SQLiteDatabase sqLiteDatabase,
                           @NonNull final String tableName,
                           @NonNull final ContentValues values,
                           @NonNull final Channel channel)
            throws SQLiteException
    {

        final String link = values.getAsString(NewsContract.BaseTable._LINK);
        final boolean alreadyExists = alreadyExistsBase(sqLiteDatabase, link, tableName);
        if (alreadyExists)
        {
            updateBase(sqLiteDatabase, tableName, values, channel);
            return channel.getId();
        } else
        {
            return sqLiteDatabase.insert(tableName, null, values);
        }
    }

    static boolean alreadyExistsBase(@NonNull final SQLiteDatabase database,
                                     @NonNull final String link,
                                     @NonNull final String tableName)
            throws SQLiteException
    {
        final String where = NewsContract.BaseTable._LINK + NewsContract.LIKE;
        final String[] whereArgs = {link};
        final int count = getCount(database, tableName, where, whereArgs);
        return count > 0;
    }

    static boolean updateBase(@NonNull final SQLiteDatabase sqLiteDatabase,
                              @NonNull final String tableName,
                              @NonNull final ContentValues values,
                              @NonNull final Channel channel)
            throws SQLiteException
    {
        final String selection = NewsContract.BaseTable._ID + NewsContract.LIKE;
        final String id = String.valueOf(channel.getId());
        final String whereArgs[] = {id};
        sqLiteDatabase.update(tableName, values, selection, whereArgs);
        return true;
    }

    private static int getCount(@NonNull final SQLiteDatabase database,
                                @NonNull final String tableName,
                                @NonNull final String where,
                                @NonNull final String[] whereArgs)
            throws SQLiteException
    {
        final String[] columns = {NewsContract.BaseTable._ID};
        final Cursor cursor =
                database.query(tableName, columns, where, whereArgs, null, null, null);
        final int count = cursor.getCount();
        cursor.close();
        return count;
    }

    static boolean deleteBase(@NonNull final SQLiteDatabase sqLiteDatabase,
                              final String id,
                              @NonNull final String tableName)
            throws SQLiteException
    {
        final String selection = NewsContract.BaseTable._ID + NewsContract.LIKE;
        final String[] selectionArgs = {id};

        sqLiteDatabase.delete(tableName, selection, selectionArgs);
        return true;
    }

    static boolean deleteChannelEntries(@NonNull final SQLiteDatabase sqLiteDatabase,
                                        final String id)
            throws SQLiteException
    {
        final String selection = NewsContract.EntryTable._CHANNEL_ID + NewsContract.LIKE;
        final String[] selectionArgs = {id};
        sqLiteDatabase.delete(NewsContract.EntryTable._TABLE_NAME, selection, selectionArgs);
        return true;
    }

    static int[] getColumnIndexes(@NonNull final Cursor cursor,
                                  @NonNull final String... ids)
    {
        final int length = ids.length;
        final int[] columnIndexes = new int[length];

        for (int i = 0; i < length; i++)
        {
            final String id = ids[i];
            columnIndexes[i] = cursor.getColumnIndex(id);
        }

        return columnIndexes;
    }

}
