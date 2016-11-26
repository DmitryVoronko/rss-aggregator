package com.dmitryvoronko.news.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.dmitryvoronko.news.model.NewsFacade;
import com.dmitryvoronko.news.model.UpdateStatus;
import com.dmitryvoronko.news.model.data.Channel;
import com.dmitryvoronko.news.util.NetworkHelper;

import java.util.ArrayList;
import java.util.Stack;

import lombok.NonNull;

import static com.dmitryvoronko.news.services.ChannelsContentService.ACTION_REGULAR_UPDATE_CONTENT;


abstract class ContentServiceBase extends IntentService implements ContentService
{
    static final String ACTION_GET_CONTENT =
            "com.dmitryvoronko.news.services.action.GET_CONTENT";
    static final String ACTION_UPDATE_CONTENT =
            "com.dmitryvoronko.news.services.action.UPDATE_CONTENT";

    final ArrayList<Channel> content = new ArrayList<>();
    final NewsFacade newsFacade;
    private final Stack<ItemToBeDeleted> deletedItems = new Stack<>();
    private final IBinder binder = createBinder();

    protected abstract IBinder createBinder();

    public ContentServiceBase()
    {
        super("ContentServiceBase");
        newsFacade = new NewsFacade(this);
    }

    public ArrayList<Channel> getContent()
    {
        return content;
    }

    public Stack<ItemToBeDeleted> getDeletedItems()
    {
        return deletedItems;
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
            deleteItem(itemId);
        }
    }

    protected abstract void deleteItem(final long itemId);

    @Nullable @Override public IBinder onBind(@NonNull final Intent intent)
    {
        return binder;
    }

    @Override
    protected final void onHandleIntent(final Intent intent)
    {
        if (intent != null)
        {
            final String action = intent.getAction();
            if (ACTION_GET_CONTENT.equals(action))
            {
                handleActionGetContent(intent);
            } else if (ACTION_UPDATE_CONTENT.equals(action))
            {
                final boolean hasConnection = NetworkHelper.hasConnection(this);
                if (hasConnection)
                {
                    handleActionUpdateContent(intent);
                } else
                {
                    sendBroadcast(ACTION_NO_INTERNET_CONNECTION);
                }
            } else if (ACTION_REGULAR_UPDATE_CONTENT.equals(action))
            {
                final boolean hasConnection = NetworkHelper.hasConnection(this);
                if (hasConnection)
                {
                    handleActionRegularUpdate();
                } else
                {
                    tryAnotherTime();
                }
            }
        }
    }

    private void tryAnotherTime()
    {
        RegularUpdateScheduler.anotherTryScheduleAlarm(getApplicationContext());
    }

    private void handleActionRegularUpdate()
    {
        final UpdateStatus updateStatus = newsFacade.updateChannels();
        if (updateStatus.equals(UpdateStatus.UPDATED))
        {
            final Intent succeedIntent = new Intent(this, RegularUpdateReceiver.class);
            succeedIntent.setAction(RegularUpdateReceiver.ACTION_REGULAR_UPDATE_SUCCEED);
            sendBroadcast(succeedIntent);
        } else
        {
            tryAnotherTime();
        }
    }

    protected abstract void handleActionGetContent(final Intent intent);

    protected abstract void handleActionUpdateContent(final Intent intent);


    final void sendBroadcast(@NonNull final String action)
    {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

}
