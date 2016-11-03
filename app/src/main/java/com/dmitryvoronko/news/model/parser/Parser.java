package com.dmitryvoronko.news.model.parser;

import android.support.annotation.Nullable;
import android.util.Log;

import com.dmitryvoronko.news.model.data.Channel;
import com.dmitryvoronko.news.model.data.FeedObjectFactory;
import com.dmitryvoronko.news.model.data.State;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Dmitry on 31/10/2016.
 */

public final class Parser
{
    private static final String TAG = "RSS/ATOM_PARSER";

    public Parser()
    {
    }

    @Nullable public Channel parseChannel(final URL url) throws IOException, XmlPullParserException
    {
        final XmlPullParser parser = getParser();
        final InputStream inputStream = url.openStream();
        final String link = url.toString();


        if (parser != null)
        {
            parser.setInput(inputStream, null);
            parser.nextTag();
            final Channel channel = readChannelHeader(parser, link);
            closeInputStream(inputStream);
            return channel;
        }

        return null;
    }


    private Channel readChannelHeader(final XmlPullParser parser, final String sourceLink)
            throws XmlPullParserException, IOException
    {
        String title = null;
        String description = null;
        String link = null;
        String pubDate = null;

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
                        } else if (tagName.equalsIgnoreCase(ParserContract.LINK))
                        {
                            link = parser.nextText();
                        } else if (tagName.equalsIgnoreCase(ParserContract.DESCRIPTION))
                        {
                            description = parser.nextText();
                        } else if (tagName.equalsIgnoreCase(ParserContract.PUB_DATE))
                        {
                            pubDate = parser.nextText();
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

        return FeedObjectFactory.createChannel(-1, title, sourceLink, description, pubDate,
                                               State.IS_NOT_READ);
    }

    private void closeInputStream(InputStream in)
    {
        try
        {
            in.close();
        } catch (final IOException e)
        {
            Log.d(TAG, "closeInputStream: TOTAL_ERROR Exception", e);
        }
    }

    @Nullable private XmlPullParser getParser()
    {
        try
        {
            final XmlPullParserFactory factory;
            factory = XmlPullParserFactory.newInstance();
            return factory.newPullParser();
        } catch (final XmlPullParserException e)
        {
            Log.d(TAG, "getParser: Xml Pull Parser Exception");
        }
        return null;
    }

}
