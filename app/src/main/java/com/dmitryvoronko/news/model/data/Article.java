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
    private final String pubDate;
    private State state = State.IS_NOT_READ;

    public Article(final String title,
                   final String description,
                   final String link,
                   final int channelId,
                   final String pubDate)
    {
        super(title, description, link);
        this.channelId = channelId;
        this.pubDate = pubDate;
    }

    public enum State
    {
        WAS_READ("WAS_READ"),
        IS_NOT_READ("IS_NOT_READ");

        private final String name;

        private State(final String name)
        {
            this.name = name;
        }
    }
}
