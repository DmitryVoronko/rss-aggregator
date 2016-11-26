package com.dmitryvoronko.news.ui;

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
import com.dmitryvoronko.news.ui.content.ContentActivity;
import com.dmitryvoronko.news.ui.util.SnackbarHelper;

public final class ChannelsActivity extends ContentActivity
{

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
        SettingsActivity.startSettingsActivity(this);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            if (requestCode == AddNewActivity.ACTION_SHOW_NEW_ITEM_ACTIVITY)
            {
                handleAddNewItem();
            }
        }
    }

    private void handleAddNewItem()
    {
        SnackbarHelper.showSnackbar(this, R.string.channel_successfully_added);
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
                startAddNewActivity();
            }

        });
    }

    private void startAddNewActivity()
    {
        AddNewActivity.startAddNewItemActivity(this);
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
                setContentService(binder.getService());
            }

            @Override public void onServiceDisconnected(final ComponentName name)
            {
                setContentService(null);
            }
        };
    }

    @Override protected void requestContent()
    {
        ChannelsContentService.startActionGetContent(this);
    }

    @Override protected void goToChild(final long id, final String link)
    {
        EntriesActivity.startEntriesActivity(this, id);
    }

}
