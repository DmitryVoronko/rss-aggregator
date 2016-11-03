package com.dmitryvoronko.news.model.data;

/**
 * Created by Dmitry on 03/11/2016.
 */

public interface Channel
{
    int getId();
    String getTitle();
    String getLink();
    String getDescription();
    String getPubDate();
    State getState();
}
