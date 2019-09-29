package br.ufpe.cin.android.podcast

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import br.ufpe.cin.android.podcast.adapters.ItemFeedsAdapter
import br.ufpe.cin.android.podcast.database.ItemFeedsDatabase
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.io.FileWriter
import java.net.URL


class MainActivity : AppCompatActivity() {
    var itemFeeds: List<ItemFeed>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createPodcastView()
    }

    fun createPodcastView() {
        doAsync {
            try {
                val rssFeed = downloadXMLFile()
                saveToDatabase(Parser.parse(rssFeed))
            } catch(e: Exception) {
                uiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "Não foi possível baixar. Carregando última lista...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            itemFeeds = getFromDatabase()

            uiThread {
                setupRecyclerView()
            }
        }
    }

    fun downloadXMLFile(): String {
        val xmlDownloadLink = getString(R.string.download_link)

        return URL(xmlDownloadLink).readText()
    }

    fun saveToDatabase(itemFeeds: List<ItemFeed>?) {
        val database = ItemFeedsDatabase.getDatabase(this@MainActivity)

        itemFeeds!!.forEach {
                itemFeed -> database.itemFeedsDao().insertItemFeeds(itemFeed)
        }
    }

    fun getFromDatabase(): List<ItemFeed> {
        val database = ItemFeedsDatabase.getDatabase(this@MainActivity)

        return database.itemFeedsDao().getAllItemFeeds()
    }

    fun setupRecyclerView() {
        listRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        listRecyclerView.adapter = ItemFeedsAdapter(itemFeeds!!, this@MainActivity)
        listRecyclerView.addItemDecoration(
            DividerItemDecoration(this@MainActivity, LinearLayoutManager.VERTICAL))
    }

    companion object {
        private val STORAGE_PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        private val WRITE_EXTERNAL_STORAGE_REQUEST = 710
    }
}
