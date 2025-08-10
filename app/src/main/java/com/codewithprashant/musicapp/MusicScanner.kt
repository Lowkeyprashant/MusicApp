// MusicScanner.kt - Device Music Scanner
package com.codewithprashant.musicapp

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.util.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File

class MusicScanner(private val context: Context) {

    companion object {
        private val MUSIC_PROJECTION = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.MIME_TYPE
        )

        private const val MUSIC_SELECTION = "${MediaStore.Audio.Media.IS_MUSIC} = 1 AND ${MediaStore.Audio.Media.DURATION} > 30000"
        private const val MUSIC_SORT_ORDER = "${MediaStore.Audio.Media.TITLE} ASC"
    }

    suspend fun scanForMusic(): List<Song> = withContext(Dispatchers.IO) {
        val songs = mutableListOf<Song>()
        val contentResolver = context.contentResolver

        try {
            val cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                MUSIC_PROJECTION,
                MUSIC_SELECTION,
                null,
                MUSIC_SORT_ORDER
            )

            cursor?.use { c ->
                val idColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val dataColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val albumIdColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val artistIdColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)
                val yearColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
                val trackColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
                val sizeColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                val dateAddedColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

                while (c.moveToNext()) {
                    try {
                        val id = c.getLong(idColumn)
                        val title = c.getString(titleColumn) ?: "Unknown Title"
                        val artist = c.getString(artistColumn) ?: "Unknown Artist"
                        val album = c.getString(albumColumn) ?: "Unknown Album"
                        val duration = c.getLong(durationColumn)
                        val filePath = c.getString(dataColumn) ?: continue
                        val albumId = c.getLong(albumIdColumn)
                        val artistId = c.getLong(artistIdColumn)
                        val year = c.getInt(yearColumn)
                        val track = c.getInt(trackColumn)
                        val size = c.getLong(sizeColumn)
                        val dateAdded = c.getLong(dateAddedColumn) * 1000 // Convert to milliseconds

                        // Check if file actually exists
                        if (!File(filePath).exists()) continue

                        // Get album art path
                        val albumArtPath = getAlbumArtPath(albumId)

                        val song = Song(
                            id = id.toString(),
                            title = title,
                            artist = artist,
                            album = album,
                            duration = formatDuration(duration),
                            albumArt = albumArtPath,
                            filePath = filePath,
                            fileSize = size,
                            year = year,
                            trackNumber = track,
                            dateAdded = dateAdded,
                            artistId = artistId.toString(),
                            albumId = albumId.toString()
                        )

                        songs.add(song)

                    } catch (e: Exception) {
                        e.printStackTrace()
                        // Continue with next song if this one fails
                        continue
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        songs
    }

    suspend fun scanForAlbums(): List<Album> = withContext(Dispatchers.IO) {
        val albums = mutableListOf<Album>()
        val contentResolver = context.contentResolver

        val projection = arrayOf(
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ARTIST,
            MediaStore.Audio.Albums.FIRST_YEAR,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS,
            MediaStore.Audio.Albums.ALBUM_ART
        )

        try {
            val cursor = contentResolver.query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                "${MediaStore.Audio.Albums.ALBUM} ASC"
            )

            cursor?.use { c ->
                val idColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)
                val albumColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)
                val artistColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)
                val yearColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Albums.FIRST_YEAR)
                val songCountColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)
                val albumArtColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART)

                while (c.moveToNext()) {
                    try {
                        val id = c.getLong(idColumn)
                        val albumName = c.getString(albumColumn) ?: "Unknown Album"
                        val artist = c.getString(artistColumn) ?: "Unknown Artist"
                        val year = c.getInt(yearColumn)
                        val songCount = c.getInt(songCountColumn)
                        val albumArt = c.getString(albumArtColumn) ?: ""

                        val album = Album(
                            id = id.toString(),
                            title = albumName,
                            artist = artist,
                            year = year,
                            songs = emptyList(), // Will be populated later if needed
                            albumArt = albumArt
                        )

                        albums.add(album)

                    } catch (e: Exception) {
                        e.printStackTrace()
                        continue
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        albums
    }

    suspend fun scanForArtists(): List<Artist> = withContext(Dispatchers.IO) {
        val artists = mutableListOf<Artist>()
        val contentResolver = context.contentResolver

        val projection = arrayOf(
            MediaStore.Audio.Artists._ID,
            MediaStore.Audio.Artists.ARTIST,
            MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
            MediaStore.Audio.Artists.NUMBER_OF_TRACKS
        )

        try {
            val cursor = contentResolver.query(
                MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                "${MediaStore.Audio.Artists.ARTIST} ASC"
            )

            cursor?.use { c ->
                val idColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID)
                val artistColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST)
                val albumCountColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)
                val trackCountColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)

                while (c.moveToNext()) {
                    try {
                        val id = c.getLong(idColumn)
                        val artistName = c.getString(artistColumn) ?: "Unknown Artist"
                        val albumCount = c.getInt(albumCountColumn)
                        val trackCount = c.getInt(trackCountColumn)

                        // Skip "Unknown Artist" entries with no real content
                        if (artistName == "Unknown Artist" || artistName == "<unknown>") {
                            continue
                        }

                        val artist = Artist(
                            id = id.toString(),
                            name = artistName,
                            albumCount = albumCount,
                            songCount = trackCount
                        )

                        artists.add(artist)

                    } catch (e: Exception) {
                        e.printStackTrace()
                        continue
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        artists
    }

    private fun getAlbumArtPath(albumId: Long): String {
        return try {
            val uri = ContentUris.withAppendedId(
                Uri.parse("content://media/external/audio/albumart"),
                albumId
            )
            uri.toString()
        } catch (e: Exception) {
            ""
        }
    }

    fun getAlbumArtBitmap(song: Song): Bitmap? {
        return try {
            when {
                song.albumArt.isNotEmpty() -> {
                    // Try to load from album art path
                    if (song.albumArt.startsWith("content://")) {
                        val uri = Uri.parse(song.albumArt)
                        context.contentResolver.openInputStream(uri)?.use { inputStream ->
                            BitmapFactory.decodeStream(inputStream)
                        }
                    } else {
                        BitmapFactory.decodeFile(song.albumArt)
                    }
                }
                song.filePath.isNotEmpty() -> {
                    // Try to extract from audio file metadata
                    extractAlbumArtFromFile(song.filePath)
                }
                else -> null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun extractAlbumArtFromFile(filePath: String): Bitmap? {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(filePath)
            val albumArtBytes = retriever.embeddedPicture
            retriever.release()

            albumArtBytes?.let { bytes ->
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveAlbumArtToCache(song: Song, bitmap: Bitmap): String? {
        return try {
            val cacheDir = File(context.cacheDir, "album_art")
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }

            val fileName = "album_${song.albumId}_${song.id}.jpg"
            val file = File(cacheDir, fileName)

            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)

            file.writeBytes(outputStream.toByteArray())
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun formatDuration(durationMs: Long): String {
        val totalSeconds = durationMs / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    fun getSongsForAlbum(albumId: String): List<Song> {
        val songs = mutableListOf<Song>()
        val contentResolver = context.contentResolver

        try {
            val selection = "${MediaStore.Audio.Media.ALBUM_ID} = ? AND ${MediaStore.Audio.Media.IS_MUSIC} = 1"
            val selectionArgs = arrayOf(albumId)

            val cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                MUSIC_PROJECTION,
                selection,
                selectionArgs,
                "${MediaStore.Audio.Media.TRACK} ASC"
            )

            cursor?.use { c ->
                // Process cursor similar to scanForMusic()
                // Implementation similar to above
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return songs
    }

    fun getSongsForArtist(artistId: String): List<Song> {
        val songs = mutableListOf<Song>()
        val contentResolver = context.contentResolver

        try {
            val selection = "${MediaStore.Audio.Media.ARTIST_ID} = ? AND ${MediaStore.Audio.Media.IS_MUSIC} = 1"
            val selectionArgs = arrayOf(artistId)

            val cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                MUSIC_PROJECTION,
                selection,
                selectionArgs,
                "${MediaStore.Audio.Media.ALBUM} ASC, ${MediaStore.Audio.Media.TRACK} ASC"
            )

            cursor?.use { c ->
                // Process cursor similar to scanForMusic()
                // Implementation similar to above
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return songs
    }
}