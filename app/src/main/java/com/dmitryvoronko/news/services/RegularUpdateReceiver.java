package com.dmitryvoronko.news.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dmitryvoronko.news.view.util.NotificationHelper;

public final class RegularUpdateReceiver extends BroadcastReceiver
{
    public static final String ACTION_REGULAR_UPDATE_SUCCEED =
            "com.dmitryvoronko.news.services.action.REGULAR_UPDATE_SUCCEED";
    public static final String ACTION_REGULAR_UPDATE_INITIATE =
            "com.dmitryvoronko.news.services.action.REGULAR_UPDATE_INITIATE";

    public static final int REQUEST_CODE = 222;

    public RegularUpdateReceiver()
    {

    }

    @Override
    public void onReceive(final Context context,
                          final Intent intent)
    {
        if (intent != null)
        {
            if (intent.getAction().equalsIgnoreCase(ACTION_REGULAR_UPDATE_SUCCEED))
            {
                final SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                NotificationHelper.showNotification(context, sharedPreferences);
            } else if (intent.getAction().equalsIgnoreCase(ACTION_REGULAR_UPDATE_INITIATE))
            {
                ChannelsContentService.startActionRegularUpdate(context);
            }
        }
    }
}
