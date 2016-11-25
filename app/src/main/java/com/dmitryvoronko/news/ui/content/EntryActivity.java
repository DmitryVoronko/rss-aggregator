package com.dmitryvoronko.news.ui.content;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dmitryvoronko.news.R;
import com.dmitryvoronko.news.ui.ActivityBase;
import com.dmitryvoronko.news.ui.util.ShareHelper;

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

        webView = (WebView) findViewById(R.id.entry_web_view);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebViewClient(new Browser());
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

    private void handleIntent(final Intent intent)
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

        webView.loadUrl(entryLink);
    }

    private final class Browser extends WebViewClient
    {
        @SuppressWarnings("deprecation")
        @Override public boolean shouldOverrideUrlLoading(final WebView view,
                                                          final String url)
        {
            view.loadUrl(url);
            return true;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(final WebView view,
                                                final WebResourceRequest request)
        {
            view.loadUrl(request.getUrl().toString());
            return true;
        }
    }
}
