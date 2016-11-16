package com.dmitryvoronko.news.util.parser;

/**
 *
 * Created by Dmitry on 31/10/2016.
 */

final class ParserContract
{
    static final String RSS_VERSION_2_0 = "2.0";
    static final String RSS_VERSION_0_9_2 = "0.92";
    static final String RSS_VERSION_0_9_1 = "0.91";
    static final String RSS = "rss";
    static final String VERSION = "version";

    static final String DESCRIPTION = "description";
    static final String LINK = "link";
    static final String TITLE = "title";
    static final String ITEM = "item";
    static final String CHANNEL = "channel";
    static final String HREF = "href";

    private ParserContract()
    {
        throw new UnsupportedOperationException();
    }
}
