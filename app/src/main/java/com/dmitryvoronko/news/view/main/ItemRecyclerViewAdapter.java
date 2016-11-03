package com.dmitryvoronko.news.view.main;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dmitryvoronko.news.R;
import com.dmitryvoronko.news.model.data.Channel;

import java.util.ArrayList;

/**
 * Created by Dmitry on 20/10/2016.
 */

public final class ItemRecyclerViewAdapter
        extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder>
{
    private final ArrayList<Channel> data;

    public ItemRecyclerViewAdapter(final ArrayList<Channel> data)
    {
        this.data = data;
    }

    @Override
    public ItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent,
                                                                 final int viewType)
    {
        final View view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.article_view,
                                                 parent,
                                                 false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,
                                 final int position)
    {
        final TextView articleTitle;
        articleTitle = (TextView) holder.cardView.findViewById(R.id.article_title);
        final TextView articleDescription;
        articleDescription = (TextView) holder.cardView.findViewById(R.id.article_description);

        final Channel channel = data.get(position);

        articleTitle.setText(channel.getTitle());
        articleDescription.setText(channel.getDescription());
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }

    final static class ViewHolder
            extends RecyclerView.ViewHolder
    {
        final CardView cardView;

        ViewHolder(View cardView)
        {
            super(cardView);
            this.cardView = (CardView) cardView;
        }
    }

}
