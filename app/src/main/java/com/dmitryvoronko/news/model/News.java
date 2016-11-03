package com.dmitryvoronko.news.model;

import android.content.Context;

import com.dmitryvoronko.news.model.userinput.Status;
import com.dmitryvoronko.news.model.userinput.UserInputHandler;
import com.dmitryvoronko.news.util.NetworkHelper;

/**
 * Created by Dmitry on 01/11/2016.
 */

public final class News
{
    private final Context context;

    public News(final Context context)
    {
        this.context = context;
    }

    public Status requestAddNewChannel(final String userInput)
    {
        final boolean hasConnection = NetworkHelper.hasConnection(context);
        if (hasConnection)
        {
            final UserInputHandler userInputHandler = new UserInputHandler(context);
            return userInputHandler.handleUserInput(userInput);
        } else
        {
            return Status.NO_INTERNET_CONNECTION;
        }
    }
}
