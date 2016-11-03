package com.dmitryvoronko.news.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.dmitryvoronko.news.model.News;

public final class MainService extends Service
{
    private final News news;

    public MainService()
    {
        this.news = new News(this);
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



}
