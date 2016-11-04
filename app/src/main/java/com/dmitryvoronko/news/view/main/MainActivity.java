package com.dmitryvoronko.news.view.main;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dmitryvoronko.news.R;
import com.dmitryvoronko.news.model.data.Channel;
import com.dmitryvoronko.news.model.data.FeedObjectFactory;
import com.dmitryvoronko.news.model.data.State;
import com.dmitryvoronko.news.services.MainService;
import com.dmitryvoronko.news.view.add.AddNewActivity;
import com.dmitryvoronko.news.view.settings.SettingsActivity;

import java.util.ArrayList;

import lombok.Data;

public final class MainActivity extends AppCompatActivity
{
    private final static int SHOW_NEW_ITEM_ACTIVITY = 1;
    public static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private final ArrayList<Channel> data = new ArrayList<>();
    private SwipeRefreshLayout updateItemsRefreshLayout;

    private TempItem tempItem;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

//        bindMainService();

        setContentView(R.layout.activity_main);

        updateItemsRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        updateItemsRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);

        updateItemsRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override public void onRefresh()
            {
//                Toast.makeText(this, R.string.refresh_started, Toast.LENGTH_SHORT).show();
                // начинаем показывать прогресс
                updateItemsRefreshLayout.setRefreshing(true);
                // ждем 3 секунды и прячем прогресс
                updateItemsRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateItemsRefreshLayout.setRefreshing(false);
                        // говорим о том, что собираемся закончить
//                        Toast.makeText(MainActivity.this, R.string.refresh_finished, Toast.LENGTH_SHORT).show();
                    }
                }, 3000);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(final RecyclerView recyclerView, final int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d(TAG, "onScrollStateChanged: state changed = " + newState);
            }

            @Override
            public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                Log.d(TAG, "onScrolled: dx = " + dx + ", dy = " + dy);
            }
        });
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

    private void bindMainService()
    {
        final Intent intent = new Intent(this, MainService.class);
        final ComponentName componentName = startService(intent);

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
//        final String channelUrl = input.getStringExtra(AddNewActivity.CHANNEL_LINK);
//        final Channel channel = FeedObjectFactory.createChannel(0, channelUrl, channelUrl, channelUrl,
//                                                                channelUrl,
//                                                                State.IS_NOT_READ);
//        putChannelToStorage(channel, 0);
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
