package br.ufpe.cin.android.podcast.holders

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.ufpe.cin.android.podcast.EpisodeDetailActivity
import br.ufpe.cin.android.podcast.ItemFeed
import br.ufpe.cin.android.podcast.R
import br.ufpe.cin.android.podcast.services.DownloadPodcastEpisodeService

class ItemFeedViewHolder (item: View) : RecyclerView.ViewHolder(item), View.OnClickListener {
    var podcastTitleView: TextView? = null
    var podcastPubDateView: TextView? = null
    var podcastDownloadLinkView: Button? = null
    var podcastPlayButtonView: ImageView? = null

    init {
        podcastTitleView = itemView.findViewById(R.id.item_title)
        podcastPubDateView = itemView.findViewById(R.id.item_date)
        podcastDownloadLinkView = itemView.findViewById(R.id.item_action)
        podcastPlayButtonView = itemView.findViewById(R.id.item_play)
        item.setOnClickListener(this)
    }

    fun bind(itemFeed : ItemFeed, context: Context) {
        podcastTitleView?.text = itemFeed.title
        podcastPubDateView?.text = itemFeed.pubDate

        podcastDownloadLinkView!!.setOnClickListener {
            startDownload(itemFeed, context)
        }

        podcastPlayButtonView!!.setOnClickListener {
            val playingMessage =
                "Reproduzindo"

            Toast.makeText(
                context,
                playingMessage,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun startDownload(itemFeed: ItemFeed, applicationContext: Context) {
        podcastDownloadLinkView!!.isEnabled = false
        val downloadService = Intent(applicationContext, DownloadPodcastEpisodeService::class.java)
        downloadService.data = Uri.parse(itemFeed.downloadLink)
        applicationContext.startService(downloadService)
    }

    override fun onClick(v: View) {
        val intent = Intent(v.context, EpisodeDetailActivity::class.java)

        intent.putExtra("item_title", podcastTitleView!!.text)

        ContextCompat.startActivity(v.context, intent, null)
    }
}