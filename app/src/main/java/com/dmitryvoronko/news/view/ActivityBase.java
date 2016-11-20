package com.dmitryvoronko.news.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dmitryvoronko.news.view.util.ThemeHelper;

/**
 *
 * Created by Dmitry on 20/11/2016.
 */

public abstract class ActivityBase extends AppCompatActivity
{
    @Override public final void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTheme();
        doOnCreate(savedInstanceState);
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
