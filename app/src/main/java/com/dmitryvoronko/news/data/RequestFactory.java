package com.dmitryvoronko.news.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dmitryvoronko.news.model.data.Channel;
import com.dmitryvoronko.news.model.data.Entry;
import com.dmitryvoronko.news.model.data.FeedObjectFactory;

import java.util.ArrayList;

import lombok.Cleanup;
import lombok.NonNull;

import static com.dmitryvoronko.news.data.NewsContract.LIKE;

/**
 *
 * Created by Dmitry on 04/11/2016.
 */

final class RequestFactory extends RequestFactoryBase
{
    private RequestFactory()
    {
        throw new UnsupportedOperationException();
    }

    static Request<Boolean> update(@NonNull final Channel channel)
    {
        return new Request<Boolean>()
        {
            @Override public Boolean executed(@NonNull final SQLiteDatabase sqLiteDatabase)
            {
                final ContentValues values = ContentValuesFactory.createContentValues(channel);
                return updateBase(sqLiteDatabase, NewsContract.ChannelsTable._TABLE_NAME,
                                  values, channel);
            }
        };
    }

    static Request<Long> insert(@NonNull final Channel channel)
    {
        return new Request<Long>()
        {
            @Override public Long executed(@NonNull final SQLiteDatabase sqLiteDatabase)
            {

                final ContentValues values = ContentValuesFactory.createContentValues(channel);
                return insertBase(sqLiteDatabase,
                                  NewsContract.ChannelsTable._TABLE_NAME, values, channel);
            }
        };
    }

    static Request<Long> insert(@NonNull final Entry entry)
    {
        return new Request<Long>()
        {
            @Override public Long executed(@NonNull final SQLiteDatabase sqLiteDatabase)
            {
                final ContentValues values = ContentValuesFactory.createContentValues(entry);
                return insertBase(sqLiteDatabase, NewsContract.EntryTable._TABLE_NAME,
                                  values, entry);
            }
        };
    }

    static Request<Boolean> deleteChannel(final long id)
    {
        return new Request<Boolean>()
        {
            @Override public Boolean executed(@NonNull final SQLiteDatabase sqLiteDatabase)
            {

                final String stringId = String.valueOf(id);
                final boolean channelRemoved =
                        deleteBase(sqLiteDatabase, stringId,
                                   NewsContract.ChannelsTable._TABLE_NAME);
                final boolean channelEntriesRemoved =
                        deleteChannelEntries(sqLiteDatabase, stringId);
                return channelRemoved && channelEntriesRemoved;
            }
        };
    }

    static Request<Boolean> deleteEntry(final long id)
    {
        return new Request<Boolean>()
        {
            @Override public Boolean executed(@NonNull final SQLiteDatabase sqLiteDatabase)
            {
                final String stringId = String.valueOf(id);
                return deleteBase(sqLiteDatabase, stringId, NewsContract.EntryTable._TABLE_NAME);
            }
        };
    }

    static Request<Boolean> channelIsAlreadyExists(@NonNull final String link)
    {

        return new Request<Boolean>()
        {
            @Override public Boolean executed(@NonNull final SQLiteDatabase sqLiteDatabase)
            {
                return alreadyExistsBase(sqLiteDatabase, link,
                                         NewsContract.ChannelsTable._TABLE_NAME);
            }
        };
    }

    static Request<Channel> getChannel(final long channelId)
    {
        return new Request<Channel>()
        {
            @Override public Channel executed(final SQLiteDatabase sqLiteDatabase)
            {
                final String tableName = NewsContract.ChannelsTable._TABLE_NAME;
                final String[] columns = getChannelColumns();
                final String where = NewsContract.ChannelsTable._ID + LIKE;
                final String id = String.valueOf(channelId);
                final String[] whereArgs = {id};
                @Cleanup final Cursor cursor = sqLiteDatabase.query(tableName, columns, where,
                                                                    whereArgs,
                                                                    null, null, null);
                cursor.moveToFirst();
                final int[] columnIndexes = getColumnIndexes(cursor, columns);
                return FeedObjectFactory.createChannel(cursor, columnIndexes);
            }
        };
    }

    @android.support.annotation.NonNull private static String[] getChannelColumns()
    {
        return new String[]{
                NewsContract.ChannelsTable._ID,
                NewsContract.ChannelsTable._TITLE,
                NewsContract.ChannelsTable._LINK,
                NewsContract.ChannelsTable._DESCRIPTION,
                NewsContract.ChannelsTable._STATE,
                };
    }

    static Request<ArrayList<Channel>> channelsRequest()
    {
        return new Request<ArrayList<Channel>>()
        {
            @Override public ArrayList<Channel> executed(@NonNull final SQLiteDatabase database)
            {
                final String tableName = NewsContract.ChannelsTable._TABLE_NAME;

                final String[] columns = getChannelColumns();

                final String orderBy =
                        NewsContract.ChannelsTable._STATE + ", " +
                                NewsContract.ChannelsTable._TITLE;

                @Cleanup final Cursor cursor =
                        database.query(tableName, columns, null, null, null, null, orderBy);

                final ArrayList<Channel> channelsRequest = new ArrayList<>();

                final int[] columnsIndexes = getColumnIndexes(cursor, columns);

                while (cursor.moveToNext())
                {
                    final Channel channel = FeedObjectFactory.createChannel(cursor, columnsIndexes);
                    channelsRequest.add(channel);
                }

                return channelsRequest;
            }
        };
    }

    static Request<ArrayList<Entry>> entriesRequest(final long channelId)
    {
        return new Request<ArrayList<Entry>>()
        {
            @Override public ArrayList<Entry> executed(@NonNull final SQLiteDatabase database)
            {
                final String tableName = NewsContract.EntryTable._TABLE_NAME;
                final String[] columns = {
                        NewsContract.EntryTable._ID,
                        NewsContract.EntryTable._TITLE,
                        NewsContract.EntryTable._LINK,
                        NewsContract.EntryTable._DESCRIPTION,
                        NewsContract.EntryTable._STATE,
                        NewsContract.EntryTable._CHANNEL_ID
                };

                final String orderBy = NewsContract.EntryTable._STATE + ", "
                        + NewsContract.EntryTable._TITLE;

                final String where = NewsContract.EntryTable._CHANNEL_ID + LIKE;
                final String stringChannelId = String.valueOf(channelId);
                final String[] whereArgs = {stringChannelId};

                @Cleanup final Cursor cursor =
                        database.query(tableName, columns, where, whereArgs, null, null, orderBy);


                final ArrayList<Entry> entries = new ArrayList<>();

                final int[] columnsIndexes = getColumnIndexes(cursor, columns);

                while (cursor.moveToNext())
                {
                    final Entry entry = FeedObjectFactory.createEntry(cursor, columnsIndexes);
                    entries.add(entry);
                }

                return entries;
            }
        };
    }

}
