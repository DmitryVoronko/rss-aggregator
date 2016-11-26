package com.dmitryvoronko.news.model.userinput;

/**
 *
 * Created by Dmitry on 01/11/2016.
 */

public enum Status
{
    NOTHING,
    ADDED,
    NOT_URL,
    NOT_XML,
    TOTAL_ERROR,
    ALREADY_EXISTS,
    UNSUPPORTED_FORMAT,
    NO_INTERNET_CONNECTION,
    CANCELED
}
