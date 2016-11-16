package com.dmitryvoronko.news.util.parser;

import com.dmitryvoronko.news.model.data.Channel;
import com.dmitryvoronko.news.model.data.Entry;
import com.dmitryvoronko.news.model.data.FeedObjectFactory;
import com.dmitryvoronko.news.model.data.State;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 *
 * Created by Dmitry on 31/10/2016.
 */

public final class NewsParser
{
    private static final String TAG = "NewsParser";

    public NewsParser()
    {

    }

    public Channel parse(final InputStream in, final String link)
            throws IOException,
                   XmlPullParserException,
                   UnsupportedOperationException
    {
        final XmlPullParser parser = getParser(in);


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
                    if (tagName.equalsIgnoreCase(ParserContract.ITEM))
                    {
                        done = true;
                    } else
                    {
                        if (tagName.equalsIgnoreCase(ParserContract.TITLE))
                        {
                            title = parser.nextText();
                        } else if (tagName.equalsIgnoreCase(ParserContract.DESCRIPTION))
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

        return FeedObjectFactory.createChannel(title, link, description);
    }

    private XmlPullParser getParser(final InputStream in)
            throws IOException,
                   XmlPullParserException,
                   UnsupportedOperationException
    {
        final XmlPullParserFactory factory;
        factory = XmlPullParserFactory.newInstance();
        final XmlPullParser parser = factory.newPullParser();
        parser.setInput(in, null);
        parser.nextTag();

        checkDocumentFormat(parser);

        return parser;
    }

    private void checkDocumentFormat(final XmlPullParser parser)
    {
        final String tagName = parser.getName();

        if (!tagName.equalsIgnoreCase(ParserContract.RSS))
        {
            throw new UnsupportedOperationException();
        }

        final String rssVersion = parser.getAttributeValue(null, ParserContract.VERSION);

        if (rssVersion == null)
        {
            throw new UnsupportedOperationException();
        }

        if (!rssVersion.equalsIgnoreCase(ParserContract.RSS_VERSION_2_0))
        {
            throw new UnsupportedOperationException();
        }
    }

    public ArrayList<Entry> parse(final InputStream in, final long channelId)
            throws XmlPullParserException,
                   IOException,
                   UnsupportedOperationException
    {
        final XmlPullParser parser = getParser(in);

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
                    if (tagName.equalsIgnoreCase(ParserContract.ITEM))
                    {
                        title = null;
                        description = null;
                        link = null;
                    } else
                    {
                        if (tagName.equalsIgnoreCase(ParserContract.TITLE))
                        {
                            title = parser.nextText();
                        } else if (tagName.equalsIgnoreCase(ParserContract.LINK))
                        {
                            link = parser.nextText();
                        } else if (tagName.equalsIgnoreCase(ParserContract.DESCRIPTION))
                        {
                            description = parser.nextText();
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    final String name = parser.getName();
                    if (name.equalsIgnoreCase(ParserContract.ITEM))
                    {
                        if (description == null)
                        {
                            description = "";
                        }

                        final Entry entry = FeedObjectFactory.createEntry(FeedObjectFactory.NO_ID,
                                                                          title,
                                                                          link,
                                                                          description,
                                                                          State.IS_NOT_READ,
                                                                          channelId);
                        entries.add(entry);
                    } else if (name.equalsIgnoreCase(ParserContract.CHANNEL))
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
}
