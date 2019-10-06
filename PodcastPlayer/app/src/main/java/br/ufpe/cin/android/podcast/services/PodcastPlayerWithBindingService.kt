package br.ufpe.cin.android.podcast.services;

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import br.ufpe.cin.android.podcast.ItemFeed
import java.io.File

class PodcastPlayerWithBindingService : Service() {
    private var mediaPlayer: MediaPlayer? = null

    private val podcastBinder = PodcastBinder()

    override fun onCreate() {
        super.onCreate()

        mediaPlayer = MediaPlayer()

        mediaPlayer?.isLooping = false

        createForegroundNotification()
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

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        super.onDestroy()
    }

    fun playOrPause (podcastEpisode: ItemFeed) {
        val episodeFile = File(podcastEpisode.downloadPath!!)
        if (!mediaPlayer!!.isPlaying) {
            Log.v("PLAY", "playing: $episodeFile")
            mediaPlayer!!.setDataSource(applicationContext, Uri.fromFile(episodeFile))
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
        } else {
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

    fun createChannel() {
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

    companion object {
        private val NOTIFICATION_ID = 2
    }
}

