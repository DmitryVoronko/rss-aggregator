package com.dmitryvoronko.news.model.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by Dmitry on 21/10/2016.
 */
@Data
class FeedObject implements Channel
{
    private final int id;
    private final String title;
    private final String link;
    private final String description;
    private final String pubDate;
    private final State state;
}
