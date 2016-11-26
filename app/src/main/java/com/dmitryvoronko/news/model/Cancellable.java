package com.dmitryvoronko.news.model;

/**
 *
 * Created by Dmitry on 17/11/2016.
 */

public abstract class Cancellable
{
    private boolean canceled = false;

    protected final void cancel()
    {
        canceled = true;
    }

    protected boolean isCanceled()
    {
        return canceled;
    }
}
