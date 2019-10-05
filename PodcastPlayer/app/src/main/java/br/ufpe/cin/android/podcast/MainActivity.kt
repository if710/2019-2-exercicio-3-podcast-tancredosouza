package br.ufpe.cin.android.podcast

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import br.ufpe.cin.android.podcast.adapters.ItemFeedsAdapter
import br.ufpe.cin.android.podcast.database.ItemFeedsDatabase
import br.ufpe.cin.android.podcast.services.DownloadPodcastEpisodeService
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL


class MainActivity : AppCompatActivity() {
    var itemFeeds: List<ItemFeed>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createPodcastView()
    }

    private fun createPodcastView() {
        doAsync {
            try {
                fetchXMLContent()
            } catch (e: Exception) {
                uiThread {
                    toastFetchingStoredContentMessage()
                }
            }

            getItemFeedsFromDatabase()

            uiThread {
                createPodcastEpisodesRecyclerView()
            }
        }
    }

    private fun fetchXMLContent() {
        val rssFeed = downloadXMLContent()
        storeContent(Parser.parse(rssFeed))
    }

    private fun downloadXMLContent(): String {
        val xmlDownloadLink = getString(R.string.download_link)
        val xmlContent = URL(xmlDownloadLink).readText()

        return xmlContent
    }

    private fun toastFetchingStoredContentMessage() {
        val failedDownloadMessage =
            "Não foi possível baixar. Carregando última lista..."

        Toast.makeText(
            this@MainActivity,
            failedDownloadMessage,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun storeContent(itemFeeds: List<ItemFeed>?) {
        val database = ItemFeedsDatabase.getDatabase(this@MainActivity)

        itemFeeds!!.forEach {
                itemFeed -> database.itemFeedsDao().insertItemFeeds(itemFeed)
        }
    }

    private fun getItemFeedsFromDatabase() {
        val database = ItemFeedsDatabase.getDatabase(this@MainActivity)
        itemFeeds = database.itemFeedsDao().getAllItemFeeds()
    }

    private fun createPodcastEpisodesRecyclerView() {
        listRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = ItemFeedsAdapter(itemFeeds!!, this@MainActivity)
            addItemDecoration(
                DividerItemDecoration(this@MainActivity, LinearLayoutManager.VERTICAL)
            )
        }
    }

    companion object {
        private val STORAGE_PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        private val WRITE_EXTERNAL_STORAGE_REQUEST = 710
    }
}
