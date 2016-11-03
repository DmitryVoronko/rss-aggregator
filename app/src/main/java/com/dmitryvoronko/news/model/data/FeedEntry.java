package com.dmitryvoronko.news.model.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by Dmitry on 19/10/2016.
 */

@Data
@EqualsAndHashCode(callSuper = true)
final class FeedEntry extends FeedObject implements Entry
{
    private final int channelId;

    FeedEntry(final int id, final String title, final String link, final String description,
                      final String pubDate,
                      final State state,
                      final int channelId)
    {
        super(id, title, link, description, pubDate, state);
        this.channelId = channelId;
    }
}
