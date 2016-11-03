package com.dmitryvoronko.news.view.settings;

import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A {@link android.preference.PreferenceActivity} which implements and proxies the necessary calls
 * to be used with AppCompat.
 */
public abstract class AppCompatPreferenceActivity extends PreferenceActivity
{

    private AppCompatDelegate mDelegate;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    private AppCompatDelegate getDelegate()
    {
        if (mDelegate == null)
        {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    @Override
    protected void onPostResume()
    {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
    }

    @Override
    public void setContentView(
            @LayoutRes
            final int layoutResID)
    {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    public void setContentView(final View view)
    {
        getDelegate().setContentView(view);
    }

    @Override
    public void setContentView(final View view, final ViewGroup.LayoutParams params)
    {
        getDelegate().setContentView(view, params);
    }

    @Override
    public void addContentView(final View view, final ViewGroup.LayoutParams params)
    {
        getDelegate().addContentView(view, params);
    }

    public void invalidateOptionsMenu()
    {
        getDelegate().invalidateOptionsMenu();
    }

    @Override
    @android.support.annotation.NonNull public MenuInflater getMenuInflater()
    {
        return getDelegate().getMenuInflater();
    }

    @Override
    protected void onTitleChanged(final CharSequence title, final int color)
    {
        super.onTitleChanged(title, color);
        getDelegate().setTitle(title);
    }

    ActionBar getSupportActionBar()
    {
        return getDelegate().getSupportActionBar();
    }

    public void setSupportActionBar(
            @Nullable
            final Toolbar toolbar)
    {
        getDelegate().setSupportActionBar(toolbar);
    }
}
