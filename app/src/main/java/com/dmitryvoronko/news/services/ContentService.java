package com.dmitryvoronko.news.services;

import com.dmitryvoronko.news.model.data.Channel;

import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * Created by Dmitry on 17/11/2016.
 */

public interface ContentService
{

    String ACTION_NO_INTERNET_CONNECTION =
            "com.dmitryvoronko.news.view.content.action.NO_INTERNET_CONNECTION";
    String ACTION_CONTENT_READY =
                    "com.dmitryvoronko.news.view.content.action.CONTENT_CHANGED";

    ArrayList<Channel> getContent();

    Stack<ItemToBeDeleted> getDeletedItems();
}
