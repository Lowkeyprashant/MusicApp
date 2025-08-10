// MediaPlayerService.kt - Core Music Playback Service
package com.codewithprashant.musicapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.IOException

class MediaPlayerService : Service() {

    private val binder = MediaPlayerBinder()
    private var mediaPlayer: MediaPlayer? = null
    private var mediaSession: MediaSessionCompat? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // State flows for UI updates
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong

    private var positionUpdateJob: Job? = null

    companion object {
        const val CHANNEL_ID = "music_player_channel"
        const val NOTIFICATION_ID = 1

        // Intent actions
        const val ACTION_PLAY = "action_play"
        const val ACTION_PAUSE = "action_pause"
        const val ACTION_NEXT = "action_next"
        const val ACTION_PREVIOUS = "action_previous"
        const val ACTION_STOP = "action_stop"
    }

    inner class MediaPlayerBinder : Binder() {
        fun getService(): MediaPlayerService = this@MediaPlayerService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        initializeMediaSession()
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handleIntent(intent)
        return START_STICKY
    }

    private fun handleIntent(intent: Intent?) {
        intent?.action?.let { action ->
            when (action) {
                ACTION_PLAY -> resumeMusic()
                ACTION_PAUSE -> pauseMusic()
                ACTION_NEXT -> skipToNext()
                ACTION_PREVIOUS -> skipToPrevious()
                ACTION_STOP -> stopMusic()
            }
        }
    }

    private fun initializeMediaSession() {
        mediaSession = MediaSessionCompat(this, "MusicPlayerSession").apply {
            setPlaybackState(
                PlaybackStateCompat.Builder()
                    .setActions(
                        PlaybackStateCompat.ACTION_PLAY or
                                PlaybackStateCompat.ACTION_PAUSE or
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                                PlaybackStateCompat.ACTION_STOP
                    )
                    .setState(PlaybackStateCompat.STATE_NONE, 0, 1.0f)
                    .build()
            )
            isActive = true
        }
    }

    fun playSong(song: Song) {
        try {
            // Release previous media player
            mediaPlayer?.release()

            // Create new media player
            mediaPlayer = MediaPlayer().apply {
                setDataSource(song.filePath)
                setOnPreparedListener { mp ->
                    mp.start()
                    _isPlaying.value = true
                    _duration.value = mp.duration.toLong()
                    startPositionUpdates()
                    updateNotification()
                    updateMediaSessionPlaybackState()
                }
                setOnCompletionListener {
                    onSongComplete()
                }
                setOnErrorListener { _, what, extra ->
                    handleMediaPlayerError(what, extra)
                    false
                }
                prepareAsync()
            }

            _currentSong.value = song

        } catch (e: IOException) {
            e.printStackTrace()
            handleMediaPlayerError(0, 0)
        }
    }

    fun pauseMusic() {
        mediaPlayer?.let { mp ->
            if (mp.isPlaying) {
                mp.pause()
                _isPlaying.value = false
                stopPositionUpdates()
                updateNotification()
                updateMediaSessionPlaybackState()
            }
        }
    }

    fun resumeMusic() {
        mediaPlayer?.let { mp ->
            if (!mp.isPlaying) {
                mp.start()
                _isPlaying.value = true
                startPositionUpdates()
                updateNotification()
                updateMediaSessionPlaybackState()
            }
        }
    }

    fun stopMusic() {
        mediaPlayer?.let { mp ->
            mp.stop()
            mp.release()
        }
        mediaPlayer = null
        _isPlaying.value = false
        _currentPosition.value = 0
        _currentSong.value = null
        stopPositionUpdates()
        stopForeground(true)
    }

    fun seekTo(position: Long) {
        mediaPlayer?.seekTo(position.toInt())
        _currentPosition.value = position
    }

    fun skipToNext() {
        // This should be handled by the ViewModel with playlist logic
        // For now, just stop current song
        stopMusic()
    }

    fun skipToPrevious() {
        // This should be handled by the ViewModel with playlist logic
        // Restart current song if position > 3 seconds, otherwise go to previous
        if (_currentPosition.value > 3000) {
            seekTo(0)
        } else {
            stopMusic()
        }
    }

    private fun startPositionUpdates() {
        positionUpdateJob = serviceScope.launch {
            while (_isPlaying.value && mediaPlayer != null) {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        _currentPosition.value = mp.currentPosition.toLong()
                    }
                }
                delay(1000) // Update every second
            }
        }
    }

    private fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }

    private fun onSongComplete() {
        _isPlaying.value = false
        _currentPosition.value = 0
        stopPositionUpdates()
        // Notify ViewModel to play next song
    }

    private fun handleMediaPlayerError(what: Int, extra: Int) {
        _isPlaying.value = false
        stopPositionUpdates()
        // Notify ViewModel about error
    }

    private fun updateMediaSessionPlaybackState() {
        val state = if (_isPlaying.value) {
            PlaybackStateCompat.STATE_PLAYING
        } else {
            PlaybackStateCompat.STATE_PAUSED
        }

        mediaSession?.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY or
                            PlaybackStateCompat.ACTION_PAUSE or
                            PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                            PlaybackStateCompat.ACTION_STOP
                )
                .setState(state, _currentPosition.value, 1.0f)
                .build()
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Player",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Music Player Controls"
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun updateNotification() {
        val song = _currentSong.value ?: return

        val playPauseAction = if (_isPlaying.value) {
            NotificationCompat.Action(
                R.drawable.ic_pause_24,
                "Pause",
                getPendingIntent(ACTION_PAUSE)
            )
        } else {
            NotificationCompat.Action(
                R.drawable.ic_play_arrow_24,
                "Play",
                getPendingIntent(ACTION_PLAY)
            )
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(song.title)
            .setContentText(song.artist)
            .setSmallIcon(R.drawable.ic_music_note_24)
            .setLargeIcon(getAlbumArt(song))
            .addAction(R.drawable.ic_skip_previous_24, "Previous", getPendingIntent(ACTION_PREVIOUS))
            .addAction(playPauseAction)
            .addAction(R.drawable.ic_skip_next_24, "Next", getPendingIntent(ACTION_NEXT))
            .setStyle(
                MediaStyle()
                    .setMediaSession(mediaSession?.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .setContentIntent(getContentPendingIntent())
            .setDeleteIntent(getPendingIntent(ACTION_STOP))
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun getAlbumArt(song: Song): Bitmap? {
        return try {
            if (song.albumArt.isNotEmpty()) {
                BitmapFactory.decodeFile(song.albumArt)
            } else {
                BitmapFactory.decodeResource(resources, R.drawable.default_album_art)
            }
        } catch (e: Exception) {
            BitmapFactory.decodeResource(resources, R.drawable.default_album_art)
        }
    }

    private fun getPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, MediaPlayerService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            this,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun getContentPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java)
        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        stopMusic()
        mediaSession?.release()
        serviceScope.cancel()
    }
}