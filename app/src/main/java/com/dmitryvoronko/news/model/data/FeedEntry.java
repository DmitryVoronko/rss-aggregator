package com.dmitryvoronko.news.model.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * Created by Dmitry on 19/10/2016.
 */

@Data
@EqualsAndHashCode(callSuper = true)
final class FeedEntry extends FeedObject implements Entry
{
    private final long channelId;

    FeedEntry(final long id,
              final String title,
              final String link,
              final String description,
              final long channelId)
    {
        super(id, title, link, description);
        this.channelId = channelId;
    }
}
