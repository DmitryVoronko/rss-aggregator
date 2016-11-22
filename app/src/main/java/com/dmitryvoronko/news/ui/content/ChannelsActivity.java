package com.dmitryvoronko.news.ui.content;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dmitryvoronko.news.R;
import com.dmitryvoronko.news.services.ChannelsContentService;
import com.dmitryvoronko.news.ui.util.SnackbarHelper;
import com.dmitryvoronko.news.ui.addnew.AddNewActivity;
import com.dmitryvoronko.news.ui.settings.SettingsActivity;


import static com.dmitryvoronko.news.services.EntriesContentService.EXTRA_CHANNEL_ID;

public final class ChannelsActivity extends ContentActivity
{
    private final static int ACTION_SHOW_NEW_ITEM_ACTIVITY = 1;

    @Override
    public boolean onCreateOptionsMenu(final Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        final int id = item.getItemId();

        if (id == R.id.action_settings)
        {
            startSettingsActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    private void startSettingsActivity()
    {
        final Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            if (requestCode == ACTION_SHOW_NEW_ITEM_ACTIVITY)
            {
                handleAddNewItem();
            }
        }
    }

    private void handleAddNewItem()
    {
        SnackbarHelper.showSnackbar(this,
                                    R.string.channel_successfully_added,
                                    SnackbarHelper.NULL_ACTION_RES_ID,
                                    SnackbarHelper.NULL_ON_CLICK_LISTENER,
                                    SnackbarHelper.NULL_CALLBACK);
        requestContent();
    }

    @Override protected void doOnCreate(final Bundle savedInstanceState)
    {
        super.doOnCreate(savedInstanceState);

        final FloatingActionButton addNewItemButton;
        addNewItemButton = (FloatingActionButton) findViewById(R.id.add_new_channel_fab);

        addNewItemButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                startAddNewItemActivity();
            }

        });
    }

    @Override protected void setLayout()
    {
        setContentView(R.layout.activity_main);
    }

    @Override protected void updateContent()
    {
        ChannelsContentService.startActionUpdateContent(this);
    }

    @NonNull @Override protected Intent getContentServiceIntent()
    {
        return new Intent(this, ChannelsContentService.class);
    }

    @Override protected ServiceConnection createServiceConnection()
    {
        return new ServiceConnection()
        {
            @Override
            public void onServiceConnected(final ComponentName name, final IBinder service)
            {
                final ChannelsContentService.Binder binder
                        = (ChannelsContentService.Binder) service;
                contentService = binder.getService();
            }

            @Override public void onServiceDisconnected(final ComponentName name)
            {
                contentService = null;
            }
        };
    }

    @Override protected void requestContent()
    {
        ChannelsContentService.startActionGetContent(this);
    }

    @Override protected void goToChild(final long id, final String link)
    {
        final Intent intent = new Intent(this, EntriesActivity.class);
        intent.putExtra(EXTRA_CHANNEL_ID, id);
        startActivity(intent);
    }

    private void startAddNewItemActivity()
    {
        final Intent intent = new Intent(this, AddNewActivity.class);
        startActivityForResult(intent, ACTION_SHOW_NEW_ITEM_ACTIVITY);
    }
}
