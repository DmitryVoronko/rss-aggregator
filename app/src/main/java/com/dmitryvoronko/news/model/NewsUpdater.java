package com.dmitryvoronko.news.model;

import android.content.ContextWrapper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dmitryvoronko.news.data.DatabaseManager;
import com.dmitryvoronko.news.model.data.Channel;
import com.dmitryvoronko.news.model.data.Entry;
import com.dmitryvoronko.news.util.downloader.FileDownloader;
import com.dmitryvoronko.news.util.downloader.FileInfo;
import com.dmitryvoronko.news.util.parser.NewsParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import lombok.Cleanup;
import lombok.Data;

/**
 *
 * Created by Dmitry on 15/11/2016.
 */

final class NewsUpdater
{
    private static final String TAG = "NewsUpdater";

    private final ContextWrapper contextWrapper;
    private final DatabaseManager databaseManager;
    private ExecutorService executorService;

    NewsUpdater(final ContextWrapper contextWrapper,
                final DatabaseManager databaseManager)
    {
        this.contextWrapper = contextWrapper;
        this.databaseManager = databaseManager;
    }

    void updateChannels()
    {
        final ArrayList<Channel> channels = databaseManager.getChannels();

        final int threadsCounts = channels.size();

        executorService = Executors.newFixedThreadPool(threadsCounts);
        while (!executorService.isShutdown())
        {
            final ArrayList<Future<Feed>> futures = new ArrayList<>();

            for (final Channel channel : channels)
            {
                final FileInfo fileInfo = FileInfo.create(channel);
                final Callable<Feed> worker = feedUpdater(fileInfo);
                final Future<Feed> submit = executorService.submit(worker);
                futures.add(submit);
            }

            final ArrayList<Feed> parsedItems = new ArrayList<>();

            for (final Future<Feed> feedFuture : futures)
            {
                try
                {
                    final Feed feed = feedFuture.get();
                    parsedItems.add(feed);
                } catch (final InterruptedException e)
                {
                    Log.d(TAG, "updateChannels: " + e);
                } catch (final ExecutionException e)
                {
                    Log.d(TAG, "updateChannels: " + e);
                    e.printStackTrace();
                }
            }

            executorService.shutdown();

            insertToDatabase(parsedItems);
        }
    }

    private Callable<Feed> feedUpdater(final FileInfo fileInfo)
    {
        return new Callable<Feed>()
        {
            @Override public Feed call() throws Exception
            {
                return getUpdatedFeed(fileInfo);
            }
        };
    }

    private void insertToDatabase(final ArrayList<Feed> parsedItems)
    {
        for (final Feed feed : parsedItems)
        {
            insertToDatabase(feed);
        }
    }

    @Nullable private Feed getUpdatedFeed(final FileInfo fileInfo)
    {
        final FileDownloader fileDownloader = new FileDownloader(contextWrapper);
        fileDownloader.downloadFile(fileInfo);
        final NewsParser parser = new NewsParser();
        final Channel updatedChannel;
        try
        {
            updatedChannel = getChannel(parser, fileInfo);
            final ArrayList<Entry> updatedEntries = getEntries(parser, updatedChannel, fileInfo);
            contextWrapper.deleteFile(fileInfo.getFileName());
            return new Feed(updatedChannel, updatedEntries);
        } catch (final IOException e)
        {
            Log.d(TAG, "getUpdatedFeed: IO Exception = " + e);
            return null;
        } catch (final XmlPullParserException e)
        {
            Log.d(TAG, "getUpdatedFeed: Xml Pull Parser Exception = " + e);
            return null;
        }
    }

    private void insertToDatabase(final Feed feed)
    {
        if (feed != null)
        {
            databaseManager.update(feed.getChannel());
            for (final Entry entry : feed.getEntries())
            {
                databaseManager.insert(entry);
            }
        }
    }

    private Channel getChannel(final NewsParser parser, final FileInfo fileInfo)
            throws IOException, XmlPullParserException
    {
        @Cleanup final FileInputStream fileInputStream =
                contextWrapper.openFileInput(fileInfo.getFileName());
        return parser.parse(fileInputStream, fileInfo.getLink());
    }

    private ArrayList<Entry> getEntries(final NewsParser parser, final Channel updatedChannel,
                                        final FileInfo fileInfo)
            throws XmlPullParserException, IOException
    {
        @Cleanup final FileInputStream fileInputStream =
                contextWrapper.openFileInput(fileInfo.getFileName());
        return parser.parse(fileInputStream, updatedChannel.getId());
    }

    void updateChannel(final long channelId)
    {
        final Channel channel = databaseManager.getChannel(channelId);
        final FileInfo fileInfo = FileInfo.create(channel);
        final Feed feed = getUpdatedFeed(fileInfo);
        insertToDatabase(feed);
    }

    void cancelUpdate()
    {
        if (executorService != null)
        {
            executorService.shutdownNow();
            Log.d(TAG, "cancelUpdate: Executor Service shutdown now");
        }
    }

    @Data
    private final class Feed
    {
        private final Channel channel;
        private final ArrayList<Entry> entries;
    }
}