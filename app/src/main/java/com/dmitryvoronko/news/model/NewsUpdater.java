package com.dmitryvoronko.news.model;

import android.content.ContextWrapper;
import android.support.annotation.Nullable;

import com.dmitryvoronko.news.data.DatabaseManager;
import com.dmitryvoronko.news.model.data.Channel;
import com.dmitryvoronko.news.model.data.Entry;
import com.dmitryvoronko.news.util.downloader.FileDownloader;
import com.dmitryvoronko.news.util.downloader.FileInfo;
import com.dmitryvoronko.news.util.log.Logger;
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
import java.util.concurrent.RejectedExecutionException;

import lombok.Cleanup;
import lombok.Data;
import lombok.NonNull;

/**
 *
 * Created by Dmitry on 15/11/2016.
 */

final class NewsUpdater extends Cancellable
{
    private static final String TAG = "NewsUpdater";

    private final ContextWrapper contextWrapper;
    private final DatabaseManager databaseManager;

    NewsUpdater(@NonNull final ContextWrapper contextWrapper,
                @NonNull final DatabaseManager databaseManager)
    {
        this.contextWrapper = contextWrapper;
        this.databaseManager = databaseManager;

    }

    UpdateStatus updateChannels()
    {
        Logger.i(TAG, "start update");
        final int threadsCount = Runtime.getRuntime().availableProcessors() * 2;
        Logger.i(TAG, "threads count = " + threadsCount);
        final ExecutorService executorService = Executors.newFixedThreadPool(threadsCount);
        final ArrayList<Channel> channels = databaseManager.getChannels();

        final ArrayList<Future<Feed>> futures = new ArrayList<>();

        for (final Channel channel : channels)
        {
            if (canceled)
            {
                break;
            }
            final FileInfo fileInfo = FileInfo.valueOf(channel);
            final Callable<Feed> worker = feedUpdater(fileInfo, channel.getId());
            final Future<Feed> submit = executorService.submit(worker);
            futures.add(submit);
        }

        final ArrayList<Feed> parsedItems = new ArrayList<>();

        for (final Future<Feed> feedFuture : futures)
        {
            if (canceled)
            {
                break;
            }
            try
            {
                final Feed feed = feedFuture.get();
                parsedItems.add(feed);
            } catch (final InterruptedException e)
            {
                Logger.e(TAG, "updateChannels: Interrupted Exception", e);
            } catch (final ExecutionException e)
            {
                Logger.e(TAG, "updateChannels: Execution Exception", e);
            } catch (final RejectedExecutionException e)
            {
                Logger.e(TAG, "updateChannels: RejectedExecutionException", e);
            }
        }

        executorService.shutdown();

        Logger.i(TAG, "Finish update");

        insertToDatabase(parsedItems);
        if (!canceled)
        {
            return UpdateStatus.UPDATED;
        } else
        {
            return UpdateStatus.CANCELED;
        }
    }

    private Callable<Feed> feedUpdater(final FileInfo fileInfo, final long channelId)
    {
        return new Callable<Feed>()
        {
            @Override public Feed call() throws Exception
            {
                return getUpdatedFeed(fileInfo, channelId);
            }
        };
    }

    private void insertToDatabase(final ArrayList<Feed> parsedItems)
    {
        for (final Feed feed : parsedItems)
        {
            if (canceled)
            {
                break;
            }
            insertToDatabase(feed);
        }
    }

    @Nullable private Feed getUpdatedFeed(final FileInfo fileInfo, final long channelId)
    {
        final FileDownloader fileDownloader = new FileDownloader(contextWrapper);
        fileDownloader.download(fileInfo);
        final NewsParser parser = new NewsParser();
        final Channel updatedChannel;
        try
        {
            updatedChannel = getChannel(parser, fileInfo, channelId);
            final ArrayList<Entry> updatedEntries = getEntries(parser, channelId, fileInfo);
            contextWrapper.deleteFile(fileInfo.getFileName());
            return new Feed(updatedChannel, updatedEntries);
        } catch (final IOException e)
        {
            Logger.e(TAG, "getUpdatedFeed: IO Exception", e);
            return null;
        } catch (final XmlPullParserException e)
        {
            Logger.e(TAG, "getUpdatedFeed: Xml Pull Parser Exception", e);
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

    private Channel getChannel(@NonNull final NewsParser parser,
                               @NonNull final FileInfo fileInfo,
                               final long channelId)
            throws IOException, XmlPullParserException
    {
        @Cleanup final FileInputStream fileInputStream =
                contextWrapper.openFileInput(fileInfo.getFileName());
        return parser.parse(fileInputStream, fileInfo.getLink(), channelId);
    }

    private ArrayList<Entry> getEntries(@NonNull final NewsParser parser,
                                        final long channelId,
                                        @NonNull final FileInfo fileInfo)
            throws XmlPullParserException, IOException
    {
        @Cleanup final FileInputStream fileInputStream =
                contextWrapper.openFileInput(fileInfo.getFileName());
        return parser.parse(fileInputStream, channelId);
    }

    UpdateStatus updateChannel(final long channelId)
    {
        @NonNull final Channel channel = databaseManager.getChannel(channelId);
        @NonNull final FileInfo fileInfo = FileInfo.valueOf(channel);
        final Feed feed = getUpdatedFeed(fileInfo, channelId);
        if (feed != null && !canceled)
        {
            insertToDatabase(feed);
            return UpdateStatus.UPDATED;
        } else
        {
            return UpdateStatus.CANCELED;
        }
    }

    @Data
    private final class Feed
    {
        private final Channel channel;
        private final ArrayList<Entry> entries;
    }

}
