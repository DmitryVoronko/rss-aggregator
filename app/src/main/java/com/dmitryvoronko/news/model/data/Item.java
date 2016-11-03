package com.dmitryvoronko.news.model.data;

import lombok.Data;

/**
 * Created by Dmitry on 22/10/2016.
 */
@Data public class Item
{
    private final String title;
    private final String description;
    private final String pubDate;

    private String link;
    private int id;
    private State state = State.IS_NOT_READ;

    public Item(final String title, final String description, final String pubDate)
    {
        this.title = title;
        this.description = description;
        this.pubDate = pubDate;
    }

    public Item(final String title, final String description,
                final String link, final String pubDate)
    {
        this.title = title;
        this.description = description;
        this.pubDate = pubDate;
        this.link = link;
    }

    private enum State
    {
        WAS_READ,
        IS_NOT_READ
    }
}
