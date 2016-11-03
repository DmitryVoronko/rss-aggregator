package com.dmitryvoronko.news.model.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by Dmitry on 21/10/2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public final class Channel extends Item
{
    public Channel(final String title,
                   final String description,
                   final String link,
                   final String pubDate)
    {
        super(title, description, link, pubDate);
    }
}
