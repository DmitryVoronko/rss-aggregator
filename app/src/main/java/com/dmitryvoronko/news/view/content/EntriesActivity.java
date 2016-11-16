package com.dmitryvoronko.news.view.content;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.dmitryvoronko.news.R;
import com.dmitryvoronko.news.services.ContentService;
import com.dmitryvoronko.news.services.ContentType;

import java.util.concurrent.CompletableFuture;

import static com.dmitryvoronko.news.services.ContentService.EXTRA_CHANNEL_ID;

/**
 *
 * Created by Dmitry on 13/11/2016.
 */
public final class EntriesActivity extends ContentActivity
{
    private static final String TAG = "EntriesActivity";

    public static final String EXTRA_ENTRY_ID =
            "com.dmitryvoronko.news.view.content.extra.ENTRY_ID";
    public static final String EXTRA_ENTRY_LINK =
            "com.dmitryvoronko.news.view.content.extra.ENTRY_LINK";
    public static final long DEFAULT_CHANNEL_ID = -1;

    private static long channelId = DEFAULT_CHANNEL_ID;

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);

            final Intent intent = getIntent();

            Log.d(TAG, "onCreate: intent action = " + intent);

            channelId = intent.getLongExtra(EXTRA_CHANNEL_ID, channelId);

            if (channelId == DEFAULT_CHANNEL_ID)
            {
                throw new UnsupportedOperationException();
            }
        } catch (final Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override protected void initContentType()
    {
        contentType = ContentType.ENTRY;
    }

    @Override protected void setLayout()
    {
        setContentView(R.layout.activity_entries);
    }

    @Override protected void updateContent()
    {
        ContentService.startActionUpdateContent(this, channelId);
    }

    @Override protected void getContent()
    {
        ContentService.startActionGetContent(this, channelId);
    }

    @Override protected void goToChild(final long id, final String link)
    {
        final Intent intent = new Intent(this, EntryActivity.class);
        intent.putExtra(EXTRA_ENTRY_ID, id);
        intent.putExtra(EXTRA_ENTRY_LINK, link);
        startActivity(intent);
    }
}
