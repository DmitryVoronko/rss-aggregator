package com.dmitryvoronko.news.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dmitryvoronko.news.model.data.Channel;
import com.dmitryvoronko.news.model.data.FeedObjectFactory;

import java.util.ArrayList;

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

    public boolean hasAlreadyChannel(final String link) {
        return hasAlready(link, NewsContract.ChannelsTable._TABLE_NAME);
    }

    public long insert(final Channel channel) {
        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        final ContentValues values = packChannel(channel);
        return database.insert(NewsContract.ChannelsTable._TABLE_NAME, null, values);
    }

    private int getCount(final SQLiteDatabase database,
                         final String tableName,
                         final String where,
                         final String[] whereArgs)
    {
        final String[] columns = {NewsContract.ChannelsTable._ID};


        final Cursor cursor =
                database.query(tableName, columns, where, whereArgs, null, null, null);

        final int count = cursor.getCount();
        cursor.close();
        return count;
    }

    // FIXME: 03/11/2016 refactor
    @Nullable public ArrayList<Channel> getChannels(final int startId,
                                                    final String limit) {
        final SQLiteDatabase database = dbHelper.getWritableDatabase();

        final String tableName = NewsContract.ChannelsTable._TABLE_NAME;
        final String where = NewsContract.ChannelsTable._ID + " >= ?";
        final String[] whereArgs = {"" + startId};
        final int count = getCount(database, tableName, where, whereArgs);

        if (count > 0)
        {


            final String[] columns = {
                    NewsContract.ChannelsTable._ID,
                    NewsContract.ChannelsTable._TITLE,
                    NewsContract.ChannelsTable._LINK,
                    NewsContract.ChannelsTable._DESCRIPTION,
                    NewsContract.ChannelsTable._PUB_DATE,
                    NewsContract.ChannelsTable._STATE,
                    };
            final String orderBy =
                    NewsContract.ChannelsTable._STATE + ", " + NewsContract.ChannelsTable._TITLE;
            final Cursor cursor =
                    database.query(tableName, columns, where, whereArgs, null, null, orderBy,
                                   limit);
            final ArrayList<Channel> channels = new ArrayList<>();

            final int[] columnsIndexes = getColumnIndexes(cursor, columns);

            while (cursor.moveToNext())
            {
                final int id = cursor.getInt(columnsIndexes[0]);
                final String title = cursor.getString(columnsIndexes[1]);
                final String link = cursor.getString(columnsIndexes[2]);
                final String description = cursor.getString(columnsIndexes[3]);
                final String pubDate = cursor.getString(columnsIndexes[4]);
                final String state = cursor.getString(columnsIndexes[5]);
                final Channel channel =
                        FeedObjectFactory.createChannel(id, title, link, description, pubDate,
                                                        state);
                channels.add(channel);
            }

            cursor.close();

            return channels;
        } else {
            return null;
        }
    }

    @NonNull private ContentValues packChannel(final Channel channel)
    {
        final ContentValues values = new ContentValues();
        values.put(NewsContract.ChannelsTable._TITLE, channel.getTitle());
        values.put(NewsContract.ChannelsTable._DESCRIPTION, channel.getDescription());
        values.put(NewsContract.ChannelsTable._LINK, channel.getLink());
        values.put(NewsContract.ChannelsTable._PUB_DATE, channel.getPubDate());
        values.put(NewsContract.ChannelsTable._STATE, channel.getState().name());
        return values;
    }

    private int[] getColumnIndexes(final Cursor cursor, final String ... ids) {
        final int length = ids.length;
        final int[] columnIndexes = new int[length];

        for (int i = 0; i < length; i++)
        {
            final String id = ids[i];
            columnIndexes[i] = cursor.getColumnIndex(id);
        }

        return columnIndexes;
    }

    private boolean hasAlready(final String link, final String tableName)
    {
        final SQLiteDatabase database = dbHelper.getWritableDatabase();

        final String where = NewsContract.BaseTable._LINK + " = ?";
        final String[] whereArgs = {link};

        final int count = getCount(database,tableName, where, whereArgs);
        return count > 0;
    }

}
