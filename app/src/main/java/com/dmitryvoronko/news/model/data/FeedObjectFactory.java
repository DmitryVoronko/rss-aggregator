package com.dmitryvoronko.news.model.data;

import android.database.Cursor;

import lombok.NonNull;

/**
 *
 * Created by Dmitry on 03/11/2016.
 */

public final class FeedObjectFactory
{
    public static final int NO_ID = -1;

    private static final int INDEX_ID_COLUMN = 0;
    private static final int INDEX_TITLE_COLUMN = 1;
    private static final int INDEX_LINK_COLUMN = 2;
    private static final int INDEX_DESCRIPTION_COLUMN = 3;
    private static final int INDEX_CHANNEL_ID_COLUMN = 4;

    private FeedObjectFactory()
    {
        throw new UnsupportedOperationException();
    }

    public static Entry createEntry(@NonNull final Cursor cursor, final int[] columnsIndexes)
    {
        @NonNull final Channel channel = createChannel(cursor, columnsIndexes);
        @NonNull final long channelId = cursor.getLong(columnsIndexes[INDEX_CHANNEL_ID_COLUMN]);

        return createEntry(channel, channelId);
    }

    public static Channel createChannel(@NonNull final Cursor cursor, final int[] columnsIndexes)
    {
        @NonNull final long id = cursor.getLong(columnsIndexes[INDEX_ID_COLUMN]);
        @NonNull final String title = cursor.getString(columnsIndexes[INDEX_TITLE_COLUMN]);
        @NonNull final String link = cursor.getString(columnsIndexes[INDEX_LINK_COLUMN]);
        @NonNull final String description =
                cursor.getString(columnsIndexes[INDEX_DESCRIPTION_COLUMN]);
        return createChannel(id, title, link, description);
    }

    private static Entry createEntry(@NonNull final Channel channel, final long channelId)
    {
        return createEntry(channel.getId(), channel.getTitle(), channel.getLink(),
                           channel.getDescription(),
                           channelId);
    }

    public static Channel createChannel(final long id,
                                        @NonNull final String title,
                                        @NonNull final String link,
                                        @NonNull final String description)
    {
        return new FeedObject(id, title, link, description);
    }

    public static Entry createEntry(final long id,
                                    @NonNull final String title,
                                    @NonNull final String link,
                                    @NonNull final String description,
                                    final long channelId)
    {
        return new FeedEntry(id, title, link, description, channelId);
    }


    public static Channel createChannel(@NonNull final String title,
                                        @NonNull final String link,
                                        @NonNull final String description)
    {
        return createChannel(NO_ID, title, link, description);
    }
}
