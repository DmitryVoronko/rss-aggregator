package com.dmitryvoronko.news.ui.content;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dmitryvoronko.news.R;
import com.dmitryvoronko.news.util.NetworkHelper;
import com.dmitryvoronko.news.ui.ActivityBase;
import com.dmitryvoronko.news.ui.util.ShareHelper;
import com.dmitryvoronko.news.ui.util.SnackbarHelper;

import static com.dmitryvoronko.news.ui.content.EntriesActivity.EXTRA_ENTRY_ID;
import static com.dmitryvoronko.news.ui.content.EntriesActivity.EXTRA_ENTRY_LINK;

public final class EntryActivity extends ActivityBase
{
    private final static long NO_ENTRY_ID = -1;
    private WebView webView;
    private FloatingActionButton shareButton;

    @Override protected void doOnCreate(final Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_entry);
        shareButton = (FloatingActionButton) findViewById(R.id.share_entry_fab);
        webView =  (WebView) findViewById(R.id.entry_web_view);
        webView.setWebViewClient(new WebViewClient());
        final Intent intent = getIntent();
        handleIntent(intent);
    }

    @Override protected void doOnResume()
    {

    }

    @Override protected void doOnPause()
    {

    }

    private void share(final String entryLink)
    {
        ShareHelper.share(this, entryLink);
    }

    private void loadEntry(final String entryLink)
    {
        webView.loadUrl(entryLink);
    }

    private void handleIntent(final Intent intent)
    {
        if (intent != null)
        {
            final long entryId = intent.getLongExtra(EXTRA_ENTRY_ID, NO_ENTRY_ID);
            if (entryId == NO_ENTRY_ID)
            {
                throw new UnsupportedOperationException();
            }
            final String entryLink = intent.getStringExtra(EXTRA_ENTRY_LINK);

            if (entryLink == null)
            {
                throw new UnsupportedOperationException();
            }

            shareButton.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(final View view)
                {
                    share(entryLink);
                }
            });

            final boolean hasConnection = NetworkHelper.hasConnection(this);
            if (hasConnection)
            {
                loadEntry(entryLink);
            } else
            {
                SnackbarHelper.showNoInternetConnectionSnackBar(this);
            }
        } else
        {
            throw new UnsupportedOperationException();
        }
    }
}
