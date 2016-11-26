package com.dmitryvoronko.news.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dmitryvoronko.news.R;
import com.dmitryvoronko.news.ui.util.ShareHelper;

public final class EntryActivity extends ActivityBase
{
    private static final String EXTRA_ENTRY_ID =
            "com.dmitryvoronko.news.ui.content.extra.ENTRY_ID";
    private static final String EXTRA_ENTRY_LINK =
            "com.dmitryvoronko.news.ui.content.extra.ENTRY_LINK";
    private final static long NO_ENTRY_ID = -1;
    private WebView webView;
    private FloatingActionButton shareButton;

    static void startEntryActivity(final Context context, final long id, final String link)
    {
        final Intent intent = new Intent(context, EntryActivity.class);
        intent.putExtra(EXTRA_ENTRY_ID, id);
        intent.putExtra(EXTRA_ENTRY_LINK, link);
        context.startActivity(intent);
    }

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

    private static final class Browser extends WebViewClient
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
