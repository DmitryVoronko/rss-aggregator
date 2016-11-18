package com.dmitryvoronko.news.util.parser;

import android.support.annotation.NonNull;

import com.dmitryvoronko.news.util.log.Logger;

import org.xmlpull.v1.XmlPullParser;


/**
 *
 * Created by Dmitry on 17/11/2016.
 */

final class AtomParser extends FormatParser
{
    private static final String TAG = "AtomParser";

    @NonNull @Override protected String getChannelDescriptionTagName()
    {
        return ParserContract.SUBTITLE;
    }

    @NonNull @Override protected String getChannelTitleTagName()
    {
        return ParserContract.TITLE;
    }

    @NonNull @Override protected String getEntryTagName()
    {
        return ParserContract.ENTRY;
    }

    @NonNull @Override protected String parseLink(final XmlPullParser parser, final String link)
    {
        return getLink(parser, link);
    }

    private String getLink(final XmlPullParser parser, String link)
    {
        if (link == null)
        {
            final String rel = parser.getAttributeValue(null, ParserContract.REL);
            if (rel == null)
            {
                link = parser.getAttributeValue(null, ParserContract.HREF);
                if (link == null)
                {
                    Logger.e(TAG, "getLink(): Rel is null. Link is null.");
                }
            } else
            {
                if (rel.equalsIgnoreCase(ParserContract.SELF))
                {
                    link = parser.getAttributeValue(null, ParserContract.HREF);
                } else if (rel.equalsIgnoreCase(ParserContract.ALTERNATE))
                {
                    link = parser.getAttributeValue(null, ParserContract.HREF);
                } else
                {
                    Logger.w(TAG, "getLink(): Unsupported rel type " + rel);
                }
            }
            return link;
        } else
        {
            return link;
        }
    }

    @NonNull @Override protected String getChannelTagName()
    {
        return ParserContract.FEED;
    }

    @NonNull @Override protected String getEntryDescriptionTagName()
    {
        return ParserContract.SUMMARY;
    }

    @NonNull @Override protected String getEntryTitleTagName()
    {
        return ParserContract.TITLE;
    }
}
