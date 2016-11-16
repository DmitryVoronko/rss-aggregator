package com.dmitryvoronko.news.data;

import android.content.Context;

import com.dmitryvoronko.news.model.data.Channel;
import com.dmitryvoronko.news.model.data.Entry;

import java.util.ArrayList;

import lombok.NonNull;

/**
 *
 * Created by Dmitry on 01/11/2016.
 */

public final class DatabaseManager extends DatabaseManagerBase
{
    public DatabaseManager(@NonNull final Context context)
    {
        super(context);
    }

    public long insert(@NonNull final Channel channel)
    {
        return new Request<Long>().executeRequest(RequestFactory.insert(channel));
    }

    public void insert(@NonNull final Entry entry)
    {
        new Request<Long>().executeRequest(RequestFactory.insert(entry));
    }

    public void update(@NonNull final Channel channel)
    {
        new Request<Boolean>().executeRequest(RequestFactory.update(channel));
    }

    public void deleteChannel(@NonNull final long id)
    {
        new Request<Boolean>().executeRequest(RequestFactory.deleteChannel(id));
    }

    public void deleteEntry(@NonNull final long id)
    {
        new Request<Boolean>().executeRequest(RequestFactory.deleteEntry(id));
    }

    public boolean channelIsAlreadyExists(@NonNull final String link)
    {
        return new Request<Boolean>().executeRequest(RequestFactory.channelIsAlreadyExists(link));
    }

    public ArrayList<Channel> getChannels()
    {
        return new Request<ArrayList<Channel>>().executeRequest(RequestFactory.channelsRequest());
    }

    public ArrayList<Entry> getEntries(@NonNull final long channelId)
    {
        return new Request<ArrayList<Entry>>()
                .executeRequest(RequestFactory.entriesRequest(channelId));
    }

    public Channel getChannel(final long channelId)
    {
        return new Request<Channel>().executeRequest(RequestFactory.getChannel(channelId));
    }
}