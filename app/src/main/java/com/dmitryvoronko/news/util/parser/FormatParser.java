package com.dmitryvoronko.news.util.parser;

import android.support.annotation.NonNull;

import com.dmitryvoronko.news.model.data.Channel;
import com.dmitryvoronko.news.model.data.Entry;
import com.dmitryvoronko.news.model.data.FeedObjectFactory;
import com.dmitryvoronko.news.util.log.Logger;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * Created by Dmitry on 18/11/2016.
 */

abstract class FormatParser
{
    private static final String TAG = "FormatParser";
    final Channel parse(final XmlPullParser parser, final String link, final long channelId)
            throws IOException,
                   XmlPullParserException,
                   UnsupportedOperationException
    {
        String title = null;
        String description = null;

        int eventType = parser.getEventType();
        boolean done = false;
        while (eventType != XmlPullParser.END_DOCUMENT && !done)
        {
            switch (eventType)
            {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    final String tagName = parser.getName();
                    if (tagName.equalsIgnoreCase(getEntryTagName()))
                    {
                        done = true;
                    } else
                    {
                        if (tagName.equalsIgnoreCase(getChannelTitleTagName()))
                        {
                            title = parser.nextText();
                        } else if (tagName.equalsIgnoreCase(getChannelDescriptionTagName()))
                        {
                            description = parser.nextText();
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                default:
                    break;
            }
            eventType = parser.next();
        }

        if (description == null)
        {
            description = "";
        }

        if (title == null)
        {
            Logger.e(TAG, "parse(): Not supported format title = null, link = " + link);
            throw new UnsupportedOperationException();
        } else
        {
            if (channelId == FeedObjectFactory.NO_ID)
            {
                return FeedObjectFactory.createChannel(title, link, description);
            } else
            {
                return FeedObjectFactory.createChannel(channelId, title, link, description);
            }
        }
    }

    @NonNull protected abstract String getChannelDescriptionTagName();

    @NonNull protected abstract String getChannelTitleTagName();

    @NonNull protected abstract String getEntryTagName();


    final ArrayList<Entry> parse(final XmlPullParser parser, final long channelId)
            throws XmlPullParserException,
                   IOException,
                   UnsupportedOperationException
    {
        final ArrayList<Entry> entries = new ArrayList<>();

        String title = null;
        String description = null;
        String link = null;

        int eventType = parser.getEventType();
        boolean done = false;
        while (eventType != XmlPullParser.END_DOCUMENT && !done)
        {
            switch (eventType)
            {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    final String tagName = parser.getName();
                    if (tagName.equalsIgnoreCase(getEntryTagName()))
                    {
                        title = null;
                        description = null;
                        link = null;
                    } else
                    {
                        if (tagName.equalsIgnoreCase(getEntryTitleTagName()))
                        {
                            title = parser.nextText();
                        } else if (tagName.equalsIgnoreCase(ParserContract.LINK))
                        {
                            link = parseLink(parser, link);
                        } else if (tagName.equalsIgnoreCase(getEntryDescriptionTagName()))
                        {
                            description = parser.nextText();
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    final String name = parser.getName();
                    if (name.equalsIgnoreCase(getEntryTagName()))
                    {
                        if (description == null)
                        {
                            description = "";
                        }

                        if (title == null || link == null)
                        {
                            break;
                        }

                        final Entry entry =
                                FeedObjectFactory.createEntry(FeedObjectFactory.NO_ID,
                                                              title,
                                                              link,
                                                              description,
                                                              channelId);
                        entries.add(entry);
                    } else if (name.equalsIgnoreCase(getChannelTagName()))
                    {
                        done = true;
                    }
                    break;
                default:
                    break;
            }
            eventType = parser.next();
        }

        return entries;
    }

    @NonNull protected abstract String parseLink(final XmlPullParser parser, final String link)
            throws XmlPullParserException, IOException;

    @NonNull protected abstract String getChannelTagName();

    @NonNull protected abstract String getEntryDescriptionTagName();

    @NonNull protected abstract String getEntryTitleTagName();

}
