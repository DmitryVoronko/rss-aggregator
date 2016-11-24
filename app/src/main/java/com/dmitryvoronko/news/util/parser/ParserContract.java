package com.dmitryvoronko.news.util.parser;

/**
 *
 * Created by Dmitry on 31/10/2016.
 */

final class ParserContract
{
    static final String CHANNEL = "channel";
    static final String ITEM = "item";
    static final String LINK = "link";
    static final String DESCRIPTION = "description";
    static final String TITLE = "title";
    static final String RSS_VERSION_2_0 = "2.0";
    static final String RSS_VERSION_0_9_1 = "0.91";
    static final String RSS = "rss";
    static final String VERSION = "version";
    static final String HREF = "href";
    static final String REL = "rel";
    static final String SELF = "self";
    static final String XMLNS = "xmlns";
    static final String ATOM_XMLNS = "http://www.w3.org/2005/Atom";
    static final String ALTERNATE = "alternate";
    static final String SUBTITLE = "subtitle";
    static final String SUMMARY = "summary";
    static final String ENTRY = "entry";
    static final String FEED = "feed";
    static final String HTML = "html";

    private ParserContract()
    {
        throw new UnsupportedOperationException();
    }
}
