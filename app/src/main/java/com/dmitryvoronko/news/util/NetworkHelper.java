package com.dmitryvoronko.news.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import lombok.NonNull;

/**
 *
 * Created by Dmitry on 01/11/2016.
 */

public final class NetworkHelper
{
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
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
