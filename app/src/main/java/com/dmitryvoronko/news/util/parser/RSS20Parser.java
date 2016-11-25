package com.dmitryvoronko.news.util.parser;

import android.support.annotation.NonNull;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 *
 * Created by Dmitry on 18/11/2016.
 */

final class RSS20Parser extends FormatParser
{
    @NonNull @Override protected String getChannelDescriptionTagName()
    {
        return ParserContract.DESCRIPTION;
    }

    @NonNull @Override protected String getChannelTitleTagName()
    {
        return ParserContract.TITLE;
    }

    @NonNull @Override protected String getEntryTagName()
    {
        return ParserContract.ITEM;
    }

    @NonNull @Override protected String parseLink(final XmlPullParser parser, final String link)
            throws IOException, XmlPullParserException
    {
        return parser.nextText();
    }

    @NonNull @Override protected String getChannelTagName()
    {
        return ParserContract.CHANNEL;
    }

    @NonNull @Override protected String getEntryDescriptionTagName()
    {
        return ParserContract.DESCRIPTION;
    }

    @NonNull @Override protected String getEntryTitleTagName()
    {
        return ParserContract.TITLE;
    }
}
