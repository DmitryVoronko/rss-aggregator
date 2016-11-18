package com.dmitryvoronko.news.model;

/**
 *
 * Created by Dmitry on 17/11/2016.
 */

abstract class Cancellable
{
    boolean canceled = false;

    public final void cancel()
    {
        canceled = true;
    }
}
