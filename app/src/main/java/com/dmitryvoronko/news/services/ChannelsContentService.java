package com.dmitryvoronko.news.services;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.dmitryvoronko.news.model.UpdateStatus;
import com.dmitryvoronko.news.util.log.Logger;

import lombok.NonNull;

/**
 *
 * Created by Dmitry on 17/11/2016.
 */

public final class ChannelsContentService extends ContentServiceBase
{
    private static final String TAG = "ChannelsContentService";

    public static void startActionGetContent(@NonNull final Context context)
    {
        startContentAction(context, ACTION_GET_CONTENT);
    }

    private static void startContentAction(@NonNull final Context context,
                                           @NonNull final String action)
    {
        final Intent intent = new Intent(context, ChannelsContentService.class);
        intent.setAction(action);
        context.startService(intent);
    }

    public static void startActionUpdateContent(@NonNull final Context context)
    {
        startContentAction(context, ACTION_UPDATE_CONTENT);
    }

    @Override protected IBinder createBinder()
    {
        return new Binder();
    }

    @Override protected void deleteItem(final long itemId)
    {
        newsFacade.deleteChannel(itemId);
    }

    @Override protected void handleActionGetContent(final Intent intent)
    {
        handleActionGetChannels();
    }

    @Override protected void handleActionUpdateContent(final Intent intent)
    {
        handleActionUpdateChannels();
    }

    private void handleActionUpdateChannels()
    {
        final UpdateStatus updateStatus = newsFacade.updateChannels();
        if (updateStatus.equals(UpdateStatus.CANCELED))
        {
            Logger.i(TAG, "handleActionUpdateChannels: Status equals 'CANCELED'");
            stopSelf();
        } else if (updateStatus.equals(UpdateStatus.UPDATED))
        {
            handleActionGetChannels();
        } else
        {
            Logger.e(TAG, "handleActionUpdateChannels: Status is not recognized");
        }
    }

    private void handleActionGetChannels()
    {
        content.clear();
        content.addAll(newsFacade.getChannels());
        sendBroadcast(ACTION_CONTENT_READY);
    }

    public final class Binder extends android.os.Binder
    {
        public ChannelsContentService getService()
        {
            return ChannelsContentService.this;
        }
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
        Logger.i(TAG, "onDestroy()");
    }

    @Override public void onCreate()
    {
        super.onCreate();
        Logger.i(TAG, "onCreate()");
    }

    @Nullable @Override public IBinder onBind(@NonNull final Intent intent)
    {
        Logger.i(TAG, "onBind() Intent = " + intent);
        return super.onBind(intent);
    }

    @Override public void onRebind(final Intent intent)
    {
        super.onRebind(intent);
        Logger.i(TAG, "onRebind() Intent = " + intent);
    }

    @Override public boolean onUnbind(@NonNull final Intent intent)
    {
        Logger.i(TAG, "onUnbind() Intent = " + intent);
        return super.onUnbind(intent);
    }
}
