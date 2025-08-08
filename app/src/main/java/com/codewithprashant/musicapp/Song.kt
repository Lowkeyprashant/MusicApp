package com.codewithprashant.musicapp

data class Song(
    val id: String = "",
    val title: String,
    val artist: String,
    val album: String,
    val duration: String,
    val albumArt: String = "",
    val filePath: String = "",
    val fileSize: Long = 0L,
    val bitrate: Int = 0,
    val sampleRate: Int = 0,
    val genre: String = "",
    val year: Int = 0,
    val trackNumber: Int = 0,
    val discNumber: Int = 1,
    val isPlaying: Boolean = false,
    val isFavorite: Boolean = false,
    val isDownloaded: Boolean = false,
    val playCount: Int = 0,
    val lastPlayed: Long = 0L,
    val dateAdded: Long = System.currentTimeMillis(),
    val lyrics: String = "",
    val artistId: String = "",
    val albumId: String = ""
)

data class PlaylistInfo(
    val id: String,
    val name: String,
    val description: String = "",
    val songIds: List<String> = emptyList(),
    val coverImageUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isUserCreated: Boolean = true,
    val isPublic: Boolean = false,
    val createdBy: String = "",
    val totalDuration: Long = 0L,
    val songCount: Int = songIds.size
)

data class ArtistInfo(
    val id: String,
    val name: String,
    val biography: String = "",
    val imageUrl: String = "",
    val albumIds: List<String> = emptyList(),
    val songIds: List<String> = emptyList(),
    val genres: List<String> = emptyList(),
    val isFollowed: Boolean = false,
    val monthlyListeners: Int = 0,
    val totalStreams: Long = 0L,
    val socialLinks: Map<String, String> = emptyMap()
) {
    val albumCount: Int get() = albumIds.size
    val songCount: Int get() = songIds.size
}

data class AlbumInfo(
    val id: String,
    val title: String,
    val artistId: String,
    val artistName: String,
    val year: Int = 0,
    val genre: String = "",
    val coverImageUrl: String = "",
    val songIds: List<String> = emptyList(),
    val totalDuration: Long = 0L,
    val releaseDate: Long = 0L,
    val recordLabel: String = "",
    val isExplicit: Boolean = false,
    val description: String = ""
) {
    val songCount: Int get() = songIds.size
}

data class QueueItem(
    val song: Song,
    val queueId: String = System.currentTimeMillis().toString(),
    val addedAt: Long = System.currentTimeMillis(),
    val addedBy: String = "user"
)

enum class RepeatMode {
    OFF, ONE, ALL
}

enum class SortOrder {
    TITLE_ASC, TITLE_DESC, ARTIST_ASC, ARTIST_DESC, DURATION_ASC, DURATION_DESC, DATE_ADDED_ASC, DATE_ADDED_DESC
}

data class Playlist(
    val id: String,
    val name: String,
    val songs: List<Song>,
    val createdAt: Long,
    val updatedAt: Long,
    val isUserCreated: Boolean = true
)

data class Artist(
    val id: String,
    val name: String,
    val albumCount: Int,
    val songCount: Int,
    val imageUrl: String = ""
)

data class Album(
    val id: String,
    val title: String,
    val artist: String,
    val year: Int,
    val songs: List<Song>,
    val albumArt: String = ""
)