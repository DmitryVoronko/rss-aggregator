package com.dmitryvoronko.news.model.userinput;

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

    private final DatabaseManager databaseManager;

    public UserInputHandler(final DatabaseManager databaseManager)
    {
        this.databaseManager = databaseManager;
    }

    public Status handleUserInput(final String userInput)
    {
        try
        {
            final URL url = new URL(userInput);
            final boolean hasAlready = databaseManager.hasAlreadyChannel(url.toString());

            if (hasAlready)
            {
                return Status.HAS_ALREADY;
            } else {
                final Channel channel = parseChannel(url);
                return insertToDatabase(channel);
            }

        } catch (final MalformedURLException e)
        {
            return Status.NOT_URL;
        } catch (final XmlPullParserException e)
        {
            return Status.NOT_XML;
        } catch (final IOException e)
        {
            return Status.TOTAL_ERROR;
        }
    }

    private Channel parseChannel(final URL url) throws IOException, XmlPullParserException
    {
        final Parser parser = new Parser();
        return parser.parseChannel(url);
    }


    private Status insertToDatabase(final Channel channel)
    {
        databaseManager.insert(channel);
        final Status status = Status.ADDED;
        Log.d(TAG, "insertToDatabase: Status = " + status);
        return status;
    }
}
