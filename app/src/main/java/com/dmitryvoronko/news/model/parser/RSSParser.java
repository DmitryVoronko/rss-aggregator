package com.dmitryvoronko.news.model.parser;

import android.support.annotation.Nullable;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;

/**
 * Created by Dmitry on 31/10/2016.
 */

public final class RSSParser extends BaseParser
{
    private static final String TAG = "RSS_PARSER";

    public RSSParser()
    {

    }

    private void parse(final InputStream inputStream)
    {
//        try
//        {
//            final XmlPullParser parser = getParser();
//
//        }
    }

    @Nullable private XmlPullParser getParser()
    {
        try
        {
            final XmlPullParserFactory factory;
            factory = XmlPullParserFactory.newInstance();
            final XmlPullParser parser = factory.newPullParser();
            return parser;
        } catch (XmlPullParserException e)
        {
            Log.d(TAG, "getParser: Xml Pull Parser Exception");
        }
        return null;
    }

}
