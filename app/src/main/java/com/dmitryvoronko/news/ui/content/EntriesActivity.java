package com.dmitryvoronko.news.ui.content;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;

import com.dmitryvoronko.news.R;
import com.dmitryvoronko.news.services.EntriesContentService;

import static com.dmitryvoronko.news.services.EntriesContentService.EXTRA_CHANNEL_ID;

/**
 *
 * Created by Dmitry on 13/11/2016.
 */
public final class EntriesActivity extends ContentActivity
{
    public static final String EXTRA_ENTRY_ID =
            "com.dmitryvoronko.news.ui.content.extra.ENTRY_ID";
    public static final String EXTRA_ENTRY_LINK =
            "com.dmitryvoronko.news.ui.content.extra.ENTRY_LINK";
    public static final long DEFAULT_CHANNEL_ID = -10;
    private static long channelId = DEFAULT_CHANNEL_ID;

    @Override protected void doOnCreate(final Bundle savedInstanceState)
    {
        super.doOnCreate(savedInstanceState);
        final Intent intent = getIntent();

        channelId = intent.getLongExtra(EXTRA_CHANNEL_ID, channelId);

        if (channelId == DEFAULT_CHANNEL_ID)
        {
            throw new UnsupportedOperationException();
        }
    }

    @Override protected void setLayout()
    {
        setContentView(R.layout.activity_entries);
    }

    @Override protected void updateContent()
    {
        EntriesContentService.startActionUpdateContent(this, channelId);
    }

    @Override protected void requestContent()
    {
        EntriesContentService.startActionGetContent(this, channelId);
    }

    @NonNull @Override protected Intent getContentServiceIntent()
    {
        return new Intent(this, EntriesContentService.class);
    }

    @Override protected ServiceConnection createServiceConnection()
    {
        return new ServiceConnection()
        {
            @Override
            public void onServiceConnected(final ComponentName name, final IBinder service)
            {
                final EntriesContentService.Binder binder = (EntriesContentService.Binder) service;
                contentService = binder.getService();
            }

            @Override public void onServiceDisconnected(final ComponentName name)
            {
                contentService = null;
            }
        };
    }

    @Override protected void goToChild(final long id, final String link)
    {
        final Intent intent = new Intent(this, EntryActivity.class);
        intent.putExtra(EXTRA_ENTRY_ID, id);
        intent.putExtra(EXTRA_ENTRY_LINK, link);
        startActivity(intent);
    }
}
