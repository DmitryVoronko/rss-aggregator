package com.dmitryvoronko.news.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dmitryvoronko.news.util.log.Logger;
import com.dmitryvoronko.news.ui.util.ThemeHelper;

/**
 *
 * Created by Dmitry on 20/11/2016.
 */

public abstract class ActivityBase extends AppCompatActivity
{
    private static final String TAG = "ActivityBase";

    @Override public final void onCreate(final Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);
            setTheme();
            doOnCreate(savedInstanceState);
        } catch (final Exception e)
        {
            Logger.e(TAG, "onCreate", e);
            e.printStackTrace();
        }
    }


    private void setTheme()
    {
        ThemeHelper.setTheme(this);
    }

    protected abstract void doOnCreate(final Bundle savedInstanceState);

    @Override protected final void onPause()
    {
        super.onPause();
        doOnPause();
    }

    @Override protected final void onResume()
    {
        super.onResume();
        doOnResume();
    }

    protected abstract void doOnResume();

    protected abstract void doOnPause();

}
