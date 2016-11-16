package com.dmitryvoronko.news.model.data;

import lombok.Data;

/**
 *
 * Created by Dmitry on 21/10/2016.
 */
@Data class FeedObject implements Channel
{
    private final long id;
    private final String title;
    private final String link;
    private final String description;
    private final State state;
}
