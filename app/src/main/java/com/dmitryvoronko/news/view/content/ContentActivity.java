package com.dmitryvoronko.news.view.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dmitryvoronko.news.R;
import com.dmitryvoronko.news.model.data.Channel;
import com.dmitryvoronko.news.services.ContentService;
import com.dmitryvoronko.news.services.ItemToBeDeleted;
import com.dmitryvoronko.news.util.SnackbarHelper;

import java.util.ArrayList;

/**
 *
 * Created by Dmitry on 12/11/2016.
 */

abstract class ContentActivity extends AppCompatActivity
{
    private static final String TAG = "ContentActivity";

    private final ContentRecyclerViewAdapter adapter = createContentRecyclerViewAdapter();
    private SwipeRefreshLayout updateContentRefreshLayout;
    private RecyclerView recyclerView;
    private final BroadcastReceiver contentBroadcastReceiver = createBroadcastReceiver();
    private final ServiceConnection contentServiceConnection = createServiceConnection();
    protected ContentService contentService;

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setLayout();

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        final SwipeableRecyclerViewTouchListener swipeTouchListener;
        swipeTouchListener = getSwipeableRecyclerViewTouchListener();

        recyclerView.addOnItemTouchListener(swipeTouchListener);
        updateContentRefreshLayout = createUpdateContentRefreshLayout();
    }

    protected abstract void setLayout();

    @NonNull
    private SwipeableRecyclerViewTouchListener getSwipeableRecyclerViewTouchListener()
    {
        final SwipeableRecyclerViewTouchListener.SwipeListener listener =
                new SwipeableRecyclerViewTouchListener.SwipeListener()
                {
                    @Override
                    public void onDismissedBySwipeRight(final int[] reverseSortedPositions)
                    {
                        showUndoRemoveItemSnackbar();
                        for (final int position : reverseSortedPositions)
                        {
                            final Channel channel = adapter.getData().get(position);
                            final ItemToBeDeleted toBeDeleted =
                                    new ItemToBeDeleted(channel, position);
                            contentService.getDeletedItems().add(toBeDeleted);
                            contentService.getContent().remove(position);
                            adapter.notifyItemRemoved(position);
                        }
                        adapter.notifyDataSetChanged();
                    }
                };
        return new SwipeableRecyclerViewTouchListener(recyclerView, listener);
    }

    private SwipeRefreshLayout createUpdateContentRefreshLayout()
    {
        final SwipeRefreshLayout swipeRefreshLayout =
                (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                                                   R.color.colorPrimary,
                                                   R.color.colorPrimaryDark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override public void onRefresh()
            {
                swipeRefreshLayout.setRefreshing(true);
                updateContent();
            }
        });

        return swipeRefreshLayout;
    }

    private void showUndoRemoveItemSnackbar()
    {

        final View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override public void onClick(final View v)
            {
                restoreItem();
            }
        };

        SnackbarHelper.showSnackbar(this, R.string.channel_deleted_message,
                                    R.string.cancel_button_text,
                                    onClickListener,
                                    null);
    }

    protected abstract void updateContent();

    private void restoreItem()
    {
        final ItemToBeDeleted lastDeletedItem = contentService.getDeletedItems().pop();
        final int itemPosition = lastDeletedItem.getPosition();
        final Channel item = lastDeletedItem.getItem();
        contentService.getContent().add(itemPosition, item);
        adapter.notifyItemInserted(itemPosition);
        adapter.notifyDataSetChanged();
        SnackbarHelper.showSnackbar(this,
                                    R.string.channel_restored_message,
                                    SnackbarHelper.NULL_ACTION_RES_ID,
                                    null,
                                    null);
    }

    @Override protected void onPause()
    {
        super.onPause();
        unbindService(contentServiceConnection);
        unregisterReceiver(contentBroadcastReceiver);
    }

    @Override protected void onResume()
    {
        super.onResume();
        registerContentReceiver();
        bindContentService();
        requestContent();
    }

    private void registerContentReceiver()
    {
        final IntentFilter intentFilter = new IntentFilter(ContentService.ACTION_CONTENT_READY);
        intentFilter.addAction(ContentService.ACTION_NO_INTERNET_CONNECTION);
        registerReceiver(contentBroadcastReceiver, intentFilter);
    }

    private void bindContentService()
    {
        final Intent intent = getContentServiceIntent();
        bindService(intent, contentServiceConnection, Context.BIND_AUTO_CREATE);
    }

    protected abstract void requestContent();

    @NonNull protected abstract Intent getContentServiceIntent();

    protected abstract ServiceConnection createServiceConnection();

    @NonNull private BroadcastReceiver createBroadcastReceiver()
    {
        return new BroadcastReceiver()
        {
            @Override public void onReceive(final Context context, final Intent intent)
            {
                if (intent.getAction().equalsIgnoreCase(ContentService.ACTION_CONTENT_READY))
                {
                    contentChanged();
                    updateContentRefreshLayout.setRefreshing(false);
                } else if (intent.getAction().equalsIgnoreCase(
                        ContentService.ACTION_NO_INTERNET_CONNECTION))
                {
                    handleActionNoInternetConnection();
                }
            }
        };
    }

    private void contentChanged()
    {
        adapter.setData(contentService.getContent());
        adapter.notifyDataSetChanged();
    }

    private void handleActionNoInternetConnection()
    {
        SnackbarHelper.showNoInternetConnectionSnackBar(this);
        updateContentRefreshLayout.setRefreshing(false);
    }

    @NonNull private ContentRecyclerViewAdapter createContentRecyclerViewAdapter()
    {
        return new ContentRecyclerViewAdapter(new ArrayList<Channel>(),
                                              createOnCardViewClickListener());
    }

    private ContentRecyclerViewAdapter.OnCardViewClickListener createOnCardViewClickListener()
    {
        return new
                ContentRecyclerViewAdapter.OnCardViewClickListener()
                {
                    @Override public void onItemClick(final long id, final String link)
                    {
                        goToChild(id, link);
                    }
                };
    }

    protected abstract void goToChild(final long id, final String link);
}
