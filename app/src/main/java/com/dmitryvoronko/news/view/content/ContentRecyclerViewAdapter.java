package com.dmitryvoronko.news.view.content;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dmitryvoronko.news.R;
import com.dmitryvoronko.news.model.data.Channel;

import java.util.ArrayList;

/**
 *
 * Created by Dmitry on 20/10/2016.
 */

final class ContentRecyclerViewAdapter
        extends RecyclerView.Adapter<ContentRecyclerViewAdapter.ViewHolder>
{
    private final OnCardViewClickListener onItemClickListener;
    private ArrayList<Channel> data;

    ContentRecyclerViewAdapter(final ArrayList<Channel> data,
                               final OnCardViewClickListener onItemClickListener)
    {
        this.data = data;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ContentRecyclerViewAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent,
                                                                    final int viewType)
    {
        final View view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.feed_object_view,
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

        holder.cardView.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(final View v)
            {
                onItemClickListener.onItemClick(channel.getId(), channel.getLink());
            }
        });

        //noinspection deprecation
        articleTitle.setText(Html.fromHtml(channel.getTitle()));
        //noinspection deprecation
        final Spanned description = Html.fromHtml(channel.getDescription());
        articleDescription.setText(description);
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }

    public ArrayList<Channel> getData()
    {
        return data;
    }

    public void setData(final ArrayList<Channel> data)
    {
        this.data = data;
    }

    interface OnCardViewClickListener
    {
        void onItemClick(final long id, final String link);
    }

    final class ViewHolder extends RecyclerView.ViewHolder
    {
        final CardView cardView;

        ViewHolder(final View cardView)
        {
            super(cardView);
            this.cardView = (CardView) cardView;
        }
    }
}
