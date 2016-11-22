package com.dmitryvoronko.news.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dmitryvoronko.news.util.log.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 *
 * Created by Dmitry on 21/11/2016.
 */

public final class RegularUpdateScheduler
{
    private static final String TAG = "RegularUpdateScheduler";
    private static final int COUNT_DAY = 1;

    private RegularUpdateScheduler()
    {
        throw new UnsupportedOperationException();
    }

    public static void scheduleAlarm(final Context context, final int hours, final int minutes)
    {
        final Calendar calendar = Calendar.getInstance();
        final long currentTime = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        final long scheduledTime = calendar.getTimeInMillis();
        
        if (!(currentTime <= scheduledTime))
        {
            calendar.add(Calendar.DAY_OF_WEEK, COUNT_DAY);
        }

        final Intent intent = new Intent(context, RegularUpdateReceiver.class);
        intent.setAction(RegularUpdateReceiver.ACTION_REGULAR_UPDATE_INITIATE);
        final PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context,
                                           RegularUpdateReceiver.REQUEST_CODE,
                                           intent,
                                           PendingIntent.FLAG_UPDATE_CURRENT);
        final AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                                         scheduledTime,
                                         AlarmManager.INTERVAL_DAY,
                                         pendingIntent);
    }
}
