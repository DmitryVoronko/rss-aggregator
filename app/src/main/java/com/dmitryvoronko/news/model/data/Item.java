package com.dmitryvoronko.news.model.data;

import lombok.Data;

/**
 * Created by Dmitry on 22/10/2016.
 */
@Data class Item
{
    private final String title;
    private final String description;
    private final String link;
    private int id;

    Item(final String title, final String description, final String link)
    {
        this.title = title;
        this.description = description;
        this.link = link;
    }
}
