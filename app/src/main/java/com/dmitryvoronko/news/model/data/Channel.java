package com.dmitryvoronko.news.model.data;

/**
 *
 * Created by Dmitry on 03/11/2016.
 */

public interface Channel
{
    long getId();

    String getTitle();

    String getLink();

    String getDescription();

    State getState();
}
