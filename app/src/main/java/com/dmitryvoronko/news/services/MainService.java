package com.dmitryvoronko.news.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.dmitryvoronko.news.model.NewsFacade;
import com.dmitryvoronko.news.model.userinput.Status;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public final class MainService extends IntentService
{
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String EXTRA_ADD_NEW_CHANNEL_STATUS =
            "com.dmitryvoronko.news.services.extra.EXTRA_ADD_NEW_CHANNEL_STATUS";
    public static final String ACTION_ADD_NEW_CHANNEL_STATUS =
            "com.dmitryvoronko.news.services.action.ADD_NEW_CHANNEL_STATUS";


    private static final String ACTION_ADD_NEW_CHANNEL =
            "com.dmitryvoronko.news.services.action.ADD_NEW_CHANNEL";
    private static final String ACTION_BAZ = "com.dmitryvoronko.news.services.action.BAZ";

    // TODO: Rename parameters
    private static final String NEW_CHANNEL_LINK =
            "com.dmitryvoronko.news.services.extra.NEW_CHANNEL_LINK";
    private static final String EXTRA_PARAM2 = "com.dmitryvoronko.news.services.extra.PARAM2";

    private NewsFacade newsFacade;

    public MainService()
    {
        super("MainService");
    }

    @Override public void onCreate()
    {
        super.onCreate();
        newsFacade = new NewsFacade(this);
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionAddNewChannel(final Context context,
                                                final String newChannelLink)
    {
        Intent intent = new Intent(context, MainService.class);
        intent.setAction(ACTION_ADD_NEW_CHANNEL);
        intent.putExtra(NEW_CHANNEL_LINK, newChannelLink);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(final Context context,
                                      final String param1,
                                      final String param2)
    {
        Intent intent = new Intent(context, MainService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(NEW_CHANNEL_LINK, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
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
                final String param1 = intent.getStringExtra(NEW_CHANNEL_LINK);
                handleActionAddNewChannel(param1);
            } else if (ACTION_BAZ.equals(action))
            {
                final String param1 = intent.getStringExtra(NEW_CHANNEL_LINK);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
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

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(final String param1, final String param2)
    {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }


//    private static final String PAGE_LIMIT = "20";
//
//    private final ArrayList<Channel> data = new ArrayList<>();
//    private final NewsFacade newsFacade;
//
//    private MainService(final String name, final NewsFacade newsFacade)
//    {
//        super(name);
//        this.newsFacade = newsFacade;
//    }
//
//    private boolean moreChanneExists()
//    {
//        final int startId = data.size();
//        final ArrayList<Channel> next20Channels = newsFacade.getChannels(startId, PAGE_LIMIT);
//        if (next20Channels != null)
//        {
//            if (next20Channels.get(0) != null)
//            {
//                for (final Channel channel : next20Channels)
//                {
//                    data.add(channel);
//                }
//                return true;
//            }
//            return false;
//        }
//        return false;
//    }
}
