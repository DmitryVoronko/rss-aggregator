package com.dmitryvoronko.news.model.parser;

import android.support.annotation.Nullable;
import android.util.Log;

import com.dmitryvoronko.news.model.data.Article;
import com.dmitryvoronko.news.model.data.Channel;
import com.dmitryvoronko.news.model.data.Item;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Dmitry on 31/10/2016.
 */

public final class Parser
{
    private static final String NAMESPACE = null;
    private static final String TAG = "PARSER";

    public Parser()
    {

    }

    @Nullable public Channel parseChannel(final URL url) throws IOException, XmlPullParserException
    {
        final XmlPullParser parser = getParser();
        final InputStream inputStream = url.openStream();


        if (parser != null)
        {
            parser.setInput(inputStream, null);

            parser.nextTag();
            final Item item = readItem(parser);
            final String link = url.toString();

            return new Channel(item.getTitle(),
                               item.getDescription(),
                               link, item.getPubDate());
        }

        return null;
    }


    private Item readItem(final XmlPullParser parser) throws XmlPullParserException, IOException
    {
        String title = null;
        String description = null;
        String link = null;
        String pubDate = null;

        while (parser.next() != XmlPullParser.END_TAG)
        {
            if (parser.getEventType() != XmlPullParser.START_TAG)
            {
                continue;
            }
            final String tagName = parser.getName();
//            Log.d(TAG, "readItem: tagName" + tagName);
            switch (tagName)
            {
                case ParserContract.TITLE:
                    title = readTitle(parser);
                    break;
                case ParserContract.LINK:
                    link = readLink(parser);
                    break;
                case ParserContract.DESCRIPTION:
                    description = readDescription(parser);
                    break;
                case ParserContract.PUB_DATE:
                    pubDate = readPubDate(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }

        }

        return new Item(title, link, description, pubDate);
    }


    private void closeInputStream(InputStream in)
    {
        try
        {
            in.close();
        } catch (final IOException e)
        {
            Log.d(TAG,
                  "closeInputStream: TOTAL_ERROR Exception",
                  e);
        }
    }

    private ArrayList<Article> readFeed(final XmlPullParser parser,
                                        final int channelId)
    {
        final ArrayList<Article> articles = new ArrayList<>();
        try
        {
            parser.require(XmlPullParser.START_TAG,
                           NAMESPACE,
                           ParserContract.CHANNEL);

            while (parser.next() != XmlPullParser.END_TAG)
            {
                if (parser.getEventType() != XmlPullParser.START_TAG)
                {
                    continue;
                }
                final String name = parser.getName();
                if (name.equals(ParserContract.ITEM))
                {
                    articles.add(readEntry(parser,
                                           channelId));
                } else
                {
                    skip(parser);
                }
            }
        } catch (final XmlPullParserException e)
        {
            Log.d(TAG, "readFeed: Xml Pull Parser Exception", e);
        } catch (final IOException e)
        {
            Log.d(TAG, "readFeed: TOTAL_ERROR Exception", e);
        }

        return articles;
    }

    private Article readEntry(final XmlPullParser parser,
                              final int channelId)
            throws
            IOException,
            XmlPullParserException
    {
        String title = null;
        String description = null;
        String link = null;
        String pubDate = null;

        while (parser.next() != XmlPullParser.END_TAG)
        {
            if (parser.getEventType() != XmlPullParser.START_TAG)
            {
                continue;
            }

            final String attributeName = parser.getName();

            switch (attributeName)
            {
                case ParserContract.TITLE:
                    title = readTitle(parser);
                    break;
                case ParserContract.LINK:
                    link = readLink(parser);
                    break;
                case ParserContract.DESCRIPTION:
                    description = readDescription(parser);
                    break;
                case ParserContract.PUB_DATE:
                    pubDate = readPubDate(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }

        return new Article(title, description, link, channelId, pubDate);
    }

    private void skip(final XmlPullParser parser)
    {
        try
        {
            if (parser.getEventType() != XmlPullParser.START_TAG)
            {
                throw new IllegalStateException();
            }
            int depth = 1;
            while (depth != 0)
            {
                switch (parser.next())
                {
                    case XmlPullParser.END_TAG:
                        depth--;
                        break;
                    case XmlPullParser.START_TAG:
                        depth++;
                        break;
                    default:
                        break;
                }
            }
        } catch (final XmlPullParserException e)
        {
            Log.d(TAG, "skip: Xml Pull Parser Exception", e);
        } catch (final IOException e)
        {
            Log.d(TAG, "skip: TOTAL_ERROR Exception", e);
        }
    }

    private String readPubDate(final XmlPullParser parser)
    {
        return readSimpleTextAttribute(parser, ParserContract.PUB_DATE);
    }

    private String readDescription(final XmlPullParser parser)
    {
        return readSimpleTextAttribute(parser, ParserContract.DESCRIPTION);
    }

    private String readLink(final XmlPullParser parser)
    {
        return readSimpleTextAttribute(parser, ParserContract.LINK);
    }

    private String readTitle(final XmlPullParser parser)
    {
        return readSimpleTextAttribute(parser, ParserContract.LINK);
    }

    @Nullable private String readSimpleTextAttribute(final XmlPullParser parser,
                                                     final String tagName)
    {
        String result = null;

        try
        {

            parser.require(XmlPullParser.START_TAG, NAMESPACE, tagName);
            result = readText(parser);
            parser.require(XmlPullParser.END_TAG, NAMESPACE, tagName);
        } catch (final XmlPullParserException e)
        {
            Log.d(TAG, "readSimpleTextAttribute: Xml Pull Parse Exception", e);
        } catch (final IOException e)
        {
            Log.d(TAG, "readSimpleTextAttribute: TOTAL_ERROR Exception", e);
        }

        return result;
    }

    private String readText(final XmlPullParser parser)
    {
        String result = "";
        try
        {
            if (parser.next() == XmlPullParser.TEXT)
            {
                result = parser.getText();
                parser.nextTag();
            }
        } catch (final XmlPullParserException e)
        {
            Log.d(TAG, "readText: Xml Pull Parser Exception", e);
        } catch (final IOException e)
        {
            Log.d(TAG, "readText: TOTAL_ERROR Exception", e);
        }
        return result;
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
