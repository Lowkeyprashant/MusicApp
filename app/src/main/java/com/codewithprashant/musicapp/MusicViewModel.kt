// MusicViewModel.kt - Updated with Media Service Integration
package com.codewithprashant.musicapp

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

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

    private val context = getApplication<Application>()
    private val musicScanner = MusicScanner(context)

    // Service connection
    private var mediaPlayerService: MediaPlayerService? = null
    private var isServiceBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MediaPlayerService.MediaPlayerBinder
            mediaPlayerService = binder.getService()
            isServiceBound = true
            observeServiceState()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
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
        checkPermissionsAndStartService()
    }

    private fun checkPermissionsAndStartService() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        _libraryState.value = _libraryState.value.copy(hasPermission = hasPermission)

        if (hasPermission) {
            startMediaService()
            loadMusicLibrary()
        }
    }

    fun requestPermissions() {
        // This will be called from the UI when permission is granted
        checkPermissionsAndStartService()
    }

    private fun startMediaService() {
        val serviceIntent = Intent(context, MediaPlayerService::class.java)
        context.startService(serviceIntent)
        context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun observeServiceState() {
        mediaPlayerService?.let { service ->
            viewModelScope.launch {
                // Observe service state flows
                launch {
                    service.isPlaying.collect { isPlaying ->
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
                        _playerState.value = _playerState.value.copy(duration = duration)
                    }
                }

                launch {
                    service.currentSong.collect { song ->
                        if (song != null) {
                            _playerState.value = _playerState.value.copy(currentSong = song)
                            addToRecentlyPlayed(song)
                        }
                    }
                }
            }
        }
    }

    private fun loadMusicLibrary() {
        viewModelScope.launch {
            _libraryState.value = _libraryState.value.copy(isLoading = true, scanProgress = 0f)

            try {
                // Scan for songs
                _libraryState.value = _libraryState.value.copy(scanProgress = 0.2f)
                val songs = musicScanner.scanForMusic()

                // Scan for artists
                _libraryState.value = _libraryState.value.copy(scanProgress = 0.5f)
                val artists = musicScanner.scanForArtists()

                // Scan for albums
                _libraryState.value = _libraryState.value.copy(scanProgress = 0.8f)
                val albums = musicScanner.scanForAlbums()

                // Create default playlists
                val defaultPlaylists = createDefaultPlaylists(songs)

                _libraryState.value = _libraryState.value.copy(
                    songs = songs,
                    artists = artists,
                    albums = albums,
                    playlists = defaultPlaylists,
                    recentlyPlayed = songs.take(5), // Show first 5 as recently played initially
                    isLoading = false,
                    scanProgress = 1f,
                    error = null
                )

                // Set up initial playlist
                _playlistState.value = _playlistState.value.copy(currentPlaylist = songs)

            } catch (e: Exception) {
                _libraryState.value = _libraryState.value.copy(
                    isLoading = false,
                    error = "Error loading music: ${e.message}"
                )
            }
        }
    }

    private fun createDefaultPlaylists(songs: List<Song>): List<Playlist> {
        val recentSongs = songs.sortedByDescending { it.dateAdded }.take(20)
        val favoriteSongs = songs.shuffled().take(15) // Random songs as favorites initially

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
        song?.let {
            mediaPlayerService?.playSong(it)
            updateCurrentPlaylistIndex(it)
        }
    }

    fun playPause() {
        val currentState = _playerState.value
        if (currentState.currentSong == null) {
            // Play first song if none is selected
            val firstSong = _libraryState.value.songs.firstOrNull()
            firstSong?.let { playSong(it) }
        } else {
            if (currentState.isPlaying) {
                mediaPlayerService?.pauseMusic()
            } else {
                mediaPlayerService?.resumeMusic()
            }
        }
    }

    fun skipToNext() {
        val playlistState = _playlistState.value
        val playerState = _playerState.value

        if (playlistState.currentPlaylist.isNotEmpty()) {
            val nextIndex = if (playerState.isShuffleEnabled) {
                getNextShuffledIndex()
            } else {
                when (playerState.repeatMode) {
                    RepeatMode.ONE -> playlistState.currentIndex // Stay on same song
                    RepeatMode.ALL -> (playlistState.currentIndex + 1) % playlistState.currentPlaylist.size
                    RepeatMode.OFF -> {
                        val next = playlistState.currentIndex + 1
                        if (next < playlistState.currentPlaylist.size) next else -1
                    }
                }
            }

            if (nextIndex >= 0) {
                val nextSong = playlistState.currentPlaylist[nextIndex]
                _playlistState.value = playlistState.copy(currentIndex = nextIndex)
                mediaPlayerService?.playSong(nextSong)
            }
        }
    }

    fun skipToPrevious() {
        val playlistState = _playlistState.value
        val currentPosition = _playerState.value.currentPosition

        // If more than 3 seconds into song, restart current song
        if (currentPosition > 3000) {
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
            _playlistState.value = playlistState.copy(currentIndex = prevIndex)
            mediaPlayerService?.playSong(prevSong)
        }
    }

    fun seekTo(position: Float) {
        val duration = _playerState.value.duration
        val seekPosition = (duration * position).toLong()
        mediaPlayerService?.seekTo(seekPosition)
    }

    fun setVolume(volume: Float) {
        _playerState.value = _playerState.value.copy(volume = volume)
        // Implement volume control in service if needed
    }

    fun toggleShuffle() {
        val newShuffleState = !_playerState.value.isShuffleEnabled
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
        _playerState.value = _playerState.value.copy(repeatMode = nextMode)
    }

    private fun generateShuffledIndices() {
        val playlistSize = _playlistState.value.currentPlaylist.size
        val shuffledIndices = (0 until playlistSize).shuffled()
        _playlistState.value = _playlistState.value.copy(shuffledIndices = shuffledIndices)
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
            shuffledIndices.first() // Loop back to beginning
        }
    }

    private fun updateCurrentPlaylistIndex(song: Song) {
        val playlistState = _playlistState.value
        val index = playlistState.currentPlaylist.indexOf(song)
        if (index != -1) {
            _playlistState.value = playlistState.copy(currentIndex = index)
        }
    }

    private fun addToRecentlyPlayed(song: Song) {
        val currentRecentlyPlayed = _libraryState.value.recentlyPlayed.toMutableList()

        // Remove if already in list
        currentRecentlyPlayed.removeAll { it.id == song.id }

        // Add to beginning
        currentRecentlyPlayed.add(0, song)

        // Keep only last 20 songs
        if (currentRecentlyPlayed.size > 20) {
            currentRecentlyPlayed.removeAt(currentRecentlyPlayed.size - 1)
        }

        _libraryState.value = _libraryState.value.copy(recentlyPlayed = currentRecentlyPlayed)
    }

    fun updateSelectedTab(tab: Int) {
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
        }
    }

    fun removeFromFavorites(song: Song) {
        val currentFavorites = _libraryState.value.favorites
        _libraryState.value = _libraryState.value.copy(
            favorites = currentFavorites - song
        )
    }

    fun playPlaylist(playlist: Playlist) {
        if (playlist.songs.isNotEmpty()) {
            _playlistState.value = _playlistState.value.copy(
                currentPlaylist = playlist.songs,
                currentIndex = 0
            )
            mediaPlayerService?.playSong(playlist.songs.first())
        }
    }

    fun playArtistSongs(artist: Artist) {
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
        if (_libraryState.value.hasPermission) {
            loadMusicLibrary()
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (isServiceBound) {
            context.unbindService(serviceConnection)
            isServiceBound = false
        }
    }}