package com.dmitryvoronko.news.services;

import com.dmitryvoronko.news.model.data.Channel;

import lombok.Data;

/**
 *
 * Created by Dmitry on 11/11/2016.
 */
@Data
public final class ItemToBeDeleted
{
    private final Channel item;
    private final int position;
}
