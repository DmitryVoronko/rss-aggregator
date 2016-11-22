package com.dmitryvoronko.news.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;


/**
 *
 * Created by Dmitry on 21/11/2016.
 */

public final class RegularUpdateScheduler
{
    private RegularUpdateScheduler()
    {
        throw new UnsupportedOperationException();
    }

    public static void scheduleAlarm(final Context context, final int hours, final int minutes)
    {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);

        final Intent intent = new Intent(context, RegularUpdateReceiver.class);
        intent.setAction(RegularUpdateReceiver.ACTION_REGULAR_UPDATE_INITIATE);
        final PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context,
                                           RegularUpdateReceiver.REQUEST_CODE,
                                           intent,
                                           PendingIntent.FLAG_UPDATE_CURRENT);
        final AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final long timeInMillis = calendar.getTimeInMillis();
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                                         timeInMillis,
                                         AlarmManager.INTERVAL_DAY,
                                         pendingIntent);
    }
}
