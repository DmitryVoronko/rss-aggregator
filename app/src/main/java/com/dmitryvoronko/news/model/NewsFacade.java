package com.dmitryvoronko.news.model;

import android.content.Context;
import android.support.annotation.Nullable;

import com.dmitryvoronko.news.data.DatabaseManager;
import com.dmitryvoronko.news.model.data.Channel;
import com.dmitryvoronko.news.model.userinput.Status;
import com.dmitryvoronko.news.model.userinput.UserInputHandler;
import com.dmitryvoronko.news.util.NetworkHelper;

import java.util.ArrayList;

/**
 * Created by Dmitry on 01/11/2016.
 */

public final class NewsFacade
{
    private final Context context;
    private final DatabaseManager databaseManager;

    public NewsFacade(final Context context)
    {
        this.context = context;
        databaseManager = new DatabaseManager(context);
    }

    public Status requestAddNewChannel(final String userInput)
    {
        final boolean hasConnection = NetworkHelper.hasConnection(context);
        if (hasConnection)
        {
            final UserInputHandler userInputHandler = new UserInputHandler(databaseManager);
            return userInputHandler.handleUserInput(userInput);
        } else
        {
            return Status.NO_INTERNET_CONNECTION;
        }
    }

    @Nullable public ArrayList<Channel> getChannels(final int startId, final String limit)
    {
        return databaseManager.getChannels(startId, limit);
    }
}
