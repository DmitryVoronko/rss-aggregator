package com.dmitryvoronko.news.model.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

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
              @NonNull final String title,
              @NonNull final String link,
              @NonNull final String description,
              final long channelId)
    {
        super(id, title, link, description);
        this.channelId = channelId;
    }
}
