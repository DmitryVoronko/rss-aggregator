package com.dmitryvoronko.news.ui.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.dmitryvoronko.news.R;
import com.dmitryvoronko.news.ui.ChannelsActivity;

/**
 *
 * Created by Dmitry on 20/11/2016.
 */

public final class NotificationHelper
{
    private static final int NOTIFICATION_ID = 1;
    private static final String GREEN_COLOR_NUMBER = "4";
    private static final boolean NOTIFICATION_AUTO_CANCEL = true;
    private static final long[] VIBRATE_PATTERN = {50, 50};
    private static final int NOTIFICATION_LED_ON_MS = 1000;
    private static final int NOTIFICATION_LED_OFF_MS = 1000;

    private NotificationHelper()
    {
        throw new UnsupportedOperationException();
    }

    public static void showNotification(final Context context, final SharedPreferences sharedPreferences)
    {
        if (notificationsEnabled(context, sharedPreferences))
        {
            createAndShowNotification(context, sharedPreferences);
        }
    }

    private static void createAndShowNotification(final Context context, final SharedPreferences sharedPreferences)
    {
        final PendingIntent pendingIntent = createNotificationActionIntent(context);

        final String notificationTitle =
                getString(context, R.string.news_update_notification_title);
        final String notificationText = getString(context, R.string.news_update_notification_text);
        final Bitmap largeIcon =
                BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);

        final Uri soundUri = getSoundUri(context, sharedPreferences);
        final NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.notification_small_icon)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationText)
                        .setLargeIcon(largeIcon)
                        .setContentIntent(pendingIntent)
                        .setSound(soundUri)
                        .setAutoCancel(NOTIFICATION_AUTO_CANCEL);

        if (hasVibrate(context, sharedPreferences))
        {
            final long[] pattern = VIBRATE_PATTERN;
            builder.setVibrate(pattern);
        }

        if (hasLights(context, sharedPreferences))
        {
            final int color = getColor(context, sharedPreferences);
            final int onMs = NOTIFICATION_LED_ON_MS;
            final int offMs = NOTIFICATION_LED_OFF_MS;
            builder.setLights(color, onMs, offMs);
        }

        final NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private static int getColor(final Context context, final SharedPreferences sharedPreferences)
    {
        final String prefColorName = getString(context, R.string.pref_key_notifications_led_color);
        final String defaultColor = GREEN_COLOR_NUMBER;
        final int colorNumber =
                Integer.parseInt(sharedPreferences.getString(prefColorName, defaultColor));
        final int colorResId = chooseColorResId(colorNumber);
        return getColor(context, colorResId);
    }

    private static int chooseColorResId(final int colorNumber)
    {
        final int colorResId;
        switch (colorNumber)
        {
            case 1:
                colorResId = R.color.red;
                break;
            case 2:
                colorResId = R.color.orange;
                break;
            case 3:
                colorResId = R.color.yellow;
                break;
            case 4:
                colorResId = R.color.green;
                break;
            case 5:
                colorResId = R.color.cyan;
                break;
            case 6:
                colorResId = R.color.blue;
                break;
            case 7:
                colorResId = R.color.indigo;
                break;
            case 8:
                colorResId = R.color.purple;
                break;
            case 9:
                colorResId = R.color.white;
                break;
            default:
                colorResId = R.color.green;
                break;
        }
        return colorResId;
    }

    private static int getColor(final Context context, final int resId)
    {
        //noinspection deprecation
        return context.getResources().getColor(resId);
    }

    private static boolean hasLights(final Context context, final SharedPreferences sharedPreferences)
    {
        final String prefLedSwitchKey =
                getString(context, R.string.pref_key_notifications_led_switch);
        final boolean defaultSwitchValue = true;
        return sharedPreferences.getBoolean(prefLedSwitchKey, defaultSwitchValue);
    }

    private static boolean notificationsEnabled(final Context context, final SharedPreferences sharedPreferences)
    {
        final String prefRingtoneSwitchKey =
                getString(context, R.string.pref_key_notifications_switch);
        final boolean defaultSwitchValue = true;
        return sharedPreferences.getBoolean(prefRingtoneSwitchKey, defaultSwitchValue);
    }

    private static Uri getSoundUri(final Context context,
                            final SharedPreferences sharedPreferences)
    {
        final String prefRingtoneKey = context.getString(R.string.pref_key_ringtone);
        final Uri actualDefaultRingtoneUri = RingtoneManager
                .getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION);
        return Uri.parse(sharedPreferences.getString(prefRingtoneKey,
                                                     actualDefaultRingtoneUri.toString()));
    }

    private static PendingIntent createNotificationActionIntent(final Context context)
    {
        final Intent intent = new Intent(context, ChannelsActivity.class);
        final  int requestID = (int) System.currentTimeMillis();
        final int flags = PendingIntent.FLAG_CANCEL_CURRENT;
        return PendingIntent.getActivity(context, requestID, intent, flags);
    }

    private static String getString(final Context context, final int resId)
    {
        return context.getString(resId);
    }

    private static boolean hasVibrate(final Context context,
                               final SharedPreferences sharedPreferences)
    {
        final String prefVibrateKey =
                getString(context, R.string.pref_key_notifications_vibrate);
        final boolean defaultVibrateValue = true;
        return sharedPreferences.getBoolean(prefVibrateKey, defaultVibrateValue);
    }
}
