package com.dmitryvoronko.news.view.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dmitryvoronko.news.R;
import com.dmitryvoronko.news.model.data.Channel;
import com.dmitryvoronko.news.view.add.AddNewActivity;
import com.dmitryvoronko.news.view.settings.SettingsActivity;

import java.util.ArrayList;

import lombok.Data;

public final class MainActivity extends AppCompatActivity
{
    private final static int SHOW_NEW_ITEM_ACTIVITY = 1;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private final ArrayList<Channel> data = new ArrayList<>();

    private TempItem tempItem;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        recyclerView.setHasFixedSize(true);

        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ItemRecyclerViewAdapter(data);
        recyclerView.setAdapter(adapter);

        final SwipeableRecyclerViewTouchListener swipeTouchListener =
                getSwipeableRecyclerViewTouchListener();

        recyclerView.addOnItemTouchListener(swipeTouchListener);


        final FloatingActionButton addNewItemButton = (FloatingActionButton) findViewById(R.id.fab);

        addNewItemButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startAddNewItemActivity();
            }

        });

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @NonNull
    private SwipeableRecyclerViewTouchListener getSwipeableRecyclerViewTouchListener()
    {
        final SwipeableRecyclerViewTouchListener.SwipeListener listener =
                new SwipeableRecyclerViewTouchListener.SwipeListener()
                {
                    @Override
                    public boolean canSwipeLeft(final int position)
                    {
                        return false;
                    }

                    @Override
                    public boolean canSwipeRight(final int position)
                    {
                        return true;
                    }

                    @Override
                    public void onDismissedBySwipeLeft(final RecyclerView recyclerView,
                                                       final int[] reverseSortedPositions)
                    {
                    }

                    @Override
                    public void onDismissedBySwipeRight(final RecyclerView recyclerView,
                                                        final int[] reverseSortedPositions)
                    {
                        getUndoRemoveItemSnackbar(getCurrentFocus()).show();
                        for (final int position : reverseSortedPositions)
                        {
                            final Channel temp = data.get(position);

                            tempItem = new TempItem(temp, position);

                            data.remove(position);
                            adapter.notifyItemRemoved(
                                    position);
                        }
                        adapter.notifyDataSetChanged();
                    }
                };
        return new SwipeableRecyclerViewTouchListener(recyclerView, listener);
    }

    private void startAddNewItemActivity()
    {
        final Intent intent = new Intent(this, AddNewActivity.class);
        startActivityForResult(intent, SHOW_NEW_ITEM_ACTIVITY);
    }

    private Snackbar getUndoRemoveItemSnackbar(final View view)
    {
        final Snackbar snackbar = Snackbar.make(
                view,
                R.string.channel_deleted_message,
                Snackbar.LENGTH_LONG
        );

        snackbar.setAction(R.string.cancel_button_text, new View.OnClickListener()
        {
            @Override public void onClick(final View v)
            {
                putChannelToStorage(tempItem.getTemp(), tempItem.getPosition());
                showRestoredSnackbar(v);
            }
        });

        return snackbar;
    }

    private void putChannelToStorage(final Channel channel,
                                     final int position)
    {
        data.add(position, channel);
        adapter.notifyItemInserted(position);
        adapter.notifyDataSetChanged();
    }

    private void showRestoredSnackbar(final View v)
    {
        final Snackbar restoredSnackbar = Snackbar.make(
                v,
                R.string.channel_restored_message,
                Snackbar.LENGTH_SHORT
        );
        restoredSnackbar.show();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
    protected void onActivityResult(final int requestCode,
                                    final int resultCode,
                                    final Intent data)
    {
        super.onActivityResult(requestCode,
                               resultCode,
                               data);

        if (resultCode == RESULT_OK)
        {
            if (requestCode == SHOW_NEW_ITEM_ACTIVITY)
            {
                handleAddNewItem(data);
            }
        }
    }

    private void handleAddNewItem(final Intent input)
    {
        final String channelUrl = input.getStringExtra(AddNewActivity.CHANNEL_LINK);
        final Channel channel = new Channel(channelUrl, channelUrl, channelUrl, channelUrl);
        putChannelToStorage(channel, 0);
    }

    @Data
    private final class TempItem
    {
        private final Channel temp;
        private final int position;

        private TempItem(final Channel temp, final int position)
        {
            this.temp = temp;
            this.position = position;
        }
    }
}
