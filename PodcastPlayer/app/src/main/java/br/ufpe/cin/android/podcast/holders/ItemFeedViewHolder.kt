package br.ufpe.cin.android.podcast.holders

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.net.Uri
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import br.ufpe.cin.android.podcast.EpisodeDetailActivity
import br.ufpe.cin.android.podcast.ItemFeed
import br.ufpe.cin.android.podcast.R
import br.ufpe.cin.android.podcast.services.DownloadPodcastEpisodeService
import br.ufpe.cin.android.podcast.services.PodcastPlayerWithBindingService

class ItemFeedViewHolder (
    item: View,
    val podcastPlayerWithBindingService: PodcastPlayerWithBindingService?):
    RecyclerView.ViewHolder(item), View.OnClickListener {

    var podcastTitleView: TextView? = null
    var podcastPubDateView: TextView? = null
    var podcastImageButtonView: ImageButton? = null

    init {
        podcastTitleView = itemView.findViewById(R.id.item_title)
        podcastPubDateView = itemView.findViewById(R.id.item_date)
        podcastImageButtonView = itemView.findViewById(R.id.item_action)
    }

    override fun onClick(v: View) {}

    fun bind(itemFeed : ItemFeed, context: Context) {
        val playIcon = Icon.createWithResource(context, R.drawable.playicon)
        val downloadIcon = Icon.createWithResource(context, R.drawable.downloadicon)

        if (itemFeed.isDownloaded()) {
            podcastImageButtonView!!.setImageIcon(playIcon)

            podcastImageButtonView!!.setOnClickListener {
                startPlayingEpisode(itemFeed)
            }
        } else {
            podcastImageButtonView!!.setImageIcon(downloadIcon)

            podcastImageButtonView!!.setOnClickListener {
                startDownload(itemFeed, context)
            }
        }

        podcastPubDateView?.text = itemFeed.pubDate

        podcastTitleView?.text = itemFeed.title
        podcastTitleView?.setOnClickListener {
            startEpisodeDetailActivity(context)
        }
    }

    private fun startEpisodeDetailActivity(context: Context) {
        val intent = Intent(context, EpisodeDetailActivity::class.java)
        intent.putExtra("item_title", podcastTitleView!!.text)
        startActivity(context, intent, null)
    }

    private fun startDownload(itemFeed: ItemFeed, applicationContext: Context) {
        val downloadPodcastEpisodeService =
            Intent(applicationContext, DownloadPodcastEpisodeService::class.java)

        downloadPodcastEpisodeService.data = Uri.parse(itemFeed.title)

        applicationContext.startService(downloadPodcastEpisodeService)
    }

    private fun startPlayingEpisode(episode: ItemFeed) {
        podcastPlayerWithBindingService?.playOrPause(episode)
    }
}