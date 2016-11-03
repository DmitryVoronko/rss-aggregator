package com.dmitryvoronko.news.view.main;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 * Created by Dmitry on 03/11/2016.
 */

public final class RecyclerViewOnScrollListener extends RecyclerView.OnScrollListener
{
    private static final String TAG = RecyclerViewOnScrollListener.class.getName();

    private boolean loading = false;

    private final LinearLayoutManager linearLayoutManager;
    private final OnScrollListener scrollListener;

    public RecyclerViewOnScrollListener(final LinearLayoutManager linearLayoutManager,
                                        final OnScrollListener scrollListener)
    {
        this.linearLayoutManager = linearLayoutManager;
        this.scrollListener = scrollListener;
    }

    @Override public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy)
    {
        super.onScrolled(recyclerView, dx, dy);

        Log.d(TAG, "onScrolled: dy = " + dy);
        Log.d(TAG, "onScrolled: dx = " + dx);
//        if (!loading)
//        {
//
//        }
    }

    public interface OnScrollListener
    {
        public void onScrollUp();
        public void onScrollDown();

    }
}
