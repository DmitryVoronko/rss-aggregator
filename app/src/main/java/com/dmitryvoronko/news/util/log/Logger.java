package com.dmitryvoronko.news.util.log;

import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 *
 * Created by Dmitry on 17/11/2016.
 */

public final class Logger
{
    private Logger()
    {
        throw new UnsupportedOperationException();
    }

    public static void e(final String tag, final String message, final Throwable thrown)
    {
        log(tag, message, thrown, Level.SEVERE);
    }

    public static void e(final String tag, final String message)
    {
        log(tag, message, Level.SEVERE);
    }

    public static void i(final String tag, final String message)
    {
        log(tag, message, Level.INFO);
    }


    public static void w(final String tag, final String message)
    {
        log(tag, message, Level.WARNING);
    }

    private static void log(final String tag, final String message, final Level level)
    {
        log(tag, message, null, level);
    }

    private static void log(final String tag, final String message, final Throwable thrown,
                        final Level level)
    {
        final LogcatHandler logcatHandler = new LogcatHandler();
        final LogRecord logRecord = new LogRecord(level, message);
        logRecord.setLoggerName(tag);
        if (thrown != null)
        {
            logRecord.setThrown(thrown);
        }
        logcatHandler.publish(logRecord);
    }

}
