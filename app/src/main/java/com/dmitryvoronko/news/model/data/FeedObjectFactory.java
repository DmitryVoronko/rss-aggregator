package com.dmitryvoronko.news.model.data;

/**
 * Created by Dmitry on 03/11/2016.
 */

public final class FeedObjectFactory
{
    public static Entry createFeedEntry(final int id, final String title, final String link,
                                 final String description, final String pubDate,
                                 final State state, final int channelId)
    {
        return new FeedEntry(id, title, link, description, pubDate, state, channelId);
    }

    public static Channel createChannel(final int id, final String title, final String link,
                                 final String description, final String pubDate,
                                 final State state)
    {
        return new FeedObject(id, title, link, description, pubDate, state);
    }

    public static Channel createChannel(final int id, final String title, final String link,
                                        final String description, final String pubDate,
                                        final String stateString)
    {
        final State state = State.valueOf(stateString);
        return new FeedObject(id, title, link, description, pubDate, state);
    }
}
