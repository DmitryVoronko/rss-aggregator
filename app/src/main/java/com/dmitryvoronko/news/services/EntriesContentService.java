package com.dmitryvoronko.news.services;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dmitryvoronko.news.model.UpdateStatus;
import com.dmitryvoronko.news.util.log.Logger;

import lombok.NonNull;

import static com.dmitryvoronko.news.view.content.EntriesActivity.DEFAULT_CHANNEL_ID;

/**
 *
 * Created by Dmitry on 17/11/2016.
 */

public final class EntriesContentService extends ContentServiceBase
{
    public static final String EXTRA_CHANNEL_ID =
            "com.dmitryvoronko.news.services.extra.CHANNEL_ID";
    private static final String TAG = "EntriesContentService";

    public static void startActionGetContent(@NonNull final Context context,
                                             @NonNull final long channelId)
    {
        startContentAction(context, channelId, ACTION_GET_CONTENT);
    }

    private static void startContentAction(@NonNull final Context context,
                                           final long channelId,
                                           @NonNull final String action)
    {
        final Intent intent = new Intent(context, EntriesContentService.class);
        intent.setAction(action);
        intent.putExtra(EXTRA_CHANNEL_ID, channelId);
        context.startService(intent);
    }

    public static void startActionUpdateContent(@NonNull final Context context,
                                                final long channelId)
    {
        startContentAction(context, channelId, ACTION_UPDATE_CONTENT);
    }

    @Override protected IBinder createBinder()
    {
        return new Binder();
    }

    @Override protected void deleteItem(final long itemId)
    {
        newsFacade.deleteEntry(itemId);
    }

    @Override protected void handleActionGetContent(final Intent intent)
    {
        final long channelId = getChannelId(intent);
        handleActionGetEntries(channelId);
    }

    @Override protected void handleActionUpdateContent(final Intent intent)
    {
        final long channelId = getChannelId(intent);
        handleActionUpdateEntries(channelId);
    }

    private void handleActionUpdateEntries(final long channelId)
    {
        final UpdateStatus updateStatus = newsFacade.updateEntries(channelId);
        if (updateStatus.equals(UpdateStatus.CANCELED))
        {
            Log.d(TAG, "handleActionUpdateEntries: Status equals 'CANCELED'");
        } else if (updateStatus.equals(UpdateStatus.UPDATED))
        {
            handleActionGetEntries(channelId);
        } else
        {
            Log.d(TAG, "handleActionUpdateEntries: Status is not recognized");
        }
    }

    private long getChannelId(final Intent intent)
    {
        return intent.getLongExtra(EXTRA_CHANNEL_ID, DEFAULT_CHANNEL_ID);
    }

    private void handleActionGetEntries(final long channelId)
    {
        content.clear();
        content.addAll(newsFacade.getEntries(channelId));
        sendBroadcast(ACTION_CONTENT_READY);
    }

    public final class Binder extends android.os.Binder
    {
        public EntriesContentService getService()
        {
            return EntriesContentService.this;
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
