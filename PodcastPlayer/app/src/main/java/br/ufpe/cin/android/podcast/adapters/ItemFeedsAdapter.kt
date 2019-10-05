package br.ufpe.cin.android.podcast.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.ufpe.cin.android.podcast.ItemFeed
import br.ufpe.cin.android.podcast.R
import br.ufpe.cin.android.podcast.holders.ItemFeedViewHolder
import br.ufpe.cin.android.podcast.services.PodcastPlayerWithBindingService

class ItemFeedsAdapter(
    private val itemFeeds: List<ItemFeed>,
    private val context: Context,
    private val podcastPlayerWithBindingService: PodcastPlayerWithBindingService?):
    RecyclerView.Adapter<ItemFeedViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemFeedViewHolder {
        val itemFeedViewInflater =
            LayoutInflater.from(context).inflate(R.layout.itemlista, parent, false)

        return ItemFeedViewHolder(itemFeedViewInflater, podcastPlayerWithBindingService)
    }

    override fun onBindViewHolder(holder: ItemFeedViewHolder, position: Int) {
        val itemFeed = itemFeeds[position]

        holder.bind(itemFeed, context)
    }

    override fun getItemCount() = itemFeeds.size
}