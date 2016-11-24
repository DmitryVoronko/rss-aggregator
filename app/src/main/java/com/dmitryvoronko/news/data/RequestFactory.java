package com.dmitryvoronko.news.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

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
        super();
    }

    static Request<Boolean> update(@NonNull final Channel channel)
    {
        return new Request<Boolean>()
        {
            @Override public Boolean executed(@NonNull final SQLiteDatabase sqLiteDatabase)
                    throws SQLiteException
            {
                @NonNull final ContentValues values =
                        ContentValuesFactory.createContentValues(channel);
                return updateBase(sqLiteDatabase,
                                  NewsContract.ChannelsTable._TABLE_NAME,
                                  values, channel);
            }
        };
    }

    static Request<Long> insert(@NonNull final Channel channel)
    {
        return new Request<Long>()
        {
            @Override public Long executed(@NonNull final SQLiteDatabase sqLiteDatabase)
                    throws SQLiteException
            {

                final ContentValues values =
                        ContentValuesFactory.createContentValues(channel);
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
                    throws SQLiteException
            {
                @NonNull final ContentValues values =
                        ContentValuesFactory.createContentValues(entry);
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
                    throws SQLiteException
            {

                @NonNull final String stringId = String.valueOf(id);
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
                    throws SQLiteException
            {
                @NonNull final String stringId = String.valueOf(id);
                return deleteBase(sqLiteDatabase, stringId, NewsContract.EntryTable._TABLE_NAME);
            }
        };
    }

    static Request<Boolean> channelIsAlreadyExists(@NonNull final String link)
    {

        return new Request<Boolean>()
        {
            @Override public Boolean executed(@NonNull final SQLiteDatabase sqLiteDatabase)
                    throws SQLiteException
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
                    throws SQLiteException
            {
                @NonNull final String tableName = NewsContract.ChannelsTable._TABLE_NAME;
                @NonNull final String[] columns = getChannelColumns();
                @NonNull final String where = NewsContract.ChannelsTable._ID + LIKE;
                @NonNull final String id = String.valueOf(channelId);
                @NonNull final String[] whereArgs = {id};
                @NonNull @Cleanup final Cursor cursor = sqLiteDatabase.query(tableName, columns, where,
                                                                    whereArgs,
                                                                    null, null, null);
                cursor.moveToFirst();
                final int[] columnIndexes = getColumnIndexes(cursor, columns);
                return FeedObjectFactory.createChannel(cursor, columnIndexes);
            }
        };
    }

    @NonNull private static String[] getChannelColumns()
    {
        return new String[]{
                NewsContract.ChannelsTable._ID,
                NewsContract.ChannelsTable._TITLE,
                NewsContract.ChannelsTable._LINK,
                NewsContract.ChannelsTable._DESCRIPTION,
                };
    }

    static Request<ArrayList<Channel>> channelsRequest()
    {
        return new Request<ArrayList<Channel>>()
        {
            @Override public ArrayList<Channel> executed(@NonNull final SQLiteDatabase database)
                    throws SQLiteException
            {
                @NonNull final String tableName = NewsContract.ChannelsTable._TABLE_NAME;

                @NonNull final String[] columns = getChannelColumns();

                @NonNull final String orderBy = NewsContract.ChannelsTable._TITLE;

                @NonNull @Cleanup final Cursor cursor =
                        database.query(tableName, columns, null, null, null, null, orderBy);

                @NonNull final ArrayList<Channel> channelsRequest = new ArrayList<>();

                @NonNull final int[] columnsIndexes = getColumnIndexes(cursor, columns);

                while (cursor.moveToNext())
                {
                    @NonNull final Channel channel = FeedObjectFactory.createChannel(cursor, columnsIndexes);
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
                    throws SQLiteException
            {
                @NonNull final String tableName = NewsContract.EntryTable._TABLE_NAME;
                @NonNull final String[] columns = {
                        NewsContract.EntryTable._ID,
                        NewsContract.EntryTable._TITLE,
                        NewsContract.EntryTable._LINK,
                        NewsContract.EntryTable._DESCRIPTION,
                        NewsContract.EntryTable._CHANNEL_ID
                };

                @NonNull final String orderBy = NewsContract.EntryTable._TITLE;

                @NonNull final String where = NewsContract.EntryTable._CHANNEL_ID + LIKE;
                @NonNull final String stringChannelId = String.valueOf(channelId);
                @NonNull final String[] whereArgs = {stringChannelId};

                @NonNull @Cleanup final Cursor cursor =
                        database.query(tableName, columns, where, whereArgs, null, null, orderBy);

                @NonNull final ArrayList<Entry> entries = new ArrayList<>();

                @NonNull final int[] columnsIndexes = getColumnIndexes(cursor, columns);

                while (cursor.moveToNext())
                {
                    @NonNull final Entry entry = FeedObjectFactory.createEntry(cursor, columnsIndexes);
                    entries.add(entry);
                }

                return entries;
            }
        };
    }

}
