package com.dmitryvoronko.news.model.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by Dmitry on 19/10/2016.
 */

@Data
@EqualsAndHashCode(callSuper = true)
public final class Article extends Item
{
    private final int channelId;

    public Article(final String title,
                   final String description,
                   final String link,
                   final int channelId,
                   final String pubDate)
    {
        super(title, description, link, pubDate);
        this.channelId = channelId;
    }
}
