package com.dmitryvoronko.news.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.dmitryvoronko.news.R;
import com.dmitryvoronko.news.util.log.Logger;

import java.util.Calendar;


/**
 *
 * Created by Dmitry on 21/11/2016.
 */

public final class RegularUpdateScheduler
{
    private static final String TAG = "RegularUpdateScheduler";

    private static final int COUNT_DAY = 1;
    private static final int MAX_DELAY = 40;

    private RegularUpdateScheduler()
    {
        throw new UnsupportedOperationException();
    }

    public static void scheduleAlarm(final Context context, final int hours, final int minutes)
    {
        Logger.i(TAG, "hours = " + hours + ", minutes = " + minutes);
        final Calendar calendar = Calendar.getInstance();
        final long currentTime = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        final long scheduledTime = calendar.getTimeInMillis();

        if (currentTime > scheduledTime)
        {
            calendar.add(Calendar.DAY_OF_WEEK, COUNT_DAY);
        }

        Logger.i(TAG, "currentTime = " + currentTime + ", scheduledTime = " + scheduledTime);

        final Intent intent = getIntent(context);
        final PendingIntent pendingIntent = getPendingIntent(context, intent);
        final AlarmManager alarmManager =
                getAlarmManager(context);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                                         calendar.getTimeInMillis(),
                                         AlarmManager.INTERVAL_DAY,
                                         pendingIntent);
    }

    @NonNull private static Intent getIntent(final Context context)
    {
        final Intent intent = new Intent(context, RegularUpdateReceiver.class);
        intent.setAction(RegularUpdateReceiver.ACTION_REGULAR_UPDATE_INITIATE);
        return intent;
    }

    private static PendingIntent getPendingIntent(final Context context, final Intent intent)
    {
        return PendingIntent.getBroadcast(context,
                                          RegularUpdateReceiver.STANDARD_REQUEST_CODE,
                                          intent,
                                          PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static AlarmManager getAlarmManager(final Context context)
    {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public static void cancelAlarm(final Context context)
    {
        final Intent intent = getIntent(context);
        final PendingIntent pendingIntent = getPendingIntent(context, intent);
        getAlarmManager(context).cancel(pendingIntent);
    }

    static void anotherTryScheduleAlarm(final Context context)
    {
        final SharedPreferences preferences =
              PreferenceManager.getDefaultSharedPreferences(context);
        final String defaultValue = context.getString(R.string.update_delay_default_value);
        final String updateDelayKey = context.getString(R.string.update_delay_key);
        final String updateDelayString = preferences.getString(updateDelayKey, defaultValue);
        final int updateDelay = Integer.valueOf(updateDelayString);
        if (updateDelay >= MAX_DELAY)
        {
            Logger.i(TAG, "anotherTryScheduleAlarm: updateDelay >= 40, update canceled");
            editPref(preferences, defaultValue, updateDelayKey);
        } else
        {
            final Intent intent = getIntent(context);
            final PendingIntent pendingIntent =
                  PendingIntent.getBroadcast(context,
                                             RegularUpdateReceiver.ANOTHER_TRY_REQUEST_CODE,
                                             intent, PendingIntent.FLAG_ONE_SHOT);

            final Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, updateDelay);
            final AlarmManager alarmManager = getAlarmManager(context);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

        final String nextDelay = String.valueOf(updateDelay * 2);
        editPref(preferences, nextDelay, updateDelayKey);
    }

    private static void editPref(final SharedPreferences preferences, final String value,
                                 final String key)
    {
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

}
