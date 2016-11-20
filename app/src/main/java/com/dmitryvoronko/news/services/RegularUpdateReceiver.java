package com.dmitryvoronko.news.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dmitryvoronko.news.view.util.NotificationHelper;

public final class RegularUpdateReceiver extends BroadcastReceiver
{
    private static final String ACTION_REGULAR_UPDATE_SUCCESS =
            "com.dmitryvoronko.news.services.action.REGULAR_UPDATE_SUCCESS";

    public RegularUpdateReceiver()
    {
    }

    @Override
    public void onReceive(final Context context,
                          final Intent intent)
    {
        if (intent != null)
        {
            if (intent.getAction().equalsIgnoreCase(ACTION_REGULAR_UPDATE_SUCCESS))
            {
                final SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                NotificationHelper.showNotification(context, sharedPreferences);
            }
        }
    }
}
