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
import com.dmitryvoronko.news.view.addnew.AddNewActivity;
import com.dmitryvoronko.news.view.settings.SettingsActivity;

import java.util.ArrayList;

public final class MainActivity extends AppCompatActivity
{

    private final static int SHOW_NEW_ITEM_ACTIVITY = 1;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<Channel> data = new ArrayList<Channel>();

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

    private Snackbar getUndoRemoveItemSnackbar(final View view)
    {
        final Snackbar snackbar = Snackbar.make(
                view,
                "Channel is deleted!",
                Snackbar.LENGTH_LONG
        );

        snackbar.setAction("CANCEL", new View.OnClickListener()
        {
            @Override public void onClick(final View v)
            {
                final Snackbar restoredSnackbar = Snackbar.make(
                        v,
                        "Channel is restored!",
                        Snackbar.LENGTH_SHORT
                );
                restoredSnackbar.show();
            }
        });

        return snackbar;
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

    private void startAddNewItemActivity()
    {
        final Intent intent = new Intent(this, AddNewActivity.class);
        startActivity(intent);
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
                            data.remove(position);
                            adapter.notifyItemRemoved(
                                    position);
                        }
                        adapter.notifyDataSetChanged();
                    }
                };
        return new SwipeableRecyclerViewTouchListener(recyclerView, listener);
    }

    private void handleAddNewItem(final Intent input)
    {
//        final String channelUrl = input.getStringExtra(Channel.KEY_CHANNEL_URL_COLUMN);
//        final Channel channel = new Channel(channelUrl, channelUrl, channelUrl);
//        data.add(0, channel);
//        adapter.notifyItemInserted(0);
//        adapter.notifyDataSetChanged();
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
}
