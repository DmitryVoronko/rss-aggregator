package com.dmitryvoronko.news.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dmitryvoronko.news.R;
import com.dmitryvoronko.news.ui.util.NotificationHelper;

public final class RegularUpdateReceiver extends BroadcastReceiver
{
    public static final String ACTION_REGULAR_UPDATE_SUCCEED =
            "com.dmitryvoronko.news.services.action.REGULAR_UPDATE_SUCCEED";
    public static final String ACTION_REGULAR_UPDATE_INITIATE =
            "com.dmitryvoronko.news.services.action.REGULAR_UPDATE_INITIATE";

    public static final int STANDARD_REQUEST_CODE = 222;
    public static final int ANOTHER_TRY_REQUEST_CODE = 347;

    @Override
    public void onReceive(final Context context,
                          final Intent intent)
    {
        if (intent != null)
        {
            if (intent.getAction().equals(ACTION_REGULAR_UPDATE_SUCCEED))
            {
                final SharedPreferences preferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                resetPref(context, preferences);
                NotificationHelper.showNotification(context, preferences);
            } else if (intent.getAction().equals(ACTION_REGULAR_UPDATE_INITIATE))
            {
                ChannelsContentService.startActionRegularUpdate(context);
            }
        }
    }

    private void resetPref(final Context context, final SharedPreferences preferences)
    {
        final String defaultValue = context.getString(R.string.update_delay_default_value);
        final String prefDelayKey = context.getString(R.string.update_delay_key);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putString(prefDelayKey, defaultValue);
        editor.apply();
    }
}
