package com.dmitryvoronko.news.data;

import android.content.ContentValues;

import com.dmitryvoronko.news.model.data.Channel;
import com.dmitryvoronko.news.model.data.Entry;

import lombok.NonNull;

/**
 *
 * Created by Dmitry on 04/11/2016.
 */

final class ContentValuesFactory
{
    private ContentValuesFactory()
    {
        throw new UnsupportedOperationException();
    }

    static ContentValues createContentValues(@NonNull final Channel channel)
    {
        return createBaseContentValueWithoutId(channel);
    }

    private static ContentValues createBaseContentValueWithoutId(@NonNull final Channel channel)
    {
        final ContentValues values = new ContentValues();
        values.put(NewsContract.BaseTable._TITLE, channel.getTitle());
        values.put(NewsContract.BaseTable._DESCRIPTION, channel.getDescription());
        values.put(NewsContract.BaseTable._LINK, channel.getLink());
        return values;
    }

    static ContentValues createContentValues(@NonNull final Entry entry)
    {
        final ContentValues values = createBaseContentValueWithoutId(entry);
        values.put(NewsContract.EntryTable._CHANNEL_ID, entry.getChannelId());
        return values;
    }

}
