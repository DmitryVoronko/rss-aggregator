package com.dmitryvoronko.news.model.userinput;

import android.content.Context;
import android.util.Log;

import com.dmitryvoronko.news.data.DatabaseManager;
import com.dmitryvoronko.news.model.data.Channel;
import com.dmitryvoronko.news.model.parser.Parser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Dmitry on 01/11/2016.
 */

public final class UserInputHandler
{
    private final static String TAG = "USER_INPUT_HANDLER";

    private final Context context;

    public UserInputHandler(final Context context)
    {
        this.context = context;
    }

    public Status handleUserInput(final String userInput)
    {
        try
        {
            final URL url = new URL(userInput);
            final Parser parser = new Parser();
            final Channel channel = parser.parseChannel(url);
            return tryToAddChannelToDB(channel);
        } catch (final MalformedURLException e)
        {
            return Status.NOT_URL;
        } catch (final XmlPullParserException e)
        {
            Log.d(TAG, "handleUserInput: Xml Pull Parser Exception", e);
            return Status.NOT_XML;
        } catch (final IOException e)
        {
            return Status.TOTAL_ERROR;
        }
    }


    private Status tryToAddChannelToDB(final Channel channel)
    {
        final DatabaseManager manager = new DatabaseManager(context);
        final boolean inserted = manager.insertIfNotExist(channel);

        if (inserted)
        {
            return Status.ADDED;
        } else
        {
            return Status.HAS_ALREADY;
        }
    }

}
