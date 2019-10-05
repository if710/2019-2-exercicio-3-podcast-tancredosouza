package br.ufpe.cin.android.podcast.services

import android.app.IntentService
import android.content.Intent
import android.net.Uri
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.ufpe.cin.android.podcast.database.ItemFeedsDatabase
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class DownloadPodcastEpisodeService : IntentService("DownloadPodcastEpisodeService") {
    public override fun onHandleIntent(i: Intent?) {
        try {
            val itemFeedKey: String = i?.data?.toString()!!

            val itemFeedsDatabase = ItemFeedsDatabase.getDatabase(applicationContext)

            val itemFeed = itemFeedsDatabase.itemFeedsDao().getItemFeed(itemFeedKey)

            val downloadUri = Uri.parse(itemFeed.downloadLink)

            val root = getExternalFilesDir(DIRECTORY_DOWNLOADS)
            root?.mkdirs()

            val output = File(root, downloadUri.lastPathSegment!!)
            if (output.exists()) {
                output.delete()
            }
            val url = URL(itemFeed.downloadLink)

            val c = url.openConnection() as HttpURLConnection
            val fos = FileOutputStream(output.path)
            val out = BufferedOutputStream(fos)
            try {
                val `in` = c.inputStream
                val buffer = ByteArray(8192)
                var len = `in`.read(buffer)
                while (len >= 0) {
                    out.write(buffer, 0, len)
                    len = `in`.read(buffer)
                }
                out.flush()

                itemFeed.downloadPath = output.path

                itemFeedsDatabase.itemFeedsDao().updateItemFeed(itemFeed)

                LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(DOWNLOAD_COMPLETE))
            } finally {
                fos.fd.sync()
                out.close()
                c.disconnect()
            }
        } catch (e2: IOException) {
            Log.e(javaClass.getName(), "Exception durante download", e2)
        }

    }

    companion object {
        val DOWNLOAD_COMPLETE = "br.ufpe.cin.if710.services.action.DOWNLOAD_COMPLETE"
    }
}