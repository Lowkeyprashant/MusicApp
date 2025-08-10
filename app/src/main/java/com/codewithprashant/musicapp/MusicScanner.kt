// MusicScanner.kt - Fixed Imports Version
package com.codewithprashant.musicapp

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File

class MusicScanner(private val context: Context) {

    companion object {
        private const val TAG = "MusicScanner"

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

        // More permissive selection to find more files
        private const val MUSIC_SELECTION = "${MediaStore.Audio.Media.IS_MUSIC} = 1"
        private const val MUSIC_SORT_ORDER = "${MediaStore.Audio.Media.TITLE} ASC"
    }

    suspend fun scanForMusic(): List<Song> = withContext(Dispatchers.IO) {
        Log.d(TAG, "Starting music scan...")
        val songs = mutableListOf<Song>()
        val contentResolver = context.contentResolver

        try {
            Log.d(TAG, "Querying MediaStore for audio files...")
            val cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                MUSIC_PROJECTION,
                MUSIC_SELECTION,
                null,
                MUSIC_SORT_ORDER
            )

            if (cursor == null) {
                Log.e(TAG, "Cursor is null - no permission or no media")
                return@withContext songs
            }

            Log.d(TAG, "Found ${cursor.count} potential audio files")

            cursor.use { c ->
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

                var processedCount = 0
                var validSongs = 0

                while (c.moveToNext()) {
                    try {
                        processedCount++

                        val id = c.getLong(idColumn)
                        val title = c.getString(titleColumn) ?: "Unknown Title"
                        val artist = c.getString(artistColumn) ?: "Unknown Artist"
                        val album = c.getString(albumColumn) ?: "Unknown Album"
                        val duration = c.getLong(durationColumn)
                        val filePath = c.getString(dataColumn)
                        val albumId = c.getLong(albumIdColumn)
                        val artistId = c.getLong(artistIdColumn)
                        val year = c.getInt(yearColumn)
                        val track = c.getInt(trackColumn)
                        val size = c.getLong(sizeColumn)
                        val dateAdded = c.getLong(dateAddedColumn) * 1000

                        Log.d(TAG, "Processing song #$processedCount: $title by $artist")
                        Log.d(TAG, "File path: $filePath")
                        Log.d(TAG, "Duration: ${duration}ms, Size: ${size} bytes")

                        if (filePath.isNullOrEmpty()) {
                            Log.w(TAG, "Skipping song with empty file path: $title")
                            continue
                        }

                        // Check if file exists (skip this check for now to see all entries)
                        val file = File(filePath)
                        if (!file.exists()) {
                            Log.w(TAG, "File does not exist: $filePath")
                            // Continue anyway to see if MediaPlayer can handle it
                        }

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
                        validSongs++

                        Log.d(TAG, "Added song: ${song.title} - ${song.duration}")

                    } catch (e: Exception) {
                        Log.e(TAG, "Error processing song #$processedCount", e)
                        continue
                    }
                }

                Log.i(TAG, "Scan complete: $validSongs valid songs out of $processedCount processed")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during music scan", e)
        }

        Log.i(TAG, "Returning ${songs.size} songs")
        return@withContext songs
    }

    suspend fun scanForAlbums(): List<Album> = withContext(Dispatchers.IO) {
        Log.d(TAG, "Starting album scan...")
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
                Log.d(TAG, "Found ${c.count} albums")

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
                            songs = emptyList(),
                            albumArt = albumArt
                        )

                        albums.add(album)
                        Log.d(TAG, "Added album: $albumName by $artist ($songCount songs)")

                    } catch (e: Exception) {
                        Log.e(TAG, "Error processing album", e)
                        continue
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during album scan", e)
        }

        Log.i(TAG, "Album scan complete: ${albums.size} albums found")
        return@withContext albums
    }

    suspend fun scanForArtists(): List<Artist> = withContext(Dispatchers.IO) {
        Log.d(TAG, "Starting artist scan...")
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
                Log.d(TAG, "Found ${c.count} artists")

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

                        // Skip unknown artists with no content
                        if (artistName == "Unknown Artist" || artistName == "<unknown>" || trackCount == 0) {
                            continue
                        }

                        val artist = Artist(
                            id = id.toString(),
                            name = artistName,
                            albumCount = albumCount,
                            songCount = trackCount
                        )

                        artists.add(artist)
                        Log.d(TAG, "Added artist: $artistName ($albumCount albums, $trackCount tracks)")

                    } catch (e: Exception) {
                        Log.e(TAG, "Error processing artist", e)
                        continue
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during artist scan", e)
        }

        Log.i(TAG, "Artist scan complete: ${artists.size} artists found")
        return@withContext artists
    }

    private fun getAlbumArtPath(albumId: Long): String {
        return try {
            val uri = ContentUris.withAppendedId(
                Uri.parse("content://media/external/audio/albumart"),
                albumId
            )
            Log.d(TAG, "Album art URI for album $albumId: $uri")
            uri.toString()
        } catch (e: Exception) {
            Log.w(TAG, "Could not get album art for album $albumId", e)
            ""
        }
    }

    fun getAlbumArtBitmap(song: Song): Bitmap? {
        return try {
            Log.d(TAG, "Loading album art for: ${song.title}")
            when {
                song.albumArt.isNotEmpty() -> {
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
                    extractAlbumArtFromFile(song.filePath)
                }
                else -> null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading album art for ${song.title}", e)
            null
        }
    }

    private fun extractAlbumArtFromFile(filePath: String): Bitmap? {
        return try {
            Log.d(TAG, "Extracting album art from file: $filePath")
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(filePath)
            val albumArtBytes = retriever.embeddedPicture
            retriever.release()

            albumArtBytes?.let { bytes ->
                Log.d(TAG, "Found embedded album art (${bytes.size} bytes)")
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } ?: run {
                Log.d(TAG, "No embedded album art found")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting album art from $filePath", e)
            null
        }
    }

    private fun formatDuration(durationMs: Long): String {
        val totalSeconds = durationMs / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    // Debug function to check permissions
    fun checkPermissions(): Boolean {
        val hasReadAudio = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.READ_MEDIA_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking permissions", e)
            false
        }

        Log.i(TAG, "Audio permission granted: $hasReadAudio")
        return hasReadAudio
    }
}