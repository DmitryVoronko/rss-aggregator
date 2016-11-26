package com.dmitryvoronko.news.model;

import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.Nullable;

import com.dmitryvoronko.news.data.DatabaseManager;
import com.dmitryvoronko.news.model.data.Channel;
import com.dmitryvoronko.news.model.data.Entry;
import com.dmitryvoronko.news.model.userinput.Status;
import com.dmitryvoronko.news.model.userinput.NewChannelHandler;
import com.dmitryvoronko.news.util.NetworkHelper;

import java.util.ArrayList;

import lombok.NonNull;

/**
 *
 * Created by Dmitry on 01/11/2016.
 */

public final class NewsFacade
{
    private final Context context;
    private final DatabaseManager databaseManager;
    private final NewsUpdater updater;
    private NewChannelHandler newChannelHandler;

    public NewsFacade(@NonNull final ContextWrapper context)
    {
        this.context = context;
        databaseManager = new DatabaseManager(context);
        updater = new NewsUpdater(context, databaseManager);
    }

    public Status requestAddNewChannel(@NonNull final String userInput)
    {
        final boolean hasConnection = NetworkHelper.hasConnection(context);
        if (hasConnection)
        {
            newChannelHandler = new NewChannelHandler(databaseManager);
            return newChannelHandler.handleUserInput(userInput);
        } else
        {
            return Status.NO_INTERNET_CONNECTION;
        }
    }

    @Nullable public ArrayList<Channel> getChannels()
    {
        return databaseManager.getChannels();
    }

    public ArrayList<Entry> getEntries(final long channelId)
    {
        return databaseManager.getEntries(channelId);
    }

    public void deleteChannel(final long id)
    {
        databaseManager.deleteChannel(id);
    }

    public UpdateStatus updateChannels()
    {
        return updater.updateChannels();
    }

    public void deleteEntry(final long id)
    {
        databaseManager.deleteEntry(id);
    }

    public UpdateStatus updateEntries(final long channelId)
    {
        return updater.updateChannel(channelId);
    }

    public void cancelUpdate()
    {
        updater.cancel();
    }

    public void cancelAddNewChannel()
    {
        newChannelHandler.cancel();
    }
}
