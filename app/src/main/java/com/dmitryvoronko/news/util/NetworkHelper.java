package com.dmitryvoronko.news.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.dmitryvoronko.news.util.log.Logger;

import java.net.InetAddress;

import lombok.NonNull;

/**
 *
 * Created by Dmitry on 01/11/2016.
 */

public final class NetworkHelper
{
    private static final String TAG = "NetworkHelper";
    private static final String HOST = "google.com";

    private NetworkHelper()
    {
        throw new UnsupportedOperationException();
    }

    public static boolean hasConnection(@NonNull final Context context)
    {
        final ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
        {
            return false;
        }
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null &&
               networkInfo.isConnectedOrConnecting() &&
               isInternetAvailable();
    }

    private static boolean isInternetAvailable()
    {
        try
        {
            final InetAddress inetAddress = InetAddress.getByName(HOST);
            Logger.i(TAG, "isInternetAvailable: inetAddress " + inetAddress);
            return !inetAddress.toString().equals("");
        } catch (final Exception e)
        {
            return false;
        }
    }
}
