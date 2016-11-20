package com.dmitryvoronko.news.view.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dmitryvoronko.news.R;

/**
 *
 * Created by Dmitry on 20/11/2016.
 */

public final class ThemeHelper
{
    private final static String THEME_GREEN_KEY = "1";

    private ThemeHelper()
    {
        throw new UnsupportedOperationException();
    }

    public static void setTheme(final Context context)
    {
        final SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        final String themeKey = context.getString(R.string.pref_key_choose_theme);
        final int themeNumber =
                Integer.valueOf(sharedPreferences.getString(themeKey, THEME_GREEN_KEY));
        final int themeResId;

        switch (themeNumber)
        {
            case 1:
                themeResId = R.style.Green;
                break;
            case 2:
                themeResId = R.style.Indigo;
                break;
            default:
                themeResId = R.style.Green;
        }

        context.setTheme(themeResId);
    }
}
