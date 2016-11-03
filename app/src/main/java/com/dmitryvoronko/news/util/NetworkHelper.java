package com.dmitryvoronko.news.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Dmitry on 01/11/2016.
 */

public final class NetworkHelper
{
    public static boolean hasConnection(final Context context)
    {
        final ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo.isConnectedOrConnecting();
    }
}
