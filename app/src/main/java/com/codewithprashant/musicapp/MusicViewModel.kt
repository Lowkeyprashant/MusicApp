// MusicViewModel.kt - Fixed Imports Version
package com.codewithprashant.musicapp

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MusicPlayerState(
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val isPaused: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val progress: Float = 0f,
    val volume: Float = 0.7f,
    val isShuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val playbackSpeed: Float = 1.0f,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class PlaylistState(
    val currentPlaylist: List<Song> = emptyList(),
    val currentIndex: Int = 0,
    val queue: List<Song> = emptyList(),
    val history: List<Song> = emptyList(),
    val shuffledIndices: List<Int> = emptyList()
)

data class LibraryState(
    val songs: List<Song> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val albums: List<Album> = emptyList(),
    val favorites: List<Song> = emptyList(),
    val recentlyPlayed: List<Song> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasPermission: Boolean = false,
    val scanProgress: Float = 0f
)

data class UIState(
    val selectedTab: Int = 0,
    val searchQuery: String = "",
    val selectedGenre: String = "",
    val sortOrder: SortOrder = SortOrder.TITLE_ASC,
    val isSearchActive: Boolean = false,
    val isPlayerExpanded: Boolean = false,
    val showEqualizer: Boolean = false
)

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "MusicViewModel"
    }

    private val context = getApplication<Application>()
    private val musicScanner = MusicScanner(context)

    // Service connection
    private var mediaPlayerService: MediaPlayerService? = null
    private var isServiceBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "MediaPlayerService connected")
            val binder = service as MediaPlayerService.MediaPlayerBinder
            mediaPlayerService = binder.getService()
            isServiceBound = true
            observeServiceState()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.w(TAG, "MediaPlayerService disconnected")
            mediaPlayerService = null
            isServiceBound = false
        }
    }

    // State flows
    private val _playerState = MutableStateFlow(MusicPlayerState())
    val playerState: StateFlow<MusicPlayerState> = _playerState.asStateFlow()

    private val _playlistState = MutableStateFlow(PlaylistState())
    val playlistState: StateFlow<PlaylistState> = _playlistState.asStateFlow()

    private val _libraryState = MutableStateFlow(LibraryState())
    val libraryState: StateFlow<LibraryState> = _libraryState.asStateFlow()

    private val _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    init {
        Log.d(TAG, "MusicViewModel initialized")
        checkPermissionsAndStartService()
    }

    private fun checkPermissionsAndStartService() {
        Log.d(TAG, "Checking permissions...")

        // Check permissions using the scanner
        val hasPermission = musicScanner.checkPermissions()

        Log.i(TAG, "Permission status: $hasPermission")
        _libraryState.value = _libraryState.value.copy(hasPermission = hasPermission)

        if (hasPermission) {
            Log.d(TAG, "Permissions granted, starting service and loading library")
            startMediaService()
            loadMusicLibrary()
        } else {
            Log.w(TAG, "Permissions not granted")
        }
    }

    fun requestPermissions() {
        Log.d(TAG, "Permissions granted by user, restarting initialization")
        checkPermissionsAndStartService()
    }

    private fun startMediaService() {
        Log.d(TAG, "Starting MediaPlayerService...")
        try {
            val serviceIntent = Intent(context, MediaPlayerService::class.java)
            context.startService(serviceIntent)
            val bindResult = context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
            Log.d(TAG, "Service bind result: $bindResult")
        } catch (e: Exception) {
            Log.e(TAG, "Error starting MediaPlayerService", e)
        }
    }

    private fun observeServiceState() {
        Log.d(TAG, "Setting up service state observation")
        mediaPlayerService?.let { service ->
            viewModelScope.launch {
                launch {
                    service.isPlaying.collect { isPlaying ->
                        Log.d(TAG, "Service playing state changed: $isPlaying")
                        _playerState.value = _playerState.value.copy(isPlaying = isPlaying)
                    }
                }

                launch {
                    service.currentPosition.collect { position ->
                        val duration = _playerState.value.duration
                        val progress = if (duration > 0) position.toFloat() / duration else 0f
                        _playerState.value = _playerState.value.copy(
                            currentPosition = position,
                            progress = progress
                        )
                    }
                }

                launch {
                    service.duration.collect { duration ->
                        Log.d(TAG, "Service duration changed: ${duration}ms")
                        _playerState.value = _playerState.value.copy(duration = duration)
                    }
                }

                launch {
                    service.currentSong.collect { song ->
                        if (song != null) {
                            Log.d(TAG, "Service current song changed: ${song.title}")
                            _playerState.value = _playerState.value.copy(currentSong = song)
                            addToRecentlyPlayed(song)
                        }
                    }
                }
            }
        }
    }

    private fun loadMusicLibrary() {
        Log.i(TAG, "Starting music library scan...")
        viewModelScope.launch {
            _libraryState.value = _libraryState.value.copy(isLoading = true, scanProgress = 0f)

            try {
                // Scan for songs
                Log.d(TAG, "Scanning for songs...")
                _libraryState.value = _libraryState.value.copy(scanProgress = 0.2f)
                val songs = musicScanner.scanForMusic()
                Log.i(TAG, "Found ${songs.size} songs")

                // If no songs found, provide detailed error info
                if (songs.isEmpty()) {
                    val errorMsg = "No music files found. Check if:\n" +
                            "1. Device has music files\n" +
                            "2. Permissions are granted\n" +
                            "3. Files are in supported formats"
                    Log.w(TAG, errorMsg)
                    _libraryState.value = _libraryState.value.copy(
                        isLoading = false,
                        error = errorMsg,
                        scanProgress = 1f
                    )
                    return@launch
                }

                // Scan for artists
                Log.d(TAG, "Scanning for artists...")
                _libraryState.value = _libraryState.value.copy(scanProgress = 0.5f)
                val artists = musicScanner.scanForArtists()
                Log.i(TAG, "Found ${artists.size} artists")

                // Scan for albums
                Log.d(TAG, "Scanning for albums...")
                _libraryState.value = _libraryState.value.copy(scanProgress = 0.8f)
                val albums = musicScanner.scanForAlbums()
                Log.i(TAG, "Found ${albums.size} albums")

                // Create default playlists
                val defaultPlaylists = createDefaultPlaylists(songs)
                Log.d(TAG, "Created ${defaultPlaylists.size} default playlists")

                _libraryState.value = _libraryState.value.copy(
                    songs = songs,
                    artists = artists,
                    albums = albums,
                    playlists = defaultPlaylists,
                    recentlyPlayed = songs.take(5),
                    isLoading = false,
                    scanProgress = 1f,
                    error = null
                )

                // Set up initial playlist
                _playlistState.value = _playlistState.value.copy(currentPlaylist = songs)

                Log.i(TAG, "Music library loaded successfully!")

            } catch (e: Exception) {
                Log.e(TAG, "Error loading music library", e)
                _libraryState.value = _libraryState.value.copy(
                    isLoading = false,
                    error = "Error loading music: ${e.message}",
                    scanProgress = 1f
                )
            }
        }
    }

    private fun createDefaultPlaylists(songs: List<Song>): List<Playlist> {
        val recentSongs = songs.sortedByDescending { it.dateAdded }.take(20)
        val favoriteSongs = songs.shuffled().take(15)

        return listOf(
            Playlist(
                id = "recently_added",
                name = "Recently Added",
                songs = recentSongs,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                isUserCreated = false
            ),
            Playlist(
                id = "favorites",
                name = "My Favorites",
                songs = favoriteSongs,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                isUserCreated = false
            )
        )
    }

    fun playSong(song: Song?) {
        Log.d(TAG, "Play song requested: ${song?.title}")
        song?.let {
            if (isServiceBound && mediaPlayerService != null) {
                Log.d(TAG, "Playing song through service: ${it.title}")
                Log.d(TAG, "Song file path: ${it.filePath}")
                mediaPlayerService?.playSong(it)
                updateCurrentPlaylistIndex(it)
            } else {
                Log.e(TAG, "Cannot play song - service not bound")
                _playerState.value = _playerState.value.copy(
                    error = "Music service not available"
                )
            }
        }
    }

    fun playPause() {
        Log.d(TAG, "Play/Pause requested")
        val currentState = _playerState.value
        if (currentState.currentSong == null) {
            Log.d(TAG, "No current song, playing first available")
            val firstSong = _libraryState.value.songs.firstOrNull()
            if (firstSong != null) {
                playSong(firstSong)
            } else {
                Log.w(TAG, "No songs available to play")
            }
        } else {
            if (currentState.isPlaying) {
                Log.d(TAG, "Pausing music")
                mediaPlayerService?.pauseMusic()
            } else {
                Log.d(TAG, "Resuming music")
                mediaPlayerService?.resumeMusic()
            }
        }
    }

    fun skipToNext() {
        Log.d(TAG, "Skip to next requested")
        val playlistState = _playlistState.value
        val playerState = _playerState.value

        if (playlistState.currentPlaylist.isNotEmpty()) {
            val nextIndex = if (playerState.isShuffleEnabled) {
                getNextShuffledIndex()
            } else {
                when (playerState.repeatMode) {
                    RepeatMode.ONE -> playlistState.currentIndex
                    RepeatMode.ALL -> (playlistState.currentIndex + 1) % playlistState.currentPlaylist.size
                    RepeatMode.OFF -> {
                        val next = playlistState.currentIndex + 1
                        if (next < playlistState.currentPlaylist.size) next else -1
                    }
                }
            }

            if (nextIndex >= 0) {
                val nextSong = playlistState.currentPlaylist[nextIndex]
                Log.d(TAG, "Playing next song: ${nextSong.title}")
                _playlistState.value = playlistState.copy(currentIndex = nextIndex)
                mediaPlayerService?.playSong(nextSong)
            } else {
                Log.d(TAG, "No next song available")
            }
        } else {
            Log.w(TAG, "No playlist available for next")
        }
    }

    fun skipToPrevious() {
        Log.d(TAG, "Skip to previous requested")
        val playlistState = _playlistState.value
        val currentPosition = _playerState.value.currentPosition

        if (currentPosition > 3000) {
            Log.d(TAG, "Restarting current song")
            mediaPlayerService?.seekTo(0)
            return
        }

        if (playlistState.currentPlaylist.isNotEmpty()) {
            val prevIndex = if (playlistState.currentIndex > 0) {
                playlistState.currentIndex - 1
            } else {
                playlistState.currentPlaylist.size - 1
            }

            val prevSong = playlistState.currentPlaylist[prevIndex]
            Log.d(TAG, "Playing previous song: ${prevSong.title}")
            _playlistState.value = playlistState.copy(currentIndex = prevIndex)
            mediaPlayerService?.playSong(prevSong)
        }
    }

    fun seekTo(position: Float) {
        val duration = _playerState.value.duration
        val seekPosition = (duration * position).toLong()
        Log.d(TAG, "Seeking to position: ${seekPosition}ms (${position * 100}%)")
        mediaPlayerService?.seekTo(seekPosition)
    }

    fun setVolume(volume: Float) {
        Log.d(TAG, "Setting volume to: $volume")
        _playerState.value = _playerState.value.copy(volume = volume)
    }

    fun toggleShuffle() {
        val newShuffleState = !_playerState.value.isShuffleEnabled
        Log.d(TAG, "Toggling shuffle: $newShuffleState")
        _playerState.value = _playerState.value.copy(isShuffleEnabled = newShuffleState)

        if (newShuffleState) {
            generateShuffledIndices()
        }
    }

    fun toggleRepeat() {
        val currentMode = _playerState.value.repeatMode
        val nextMode = when (currentMode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        Log.d(TAG, "Toggling repeat: $currentMode -> $nextMode")
        _playerState.value = _playerState.value.copy(repeatMode = nextMode)
    }

    private fun generateShuffledIndices() {
        val playlistSize = _playlistState.value.currentPlaylist.size
        val shuffledIndices = (0 until playlistSize).shuffled()
        _playlistState.value = _playlistState.value.copy(shuffledIndices = shuffledIndices)
        Log.d(TAG, "Generated shuffled indices for $playlistSize songs")
    }

    private fun getNextShuffledIndex(): Int {
        val playlistState = _playlistState.value
        val currentIndex = playlistState.currentIndex
        val shuffledIndices = playlistState.shuffledIndices

        if (shuffledIndices.isEmpty()) {
            generateShuffledIndices()
            return _playlistState.value.shuffledIndices.firstOrNull() ?: 0
        }

        val currentShuffledPosition = shuffledIndices.indexOf(currentIndex)
        return if (currentShuffledPosition < shuffledIndices.size - 1) {
            shuffledIndices[currentShuffledPosition + 1]
        } else {
            shuffledIndices.first()
        }
    }

    private fun updateCurrentPlaylistIndex(song: Song) {
        val playlistState = _playlistState.value
        val index = playlistState.currentPlaylist.indexOf(song)
        if (index != -1) {
            _playlistState.value = playlistState.copy(currentIndex = index)
            Log.d(TAG, "Updated playlist index to: $index")
        }
    }

    private fun addToRecentlyPlayed(song: Song) {
        val currentRecentlyPlayed = _libraryState.value.recentlyPlayed.toMutableList()

        currentRecentlyPlayed.removeAll { it.id == song.id }
        currentRecentlyPlayed.add(0, song)

        if (currentRecentlyPlayed.size > 20) {
            currentRecentlyPlayed.removeAt(currentRecentlyPlayed.size - 1)
        }

        _libraryState.value = _libraryState.value.copy(recentlyPlayed = currentRecentlyPlayed)
        Log.d(TAG, "Added ${song.title} to recently played")
    }

    fun updateSelectedTab(tab: Int) {
        Log.d(TAG, "Selected tab changed to: $tab")
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun toggleSearch() {
        _uiState.value = _uiState.value.copy(
            isSearchActive = !_uiState.value.isSearchActive
        )
    }

    fun createPlaylist(name: String, songs: List<Song> = emptyList()) {
        Log.d(TAG, "Creating playlist: $name with ${songs.size} songs")
        val newPlaylist = Playlist(
            id = System.currentTimeMillis().toString(),
            name = name,
            songs = songs,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        val currentPlaylists = _libraryState.value.playlists
        _libraryState.value = _libraryState.value.copy(
            playlists = currentPlaylists + newPlaylist
        )
    }

    fun addToFavorites(song: Song) {
        val currentFavorites = _libraryState.value.favorites
        if (!currentFavorites.contains(song)) {
            _libraryState.value = _libraryState.value.copy(
                favorites = currentFavorites + song
            )
            Log.d(TAG, "Added ${song.title} to favorites")
        }
    }

    fun removeFromFavorites(song: Song) {
        val currentFavorites = _libraryState.value.favorites
        _libraryState.value = _libraryState.value.copy(
            favorites = currentFavorites - song
        )
        Log.d(TAG, "Removed ${song.title} from favorites")
    }

    fun playPlaylist(playlist: Playlist) {
        Log.d(TAG, "Playing playlist: ${playlist.name} with ${playlist.songs.size} songs")
        if (playlist.songs.isNotEmpty()) {
            _playlistState.value = _playlistState.value.copy(
                currentPlaylist = playlist.songs,
                currentIndex = 0
            )
            mediaPlayerService?.playSong(playlist.songs.first())
        }
    }

    fun playArtistSongs(artist: Artist) {
        Log.d(TAG, "Playing songs for artist: ${artist.name}")
        val artistSongs = _libraryState.value.songs.filter { it.artistId == artist.id }
        if (artistSongs.isNotEmpty()) {
            _playlistState.value = _playlistState.value.copy(
                currentPlaylist = artistSongs,
                currentIndex = 0
            )
            mediaPlayerService?.playSong(artistSongs.first())
        }
    }

    fun playAlbumSongs(album: Album) {
        Log.d(TAG, "Playing songs for album: ${album.title}")
        val albumSongs = _libraryState.value.songs.filter { it.albumId == album.id }
            .sortedBy { it.trackNumber }
        if (albumSongs.isNotEmpty()) {
            _playlistState.value = _playlistState.value.copy(
                currentPlaylist = albumSongs,
                currentIndex = 0
            )
            mediaPlayerService?.playSong(albumSongs.first())
        }
    }

    fun refreshLibrary() {
        Log.d(TAG, "Refreshing music library")
        if (_libraryState.value.hasPermission) {
            loadMusicLibrary()
        }
    }

    // Debug function to manually check state
    fun debugPrintState() {
        Log.d(TAG, "=== DEBUG STATE ===")
        Log.d(TAG, "Library has permission: ${_libraryState.value.hasPermission}")
        Log.d(TAG, "Library is loading: ${_libraryState.value.isLoading}")
        Log.d(TAG, "Songs count: ${_libraryState.value.songs.size}")
        Log.d(TAG, "Library error: ${_libraryState.value.error}")
        Log.d(TAG, "Service bound: $isServiceBound")
        Log.d(TAG, "Current song: ${_playerState.value.currentSong?.title}")
        Log.d(TAG, "Is playing: ${_playerState.value.isPlaying}")
        Log.d(TAG, "Player error: ${_playerState.value.error}")
        Log.d(TAG, "=================")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel cleared, unbinding service")
        if (isServiceBound) {
            context.unbindService(serviceConnection)
            isServiceBound = false
        }
    }
}