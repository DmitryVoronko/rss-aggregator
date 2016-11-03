package com.dmitryvoronko.news.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.dmitryvoronko.news.model.data.Channel;

/**
 * Created by Dmitry on 01/11/2016.
 */

public final class DatabaseManager
{
    private final NewsDBOpenHelper dbHelper;

    public DatabaseManager(final Context context)
    {
        dbHelper = new NewsDBOpenHelper(context);
    }

    public boolean insertIfNotExist(final Channel channel)
    {
        final SQLiteDatabase database = dbHelper.getWritableDatabase();

        final String link = channel.getLink();

        final String tableName = NewsContract.ChannelsTable._TABLE_NAME;
        final String[] columns = {NewsContract.ChannelsTable._ID};
        final String where = NewsContract.ArticlesTable._LINK + " = ?";
        final String[] whereArgs = {link};

        final Cursor cursor =
                database.query(tableName, columns, where, whereArgs, null, null, null);

        final int count = cursor.getCount();
        cursor.close();
        if (count > 0)
        {
            return false;
        } else
        {
            final ContentValues values = packChannel(channel);
            database.insert(NewsContract.ArticlesTable._TABLE_NAME, null, values);
            return true;
        }
    }

    @NonNull private ContentValues packChannel(final Channel channel)
    {
        final ContentValues values = new ContentValues();
        values.put(NewsContract.ChannelsTable._TITLE, channel.getTitle());
        values.put(NewsContract.ChannelsTable._DESCRIPTION, channel.getDescription());
        values.put(NewsContract.ChannelsTable._LINK, channel.getLink());
        values.put(NewsContract.ChannelsTable._PUB_DATE, channel.getPubDate());
        return values;
    }

}
