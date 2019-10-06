package br.ufpe.cin.android.podcast.services;

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.ufpe.cin.android.podcast.ItemFeed
import br.ufpe.cin.android.podcast.database.ItemFeedsDatabase
import org.jetbrains.anko.doAsync
import java.io.File

class PodcastPlayerWithBindingService : Service() {
    private var mediaPlayer: MediaPlayer? = null

    private val podcastBinder = PodcastBinder()

    private var currentlyPlayingPodcastEpisode: ItemFeed? = null

    override fun onCreate() {
        super.onCreate()

        mediaPlayer = MediaPlayer()
        mediaPlayer?.isLooping = true

        mediaPlayer?.setOnCompletionListener {
            deleteEpisode()
        }

        createForegroundNotification()
    }

    private fun deleteEpisode() {
        val episodeFile = File(currentlyPlayingPodcastEpisode!!.downloadPath!!)

        episodeFile.delete()

        currentlyPlayingPodcastEpisode!!.downloadPath = null

        updateEpisodeInDatabase(currentlyPlayingPodcastEpisode!!)

        currentlyPlayingPodcastEpisode = null

        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(
            DownloadPodcastEpisodeService.UPDATE_FEED_ACTION
        ))
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        super.onDestroy()
    }

    fun playOrPause (podcastEpisode: ItemFeed) {
        if (podcastEpisode != currentlyPlayingPodcastEpisode) {
            changeEpisodeTo(podcastEpisode)
        }

        playback()
    }

    private fun changeEpisodeTo(podcastEpisode: ItemFeed) {
        if (currentlyPlayingPodcastEpisode != null) {
            saveEpisodeCurrentPosition()
        }

        loadEpisodeToMediaPlayer(podcastEpisode)
    }

    private fun saveEpisodeCurrentPosition() {
        currentlyPlayingPodcastEpisode!!.currentPosition =
            mediaPlayer!!.currentPosition

        updateEpisodeInDatabase(currentlyPlayingPodcastEpisode!!)
    }

    private fun loadEpisodeToMediaPlayer(podcastEpisode: ItemFeed) {
        currentlyPlayingPodcastEpisode = podcastEpisode

        mediaPlayer!!.reset()
        val episodeFile = File(podcastEpisode.downloadPath!!)
        mediaPlayer!!.setDataSource(applicationContext, Uri.fromFile(episodeFile))
        mediaPlayer!!.prepare()
        mediaPlayer!!.seekTo(podcastEpisode.currentPosition ?: 0)
    }

    private fun playback() {
        if (!mediaPlayer!!.isPlaying) {
            mediaPlayer!!.start()
        } else {
            saveEpisodeCurrentPosition()
            mediaPlayer!!.pause()
        }
    }

    inner class PodcastBinder : Binder() {
        internal val service: PodcastPlayerWithBindingService
            get() = this@PodcastPlayerWithBindingService
    }

    override fun onBind(intent: Intent): IBinder {
        return podcastBinder
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val mChannel =
                NotificationChannel(
                    "1",
                    "Canal de Notificacoes",
                    NotificationManager.IMPORTANCE_DEFAULT)

            mChannel.description = "Descricao"

            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(mChannel)
        }
    }

    private fun updateEpisodeInDatabase(itemFeed: ItemFeed) {
        doAsync {
            val itemFeedsDatabase = ItemFeedsDatabase.getDatabase(applicationContext)
            itemFeedsDatabase.itemFeedsDao().updateItemFeed(itemFeed)
        }
    }

    private fun createForegroundNotification() {
        createChannel()

        val notificationIntent =
            Intent(applicationContext, PodcastPlayerWithBindingService::class.java)

        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val notification = NotificationCompat.Builder(
            applicationContext,"1")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true).setContentTitle("Music Service rodando")
            .setContentText("Clique para acessar o player!")
            .setContentIntent(pendingIntent).build()

        startForeground(NOTIFICATION_ID, notification)
    }

    companion object {
        private val NOTIFICATION_ID = 2
    }
}

