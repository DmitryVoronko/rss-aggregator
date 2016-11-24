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
        return executeRequest(RequestFactory.insert(channel));
    }

    public void insert(@NonNull final Entry entry)
    {
        executeRequest(RequestFactory.insert(entry));
    }

    public void update(@NonNull final Channel channel)
    {
        executeRequest(RequestFactory.update(channel));
    }

    public void deleteChannel(final long id) throws IllegalArgumentException
    {
        if (id <= -1)
        {
            throw new IllegalArgumentException();
        } else
        {
            executeRequest(RequestFactory.deleteChannel(id));
        }
    }

    public void deleteEntry(final long id) throws IllegalArgumentException
    {
        if (id <= -1)
        {
            throw new IllegalArgumentException();
        } else
        {
            executeRequest(RequestFactory.deleteEntry(id));
        }
    }

    public boolean channelIsAlreadyExists(@NonNull final String link)
    {
        return executeRequest(RequestFactory.channelIsAlreadyExists(link));
    }

    public ArrayList<Channel> getChannels()
    {
        return executeRequest(RequestFactory.channelsRequest());
    }

    public ArrayList<Entry> getEntries(final long channelId) throws IllegalArgumentException
    {
        if (channelId <= -1)
        {
            throw new IllegalArgumentException();
        } else
        {
            return executeRequest(RequestFactory.entriesRequest(channelId));
        }
    }

    public Channel getChannel(final long channelId) throws IllegalArgumentException
    {
        if (channelId <= -1)
        {
            throw new IllegalArgumentException();
        } else
        {
            return executeRequest(RequestFactory.getChannel(channelId));
        }
    }
}