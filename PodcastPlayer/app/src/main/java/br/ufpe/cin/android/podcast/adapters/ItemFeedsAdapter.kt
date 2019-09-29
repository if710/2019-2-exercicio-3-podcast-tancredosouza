package br.ufpe.cin.android.podcast.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import br.ufpe.cin.android.podcast.EpisodeDetailActivity
import br.ufpe.cin.android.podcast.ItemFeed
import br.ufpe.cin.android.podcast.R
import br.ufpe.cin.android.podcast.holders.ItemFeedViewHolder

class ItemFeedsAdapter(private val itemFeeds: List<ItemFeed>, private val context: Context):
    RecyclerView.Adapter<ItemFeedViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemFeedViewHolder {
        val itemFeedViewInflater =
            LayoutInflater.from(context).inflate(R.layout.itemlista, parent, false)

        return ItemFeedViewHolder(itemFeedViewInflater)
    }

    override fun onBindViewHolder(holder: ItemFeedViewHolder, position: Int) {
        val itemFeed = itemFeeds[position]

        holder.bind(itemFeed, context)
    }

    override fun getItemCount() = itemFeeds.size
}