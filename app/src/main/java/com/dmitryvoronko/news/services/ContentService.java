package com.dmitryvoronko.news.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dmitryvoronko.news.model.NewsFacade;
import com.dmitryvoronko.news.model.data.Channel;
import com.dmitryvoronko.news.util.NetworkHelper;
import com.dmitryvoronko.news.view.content.EntriesActivity;

import java.util.ArrayList;
import java.util.Stack;

import lombok.NonNull;

public final class ContentService extends IntentService
{
    private static final String TAG = "ContentService";

    private static final String ACTION_GET_CONTENT =
            "com.dmitryvoronko.news.services.action.GET_CONTENT";
    private static final String ACTION_UPDATE_CONTENT =
            "com.dmitryvoronko.news.services.action.UPDATE_CONTENT";
    public static final String ACTION_NO_INTERNET_CONNECTION =
            "com.dmitryvoronko.news.view.content.action.NO_INTERNET_CONNECTION";
    public static final String ACTION_CONTENT_READY =
            "com.dmitryvoronko.news.view.content.action.CONTENT_CHANGED";

    public static final String EXTRA_CHANNEL_ID =
            "com.dmitryvoronko.news.services.extra.CHANNEL_ID";
    public static final String EXTRA_CONTENT_TYPE =
            "com.dmitryvoronko.news.services.extra.CONTENT_TYPE";

    private final ArrayList<Channel> content = new ArrayList<>();
    private final Stack<ItemToBeDeleted> deletedItems = new Stack<>();
    private final IBinder binder = new Binder();
    private final NewsFacade newsFacade;

    private ContentType contentType;

    public ContentService()
    {
        super("ContentService");
        newsFacade = new NewsFacade(this);
    }

    public static void startActionGetContent(@NonNull final Context context)
    {
        startContentAction(context, ACTION_GET_CONTENT);
    }

    private static void startContentAction(@NonNull final Context context,
                                           @NonNull final String action)
    {
        final Intent intent = new Intent(context, ContentService.class);
        intent.setAction(action);
        context.startService(intent);
    }

    public static void startActionUpdateContent(@NonNull final Context context)
    {
        startContentAction(context, ACTION_UPDATE_CONTENT);
    }

    public static void startActionGetContent(@NonNull final Context context,
                                             @NonNull final long channelId)
    {
        startContentAction(context, channelId, ACTION_GET_CONTENT);
    }

    private static void startContentAction(@NonNull final Context context,
                                           final long channelId,
                                           @NonNull final String action)
    {
        final Intent intent = new Intent(context, ContentService.class);
        intent.setAction(action);
        intent.putExtra(EXTRA_CHANNEL_ID, channelId);
        context.startService(intent);
    }

    public static void startActionUpdateContent(@NonNull final Context context,
                                                final long channelId)
    {
        startContentAction(context, channelId, ACTION_UPDATE_CONTENT);
    }

    public ArrayList<Channel> getContent()
    {
        return content;
    }

    public Stack<ItemToBeDeleted> getDeletedItems()
    {
        return deletedItems;
    }

    @Nullable @Override public IBinder onBind(@NonNull final Intent intent)
    {
        final String stringContentType = intent.getStringExtra(EXTRA_CONTENT_TYPE);
        contentType = ContentType.valueOf(stringContentType);
        return binder;
    }

    @Override
    protected final void onHandleIntent(final Intent intent)
    {
        if (intent != null)
        {
            final String action = intent.getAction();
            if (ACTION_GET_CONTENT.equals(action) || ACTION_UPDATE_CONTENT.equals(action))
            {
                final long channelId = intent.getLongExtra(EXTRA_CHANNEL_ID,
                                                           EntriesActivity.DEFAULT_CHANNEL_ID);
                handleContentAction(action, channelId);
            }
        }
    }

    private void handleContentAction(@NonNull final String action,
                                     final long channelId)
    {
        final String resultAction;

        if (action.equalsIgnoreCase(ACTION_GET_CONTENT))
        {
            handleActionGetContent(channelId);
            resultAction = ACTION_CONTENT_READY;
        } else if (action.equalsIgnoreCase(ACTION_UPDATE_CONTENT))
        {
            final boolean hasConnection = NetworkHelper.hasConnection(this);
            if (hasConnection)
            {
                handleActionUpdateContent(channelId);
                handleActionGetContent(channelId);
                resultAction = ACTION_CONTENT_READY;
            } else
            {
                resultAction = ACTION_NO_INTERNET_CONNECTION;
            }
        } else
        {
            throw new UnsupportedOperationException();
        }

        sendBroadcast(resultAction);
    }

    private void handleActionGetContent(final long channelId)
    {
        content.clear();
        if (contentType.equals(ContentType.CHANNEL))
        {
            content.addAll(newsFacade.getChannels());
        } else if (contentType.equals(ContentType.ENTRY))
        {
            content.addAll(newsFacade.getEntries(channelId));
        } else
        {
            throw new UnsupportedOperationException();
        }
    }

    private void handleActionUpdateContent(final long channelId)
    {
        if (contentType.equals(ContentType.CHANNEL))
        {
            newsFacade.updateChannels();
        } else if (contentType.equals(ContentType.ENTRY))
        {
            newsFacade.updateEntries(channelId);
        } else
        {
            throw new UnsupportedOperationException();
        }
    }

    private void sendBroadcast(@NonNull final String action)
    {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    @Override public boolean onUnbind(@NonNull final Intent intent)
    {
        deleteCachedItems();
        newsFacade.cancelUpdate();
        return super.onUnbind(intent);
    }

    private void deleteCachedItems()
    {
        for (final ItemToBeDeleted itemToBeDeleted : deletedItems)
        {
            final Channel channel = itemToBeDeleted.getItem();
            final long itemId = channel.getId();
            if (contentType.equals(ContentType.CHANNEL))
            {
                newsFacade.deleteChannel(itemId);
            } else if (contentType.equals(ContentType.ENTRY))
            {
                newsFacade.deleteEntry(itemId);
            } else
            {
                throw new UnsupportedOperationException();
            }
        }
    }

    public final class Binder extends android.os.Binder
    {
        public ContentService getService()
        {
            return ContentService.this;
        }
    }

    @Override public void onCreate()
    {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }
}
