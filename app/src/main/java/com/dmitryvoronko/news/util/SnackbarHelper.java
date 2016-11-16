package com.dmitryvoronko.news.util;

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
    public static final int NULL_ACTION_RES_ID = -1;
    public static final View.OnClickListener NULL_ON_CLICK_LISTENER = null;
    public static final Snackbar.Callback NULL_CALLBACK = null;

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

        showSnackbar(activity, R.string.no_internet_connection_message, R.string.action_settings,
                     openSettingsAction, NULL_CALLBACK);
    }

    public static void showSnackbar(@NonNull final Activity activity,
                                    @StringRes final int resId,
                                    final int actionResId,
                                    final View.OnClickListener onClickListener,
                                    final Snackbar.Callback callback)
    {
        final View currentFocus = activity.getCurrentFocus();
        if (currentFocus != null)
        {
            final Snackbar snackbar = Snackbar.make(currentFocus, resId, Snackbar.LENGTH_SHORT);
            if (onClickListener != NULL_ON_CLICK_LISTENER)
            {
                snackbar.setAction(actionResId, onClickListener);
            }

            if (callback != NULL_CALLBACK)
            {
                snackbar.setCallback(callback);
            }

            snackbar.show();
        }
    }
}
