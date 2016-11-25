package com.dmitryvoronko.news.ui.util;

import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.dmitryvoronko.news.R;

import lombok.NonNull;

/**
 *
 * Created by Dmitry on 14/11/2016.
 */

public final class SnackbarHelper
{
    private SnackbarHelper()
    {
        throw new UnsupportedOperationException();
    }

    public static void showNoInternetConnectionSnackBar(final Activity activity)
    {
        final View.OnClickListener openSettingsAction = new View.OnClickListener()
        {
            @Override public void onClick(final View v)
            {
                final Intent intent = new Intent(Settings.ACTION_SETTINGS);
                activity.startActivity(intent);
            }
        };

        showSnackbar(activity, R.string.no_internet_connection_message, R.string.action_settings, openSettingsAction);
    }

    public static void showSnackbar(@NonNull final  Activity activity,
                                    @StringRes final int resId)
    {
        showSnackbar(activity, resId, -1, null);
    }

    public static void showSnackbar(@NonNull final Activity activity,
                                    @StringRes final int resId,
                                    final int actionResId,
                                    final View.OnClickListener onClickListener)
    {
        final View currentFocus = activity.getCurrentFocus();
        if (currentFocus != null)
        {
            final Snackbar snackbar = Snackbar.make(currentFocus, resId, Snackbar.LENGTH_SHORT);
            if (onClickListener != null)
            {
                snackbar.setAction(actionResId, onClickListener);
            }

            snackbar.show();
        }
    }
}
