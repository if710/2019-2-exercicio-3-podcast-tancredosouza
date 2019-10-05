package br.ufpe.cin.android.podcast

import android.Manifest
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import br.ufpe.cin.android.podcast.adapters.ItemFeedsAdapter
import br.ufpe.cin.android.podcast.database.ItemFeedsDatabase
import br.ufpe.cin.android.podcast.services.DownloadPodcastEpisodeService.Companion.DOWNLOAD_COMPLETE
import br.ufpe.cin.android.podcast.services.PodcastPlayerWithBindingService
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL


class MainActivity : AppCompatActivity() {
    private var sharedPreferences: SharedPreferences? = null
    private var podcastEpisodes: List<ItemFeed>? = null

    internal var podcastPlayerWithBindingService: PodcastPlayerWithBindingService? = null
    internal var isBound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LocalBroadcastManager
            .getInstance(this).registerReceiver(receiver, receiver.intentFilter)

        setContentView(R.layout.activity_main)

        sharedPreferences = defaultSharedPreferences

        createPodcastView()

        val podcastPlayerIntent =
            Intent(this, PodcastPlayerWithBindingService::class.java)
        startService(podcastPlayerIntent)
    }

    override fun onStart() {
        super.onStart()

        if (!isBound) {
            val bindIntent =
                Intent(this,PodcastPlayerWithBindingService::class.java)

            isBound =
                bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        unbindService(serviceConnection)
        isBound = false
        super.onStop()
    }

    private fun createPodcastView() {
        doAsync {
            try {
                getItemFeedsFromDatabase()

                if (podcastEpisodes!!.isEmpty()) {
                    fetchXMLContent()
                }

            } catch (e: Exception) {
                uiThread {
                    toastFetchingStoredContentMessage()
                }
            }

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
        podcastEpisodes = database.itemFeedsDao().getAllItemFeeds()
    }

    private fun createPodcastEpisodesRecyclerView() {
        listRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = ItemFeedsAdapter(
                podcastEpisodes!!,
                this@MainActivity,
                podcastPlayerWithBindingService)
            addItemDecoration(
                DividerItemDecoration(this@MainActivity, LinearLayoutManager.VERTICAL)
            )
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {
            podcastPlayerWithBindingService = null
            isBound = false
        }

        override fun onServiceConnected(p0: ComponentName?, b: IBinder?) {
            val binder = b as PodcastPlayerWithBindingService.PodcastBinder
            podcastPlayerWithBindingService = binder.service
            isBound = true
        }
    }

    private val receiver = object : BroadcastReceiver() {
        val intentFilter: IntentFilter
            get() {
                val intentFilter = IntentFilter()
                intentFilter.addAction(DOWNLOAD_COMPLETE)
                return intentFilter
            }

        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            Log.d ("MainActivityReceiver", action)
            if (action == DOWNLOAD_COMPLETE) {
                createPodcastView()
            }
        }
    }
}
