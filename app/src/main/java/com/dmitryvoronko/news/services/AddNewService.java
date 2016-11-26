package com.dmitryvoronko.news.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.dmitryvoronko.news.model.NewsFacade;
import com.dmitryvoronko.news.model.userinput.Status;

import lombok.NonNull;

public final class AddNewService extends IntentService
{
    public static final String ACTION_ADD_NEW_CHANNEL_STATUS =
            "com.dmitryvoronko.news.services.action.ADD_NEW_CHANNEL_STATUS";
    public static final String EXTRA_ADD_NEW_CHANNEL_STATUS =
            "com.dmitryvoronko.news.services.extra.ADD_NEW_CHANNEL_STATUS";
    private static final String TAG = "AddNewService";
    private static final String ACTION_ADD_NEW_CHANNEL =
            "com.dmitryvoronko.news.services.action.ADD_NEW_CHANNEL";
    private static final String EXTRA_NEW_CHANNEL_LINK =
            "com.dmitryvoronko.news.services.extra.NEW_CHANNEL_LINK";

    private final NewsFacade newsFacade;
    private final IBinder binder = new Binder();

    public AddNewService()
    {
        super(TAG);
        this.newsFacade = new NewsFacade(this);
    }

    public static void startActionAddNewChannel(final Context context,
                                                final String newChannelLink)
    {
        final Intent intent = new Intent(context, AddNewService.class);
        intent.setAction(ACTION_ADD_NEW_CHANNEL);
        intent.putExtra(EXTRA_NEW_CHANNEL_LINK, newChannelLink);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(final Intent intent)
    {
        if (intent != null)
        {
            final String action = intent.getAction();
            if (ACTION_ADD_NEW_CHANNEL.equals(action))
            {
                final String newChannelLink = intent.getStringExtra(EXTRA_NEW_CHANNEL_LINK);
                handleActionAddNewChannel(newChannelLink);
            }
        }
    }

    private void handleActionAddNewChannel(final String newChannelLink)
    {
        final Status status = newsFacade.requestAddNewChannel(newChannelLink);
        final Intent intent = new Intent(ACTION_ADD_NEW_CHANNEL_STATUS);
        intent.putExtra(EXTRA_ADD_NEW_CHANNEL_STATUS, status.name());
        sendBroadcast(intent);
    }

    public void cancelAddNewChannel()
    {
        newsFacade.cancelAddNewChannel();
    }

    public final class Binder extends android.os.Binder
    {
        public AddNewService getService()
        {
            return AddNewService.this;
        }
    }

    @Nullable @Override public IBinder onBind(@NonNull final Intent intent)
    {
        return binder;
    }



}
