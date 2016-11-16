package com.dmitryvoronko.news.view.content;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dmitryvoronko.news.R;
import com.dmitryvoronko.news.util.NetworkHelper;
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

    private void loadEntry(final String entryLink)
    {
        final WebView webView = (WebView) findViewById(R.id.entry_web_view);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(entryLink);
    }
}
