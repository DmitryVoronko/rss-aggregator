package com.dmitryvoronko.news.util.parser;

import com.dmitryvoronko.news.model.data.Channel;
import com.dmitryvoronko.news.model.data.Entry;
import com.dmitryvoronko.news.util.log.Logger;

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

    public Channel parse(final InputStream in, final String link)
            throws IOException,
                   XmlPullParserException,
                   UnsupportedOperationException
    {
        final XmlPullParser parser = getParser(in);
        final FormatParser formatParser = getFormatParser(parser);
        return formatParser.parse(parser, link);
    }

    private FormatParser getAtomFormatParser(final XmlPullParser parser)
    {
        final String atomXMLNS = parser.getAttributeValue(null, ParserContract.XMLNS);

        if (atomXMLNS == null)
        {
            Logger.e(TAG, "getAtomFormatParser(): atomXMLNS is null");
            throw new UnsupportedOperationException();
        }

        if (!atomXMLNS.equalsIgnoreCase(ParserContract.ATOM_XMLNS))
        {
            Logger.e(TAG, "getAtomFormatParser(): Not supported atomXMLNS = " + atomXMLNS);
            throw new UnsupportedOperationException();
        }

        return new AtomParser();
    }

    private FormatParser getRSSFormatParser(final XmlPullParser parser)
    {
        final String rssVersion = parser.getAttributeValue(null, ParserContract.VERSION);

        if (rssVersion == null)
        {
            Logger.e(TAG, "getRSSFormatParser(): rssVersion is null");
            throw new UnsupportedOperationException();
        }

        if (rssVersion.equalsIgnoreCase(ParserContract.RSS_VERSION_2_0) ||
                rssVersion.equalsIgnoreCase(ParserContract.RSS_VERSION_0_9_1))
        {

            return new RSS20Parser();
        } else
        {
            Logger.e(TAG, "getRSSFormatParser(): Not supported rssVersion = " + rssVersion);
            throw new UnsupportedOperationException();
        }
    }

    public ArrayList<Entry> parse(final InputStream in, final long channelId)
            throws XmlPullParserException,
                   IOException,
                   UnsupportedOperationException
    {

        final XmlPullParser parser = getParser(in);
        final FormatParser formatParser = getFormatParser(parser);
        return formatParser.parse(parser, channelId);
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
        return parser;
    }

    private FormatParser getFormatParser(final XmlPullParser parser)
    {
        final String tagName = parser.getName();

        if (tagName.equalsIgnoreCase(ParserContract.RSS))
        {
            return getRSSFormatParser(parser);
        } else if (tagName.equalsIgnoreCase(ParserContract.FEED))
        {
            return getAtomFormatParser(parser);
        } else
        {
            Logger.e(TAG, "getFormatParser(): Not supported format = " + tagName);
            throw new UnsupportedOperationException();
        }
    }
}
