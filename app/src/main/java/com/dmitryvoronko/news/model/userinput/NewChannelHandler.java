package com.dmitryvoronko.news.model.userinput;

import com.dmitryvoronko.news.data.DatabaseManager;
import com.dmitryvoronko.news.model.Cancellable;
import com.dmitryvoronko.news.model.data.Channel;
import com.dmitryvoronko.news.model.data.Entry;
import com.dmitryvoronko.news.model.data.FeedObjectFactory;
import com.dmitryvoronko.news.util.log.Logger;
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

public final class NewChannelHandler extends Cancellable
{
    private static final String TAG = "NewChannelHandler";
    private static final int NO_CHANNEL = -1;

    private final DatabaseManager databaseManager;

    public NewChannelHandler(@NonNull final DatabaseManager databaseManager)
    {
        this.databaseManager = databaseManager;
    }

    public Status handleUserInput(@NonNull final String userInput)
    {
        try
        {
            @NonNull final URL url = new URL(userInput);
            @NonNull final String link = url.toString();
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
                    @NonNull final NewsParser newsParser = new NewsParser();
                    channel = parseChannel(url, newsParser);
                    channelId = databaseManager.insert(channel);
                    entries = parseEntries(url, channelId, newsParser);
                } catch (final XmlPullParserException e)
                {
                    deleteChannel(channelId);
                    return Status.NOT_XML;
                } catch (final UnsupportedOperationException e)
                {
                    return Status.UNSUPPORTED_FORMAT;
                }
                return insertToDatabase(entries, channelId);
            }

        } catch (final MalformedURLException e)
        {
            return Status.NOT_URL;
        } catch (final IOException e)
        {
            return Status.TOTAL_ERROR;
        }
    }

    private void deleteChannel(final long channelId)
    {
        if (channelId != NO_CHANNEL)
        {
            databaseManager.deleteChannel(channelId);
        }
    }

    private Channel parseChannel(@NonNull final URL url,
                                 @NonNull final NewsParser newsParser)
            throws IOException, XmlPullParserException
    {
        @Cleanup final InputStream inputStream = url.openStream();
        return newsParser.parse(inputStream, url.toString(), FeedObjectFactory.NO_ID);
    }

    private ArrayList<Entry> parseEntries(@NonNull final URL url, final long channelId,
                                          @NonNull final NewsParser newsParser)
            throws IOException, XmlPullParserException
    {
        @Cleanup final InputStream inputStream = url.openStream();
        return newsParser.parse(inputStream, channelId);
    }

    private Status insertToDatabase(@NonNull final ArrayList<Entry> entries,
                                    final long channelId)
    {
        for (final Entry entry : entries)
        {
            if (isCanceled())
            {
                break;
            }
            databaseManager.insert(entry);
        }
        final Status status;
        if (isCanceled())
        {
            deleteChannel(channelId);
            status = Status.CANCELED;
        } else
        {
            status = Status.ADDED;
        }
        Logger.i(TAG, "insertToDatabase: Status = " + status);
        return status;
    }
}
