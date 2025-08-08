package com.codewithprashant.musicapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

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
    val playbackSpeed: Float = 1.0f
)

data class PlaylistState(
    val currentPlaylist: List<Song> = emptyList(),
    val currentIndex: Int = 0,
    val queue: List<Song> = emptyList(),
    val history: List<Song> = emptyList()
)

data class LibraryState(
    val songs: List<Song> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val albums: List<Album> = emptyList(),
    val favorites: List<Song> = emptyList(),
    val recentlyPlayed: List<Song> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
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



class MusicViewModel : ViewModel() {

    private val _playerState = MutableStateFlow(MusicPlayerState())
    val playerState: StateFlow<MusicPlayerState> = _playerState.asStateFlow()

    private val _playlistState = MutableStateFlow(PlaylistState())
    val playlistState: StateFlow<PlaylistState> = _playlistState.asStateFlow()

    private val _libraryState = MutableStateFlow(LibraryState())
    val libraryState: StateFlow<LibraryState> = _libraryState.asStateFlow()

    private val _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    init {
        loadSampleData()
        startProgressUpdater()
    }

    private fun loadSampleData() {
        val sampleSongs = listOf(
            Song(
                title = "Blinding Lights",
                artist = "The Weeknd",
                album = "After Hours",
                duration = "3:20",
                albumArt = ""
            ),
            Song(
                title = "Watermelon Sugar",
                artist = "Harry Styles",
                album = "Fine Line",
                duration = "2:54",
                albumArt = ""
            ),
            Song(
                title = "Levitating",
                artist = "Dua Lipa",
                album = "Future Nostalgia",
                duration = "3:23",
                albumArt = ""
            ),
            Song(
                title = "Good 4 U",
                artist = "Olivia Rodrigo",
                album = "SOUR",
                duration = "2:58",
                albumArt = ""
            ),
            Song(
                title = "Stay",
                artist = "The Kid LAROI & Justin Bieber",
                album = "F*CK LOVE 3",
                duration = "2:21",
                albumArt = ""
            ),
            Song(
                title = "Anti-Hero",
                artist = "Taylor Swift",
                album = "Midnights",
                duration = "3:20",
                albumArt = ""
            ),
            Song(
                title = "As It Was",
                artist = "Harry Styles",
                album = "Harry's House",
                duration = "2:47",
                albumArt = ""
            ),
            Song(
                title = "Heat Waves",
                artist = "Glass Animals",
                album = "Dreamland",
                duration = "3:58",
                albumArt = ""
            )
        )

        val samplePlaylists = listOf(
            Playlist(
                id = "1",
                name = "My Favorites",
                songs = sampleSongs.take(5),
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            Playlist(
                id = "2",
                name = "Workout Mix",
                songs = sampleSongs.drop(2).take(4),
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            Playlist(
                id = "3",
                name = "Chill Vibes",
                songs = sampleSongs.drop(1).take(3),
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        )

        val sampleArtists = listOf(
            Artist(id = "1", name = "The Weeknd", albumCount = 5, songCount = 52),
            Artist(id = "2", name = "Harry Styles", albumCount = 3, songCount = 34),
            Artist(id = "3", name = "Dua Lipa", albumCount = 2, songCount = 28),
            Artist(id = "4", name = "Taylor Swift", albumCount = 10, songCount = 156),
            Artist(id = "5", name = "Olivia Rodrigo", albumCount = 1, songCount = 11)
        )

        _libraryState.value = _libraryState.value.copy(
            songs = sampleSongs,
            playlists = samplePlaylists,
            artists = sampleArtists,
            recentlyPlayed = sampleSongs.take(3)
        )

        _playlistState.value = _playlistState.value.copy(
            currentPlaylist = sampleSongs
        )
    }

    fun playPause() {
        val currentState = _playerState.value
        if (currentState.currentSong == null) {
            playSong(_libraryState.value.songs.firstOrNull())
        } else {
            _playerState.value = currentState.copy(
                isPlaying = !currentState.isPlaying,
                isPaused = currentState.isPlaying
            )
        }
    }

    fun playSong(song: Song?) {
        song?.let {
            _playerState.value = _playerState.value.copy(
                currentSong = it,
                isPlaying = true,
                isPaused = false,
                currentPosition = 0L,
                duration = parseDurationToMillis(it.duration),
                progress = 0f
            )

            val currentPlaylist = _playlistState.value.currentPlaylist
            val index = currentPlaylist.indexOf(song)
            if (index != -1) {
                _playlistState.value = _playlistState.value.copy(
                    currentIndex = index
                )
            }
        }
    }

    fun seekTo(position: Float) {
        val currentState = _playerState.value
        val newPosition = (currentState.duration * position).toLong()
        _playerState.value = currentState.copy(
            currentPosition = newPosition,
            progress = position
        )
    }

    fun setVolume(volume: Float) {
        _playerState.value = _playerState.value.copy(volume = volume)
    }

    fun toggleShuffle() {
        _playerState.value = _playerState.value.copy(
            isShuffleEnabled = !_playerState.value.isShuffleEnabled
        )
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

    fun skipToNext() {
        val playlistState = _playlistState.value
        val playerState = _playerState.value

        if (playlistState.currentPlaylist.isNotEmpty()) {
            val nextIndex = if (playerState.isShuffleEnabled) {
                playlistState.currentPlaylist.indices.random()
            } else {
                (playlistState.currentIndex + 1) % playlistState.currentPlaylist.size
            }

            val nextSong = playlistState.currentPlaylist[nextIndex]
            playSong(nextSong)

            _playlistState.value = playlistState.copy(currentIndex = nextIndex)
        }
    }

    fun skipToPrevious() {
        val playlistState = _playlistState.value

        if (playlistState.currentPlaylist.isNotEmpty()) {
            val prevIndex = if (playlistState.currentIndex > 0) {
                playlistState.currentIndex - 1
            } else {
                playlistState.currentPlaylist.size - 1
            }

            val prevSong = playlistState.currentPlaylist[prevIndex]
            playSong(prevSong)

            _playlistState.value = playlistState.copy(currentIndex = prevIndex)
        }
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

    private fun startProgressUpdater() {
        viewModelScope.launch {
            while (true) {
                val currentState = _playerState.value
                if (currentState.isPlaying && currentState.duration > 0) {
                    val newPosition = currentState.currentPosition + 1000
                    val newProgress = newPosition.toFloat() / currentState.duration

                    if (newPosition >= currentState.duration) {
                        handleSongEnd()
                    } else {
                        _playerState.value = currentState.copy(
                            currentPosition = newPosition,
                            progress = newProgress
                        )
                    }
                }
                delay(1000)
            }
        }
    }

    private fun handleSongEnd() {
        val repeatMode = _playerState.value.repeatMode
        when (repeatMode) {
            RepeatMode.ONE -> {
                _playerState.value = _playerState.value.copy(
                    currentPosition = 0L,
                    progress = 0f
                )
            }
            RepeatMode.ALL -> skipToNext()
            RepeatMode.OFF -> {
                val playlistState = _playlistState.value
                if (playlistState.currentIndex < playlistState.currentPlaylist.size - 1) {
                    skipToNext()
                } else {
                    _playerState.value = _playerState.value.copy(
                        isPlaying = false,
                        currentPosition = 0L,
                        progress = 0f
                    )
                }
            }
        }
    }

    private fun parseDurationToMillis(duration: String): Long {
        return try {
            val parts = duration.split(":")
            val minutes = parts[0].toLong()
            val seconds = parts[1].toLong()
            (minutes * 60 + seconds) * 1000
        } catch (e: Exception) {
            180000L
        }
    }
}