package com.dmitryvoronko.news.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.dmitryvoronko.news.model.NewsFacade;
import com.dmitryvoronko.news.model.data.Channel;

import java.util.ArrayList;

// FIXME: 03/11/2016
public final class MainService extends Service
{
    private static final String PAGE_LIMIT = "20";


    private final ArrayList<Channel> data = new ArrayList<>();
    private final NewsFacade newsFacade;

    public MainService()
    {
        this.newsFacade = new NewsFacade(this);
    }

    @Override public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public IBinder onBind(final Intent intent)
    {
        return null;
    }

    private boolean moreChanneExists()
    {
        final int startId = data.size();
        final ArrayList<Channel> next20Channels = newsFacade.getChannels(startId, PAGE_LIMIT);
        if (next20Channels != null)
        {
            if (next20Channels.get(0) != null)
            {
                for (final Channel channel : next20Channels)
                {
                    data.add(channel);
                }
                return true;
            }
            return false;
        }
        return false;
    }

}
