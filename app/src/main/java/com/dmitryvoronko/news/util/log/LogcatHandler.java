package com.dmitryvoronko.news.util.log;

import android.util.Log;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 *
 * Created by Dmitry on 11/11/2016.
 */

final class LogcatHandler extends Handler
{

    @Override public void publish(final LogRecord record)
    {
        if (null == record)
        {
            return;
        }

        final String message = (null == record.getMessage() ? "" : record.getMessage());

        if (Level.SEVERE == record.getLevel())
        {
            Log.e(record.getLoggerName(), message);
        } else if (Level.WARNING == record.getLevel())
        {
            Log.w(record.getLoggerName(), message);
        } else
        {
            Log.i(record.getLoggerName(), message);
        }
    }

    @Override public void flush()
    {
        // nothing to close
    }

    @Override public void close() throws SecurityException
    {
        // nothing to flush
    }
}
