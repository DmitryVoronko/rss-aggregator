package com.dmitryvoronko.news.util;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Dmitry on 17/11/2016.
 */

public final class ShareHelper
{
    public static void share(final Context context,
                             final String link)
    {
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, link);
        context.startActivity(Intent.createChooser(shareIntent, "Shipping method"));
    }
}
