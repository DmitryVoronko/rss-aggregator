package com.dmitryvoronko.news.model.userinput;

import android.util.Log;

import com.dmitryvoronko.news.data.DatabaseManager;
import com.dmitryvoronko.news.model.data.Channel;
import com.dmitryvoronko.news.model.data.Entry;
import com.dmitryvoronko.news.util.parser.NewsParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import lombok.Cleanup;
import lombok.NonNull;

/**
 *
 * Created by Dmitry on 01/11/2016.
 */

public final class UserInputHandler
{
    private final static String TAG = "USER_INPUT_HANDLER";
    private static final int NO_CHANNEL = -1;

    private final DatabaseManager databaseManager;

    public UserInputHandler(@NonNull final DatabaseManager databaseManager)
    {
        this.databaseManager = databaseManager;
    }

    public Status handleUserInput(@NonNull final String userInput)
    {
        try
        {
            final URL url = new URL(userInput);
            final String link = url.toString();
            final boolean hasAlready = databaseManager.channelIsAlreadyExists(link);

            if (hasAlready)
            {
                return Status.ALREADY_EXISTS;
            } else
            {
                final Channel channel;
                long channelId = NO_CHANNEL;
                final ArrayList<Entry> entries;
                try
                {
                    final NewsParser newsParser = new NewsParser();
                    channel = parseChannel(url, newsParser);
                    channelId = databaseManager.insert(channel);
                    entries = parseEntries(url, channelId, newsParser);
                } catch (final XmlPullParserException e)
                {
                    if (channelId != NO_CHANNEL)
                    {
                        databaseManager.deleteChannel(channelId);
                    }
                    return Status.NOT_XML;
                } catch (final UnsupportedOperationException e)
                {
                    return Status.UNSUPPORTED_FORMAT;
                }
                return insertToDatabase(entries);
            }

        } catch (final MalformedURLException e)
        {
            return Status.NOT_URL;
        } catch (final IOException e)
        {
            return Status.TOTAL_ERROR;
        }
    }

    private Channel parseChannel(final URL url, final NewsParser newsParser)
            throws IOException, XmlPullParserException
    {
        final Channel channel;
        @Cleanup final InputStream inputStream = url.openStream();
        channel = newsParser.parse(inputStream, url.toString());
        return channel;
    }

    private ArrayList<Entry> parseEntries(final URL url, final long channelId,
                                          final NewsParser newsParser)
            throws IOException, XmlPullParserException
    {
        final ArrayList<Entry> entries;
        @Cleanup final InputStream inputStream = url.openStream();
        entries = newsParser.parse(inputStream, channelId);
        return entries;
    }

    private Status insertToDatabase(@NonNull final ArrayList<Entry> entries)
    {
        for (final Entry entry : entries)
        {
            databaseManager.insert(entry);
        }
        final Status status = Status.ADDED;
        Log.d(TAG, "insertToDatabase: Status = " + status);
        return status;
    }
}
