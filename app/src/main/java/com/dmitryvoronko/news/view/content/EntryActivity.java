package com.dmitryvoronko.news.view.content;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dmitryvoronko.news.R;
import com.dmitryvoronko.news.util.NetworkHelper;
import com.dmitryvoronko.news.util.ShareHelper;
import com.dmitryvoronko.news.util.SnackbarHelper;

import static com.dmitryvoronko.news.view.content.EntriesActivity.EXTRA_ENTRY_ID;
import static com.dmitryvoronko.news.view.content.EntriesActivity.EXTRA_ENTRY_LINK;

public final class EntryActivity extends AppCompatActivity
{
    private final static long NO_ENTRY_ID = -1;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        final FloatingActionButton shareButton =
                (FloatingActionButton) findViewById(R.id.share_entry_fab);
        final Intent intent = getIntent();
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
        }
    }

    private void share(final String entryLink)
    {
        ShareHelper.share(this, entryLink);
    }

    private void loadEntry(final String entryLink)
    {
        final WebView webView = (WebView) findViewById(R.id.entry_web_view);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(entryLink);
    }
}
