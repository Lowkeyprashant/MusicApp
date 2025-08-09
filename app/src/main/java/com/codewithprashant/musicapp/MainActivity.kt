// MainActivity.kt - Fixed Version with No Errors
package com.codewithprashant.musicapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codewithprashant.musicapp.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MusicAppMain()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicAppMain() {
    val viewModel: MusicViewModel = viewModel()
    val playerState by viewModel.playerState.collectAsState()
    val libraryState by viewModel.libraryState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var showPlayerScreen by remember { mutableStateOf(false) }
    var showSearchScreen by remember { mutableStateOf(false) }
    var selectedSong by remember { mutableStateOf<Song?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = when {
                showPlayerScreen -> "player"
                showSearchScreen -> "search"
                else -> "main"
            },
            transitionSpec = {
                slideInVertically(
                    animationSpec = tween(600, easing = EaseOutCubic),
                    initialOffsetY = { if (targetState == "player") it else -it }
                ) togetherWith slideOutVertically(
                    animationSpec = tween(600, easing = EaseInCubic),
                    targetOffsetY = { if (initialState == "player") it else -it }
                )
            },
            label = "screen_transition"
        ) { screen ->
            when (screen) {
                "main" -> MainScreen(
                    libraryState = libraryState,
                    playerState = playerState,
                    uiState = uiState,
                    onSongClick = { song ->
                        selectedSong = song
                        viewModel.playSong(song)
                        showPlayerScreen = true
                    },
                    onSearchClick = { showSearchScreen = true },
                    onTabSelected = viewModel::updateSelectedTab,
                    onMiniPlayerClick = { showPlayerScreen = true }
                )
                "player" -> PlayerScreen(
                    song = selectedSong ?: playerState.currentSong,
                    playerState = playerState,
                    onBackClick = { showPlayerScreen = false },
                    onPlayPause = viewModel::playPause,
                    onNext = viewModel::skipToNext,
                    onPrevious = viewModel::skipToPrevious,
                    onSeek = viewModel::seekTo,
                    onVolumeChange = viewModel::setVolume,
                    onRepeatToggle = viewModel::toggleRepeat,
                    onShuffleToggle = viewModel::toggleShuffle
                )
                "search" -> SearchScreen(
                    onBackClick = { showSearchScreen = false },
                    onSongClick = { song ->
                        selectedSong = song
                        viewModel.playSong(song)
                        showSearchScreen = false
                        showPlayerScreen = true
                    }
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    libraryState: LibraryState,
    playerState: MusicPlayerState,
    uiState: UIState,
    onSongClick: (Song) -> Unit,
    onSearchClick: () -> Unit,
    onTabSelected: (Int) -> Unit,
    onMiniPlayerClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DeepNavy, MidnightBlue, DarkCharcoal)
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopBar(onSearchClick = onSearchClick)

            CustomTabRow(
                selectedTab = uiState.selectedTab,
                onTabSelected = onTabSelected
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = if (playerState.currentSong != null) 120.dp else 0.dp)
            ) {
                when (uiState.selectedTab) {
                    0 -> HomeContent(
                        songs = libraryState.songs,
                        recentlyPlayed = libraryState.recentlyPlayed,
                        onSongClick = onSongClick
                    )
                    1 -> LibraryContent(
                        songs = libraryState.songs,
                        onSongClick = onSongClick
                    )
                    2 -> PlaylistsContent(
                        playlists = libraryState.playlists,
                        onPlaylistClick = { }
                    )
                    3 -> ArtistsContent(
                        artists = libraryState.artists,
                        onArtistClick = { }
                    )
                }
            }
        }

        if (playerState.currentSong != null) {
            EnhancedMiniPlayer(
                modifier = Modifier.align(Alignment.BottomCenter),
                song = playerState.currentSong!!,
                isPlaying = playerState.isPlaying,
                progress = playerState.progress,
                onClick = onMiniPlayerClick
            )
        }
    }
}

@Composable
fun TopBar(onSearchClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Good Evening",
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Music Lover",
                color = TextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier
                    .size(48.dp)
                    .glassCard(alpha = 0.12f, cornerRadius = 24.dp)
                    .premiumGlow(glowColor = SoftBlue)
            ) {
                Icon(
                    Icons.Rounded.Search,
                    contentDescription = "Search",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = { },
                modifier = Modifier
                    .size(48.dp)
                    .glassCard(alpha = 0.12f, cornerRadius = 24.dp)
                    .premiumGlow(glowColor = SoftPink)
            ) {
                Icon(
                    Icons.Rounded.Notifications,
                    contentDescription = "Notifications",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun CustomTabRow(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("Discover", "Library", "Playlists", "Artists")

    LazyRow(
        modifier = Modifier.padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tabs.size) { index ->
            val isSelected = selectedTab == index

            Card(
                modifier = Modifier
                    .clickable { onTabSelected(index) }
                    .animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)),
                shape = RoundedCornerShape(25.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            if (isSelected) {
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        SoftPurple.copy(alpha = 0.3f),
                                        SoftBlue.copy(alpha = 0.2f)
                                    )
                                )
                            } else {
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        GlassLight,
                                        GlassDark
                                    )
                                )
                            }
                        )
                        .then(
                            if (isSelected) {
                                Modifier.border(
                                    1.dp,
                                    SoftPurple.copy(alpha = 0.4f),
                                    RoundedCornerShape(25.dp)
                                )
                            } else Modifier
                        )
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tabs[index],
                        color = if (isSelected) TextPrimary else TextSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun HomeContent(
    songs: List<Song>,
    recentlyPlayed: List<Song>,
    onSongClick: (Song) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        item {
            QuickAccessSection()
        }

        item {
            SectionHeader("Recently Played")
            Spacer(modifier = Modifier.height(16.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(recentlyPlayed) { song ->
                    RecentlyPlayedCard(
                        song = song,
                        onClick = { onSongClick(song) }
                    )
                }
            }
        }

        item {
            SectionHeader("Trending Now")
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(songs.take(5)) { song ->
            EnhancedSongItem(
                song = song,
                onClick = { onSongClick(song) }
            )
        }
    }
}

@Composable
fun QuickAccessSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            SoftPurple.copy(alpha = 0.4f),
                            SoftPink.copy(alpha = 0.3f),
                            SoftTeal.copy(alpha = 0.2f)
                        )
                    )
                )
                .border(
                    1.dp,
                    Color.White.copy(alpha = 0.1f),
                    RoundedCornerShape(24.dp)
                )
                .padding(24.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Discover Weekly",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Fresh music curated for you",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                FloatingActionButton(
                    onClick = { },
                    modifier = Modifier.size(56.dp),
                    containerColor = Color.White.copy(alpha = 0.15f),
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 12.dp
                    )
                ) {
                    Icon(
                        Icons.Rounded.PlayArrow,
                        contentDescription = "Play",
                        tint = TextPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RecentlyPlayedCard(
    song: Song,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .glassCard(alpha = 0.1f, cornerRadius = 20.dp)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    SoftBlue.copy(alpha = 0.6f),
                                    SoftTeal.copy(alpha = 0.4f)
                                )
                            ),
                            RoundedCornerShape(16.dp)
                        )
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.2f),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.MusicNote,
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = song.title,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = song.artist,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedSongItem(
    song: Song,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .scale(scale)
            .clickable {
                isPressed = true
                onClick()
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .glassCard(alpha = 0.08f, cornerRadius = 20.dp)
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    SoftPurple.copy(alpha = 0.6f),
                                    SoftPink.copy(alpha = 0.4f)
                                )
                            ),
                            RoundedCornerShape(16.dp)
                        )
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.15f),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.MusicNote,
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = song.title,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${song.artist} • ${song.album}",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = song.duration,
                    color = TextTertiary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.width(12.dp))

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            GlassLight,
                            CircleShape
                        )
                ) {
                    Icon(
                        Icons.Rounded.MoreVert,
                        contentDescription = "More",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(150)
            isPressed = false
        }
    }
}

@Composable
fun EnhancedMiniPlayer(
    modifier: Modifier = Modifier,
    song: Song,
    isPlaying: Boolean,
    progress: Float,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .glassCard(alpha = 0.15f, cornerRadius = 24.dp)
        ) {
            Column {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = SoftPurple,
                    trackColor = GlassDark
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        SoftBlue.copy(alpha = 0.6f),
                                        SoftTeal.copy(alpha = 0.4f)
                                    )
                                ),
                                RoundedCornerShape(16.dp)
                            )
                            .border(
                                1.dp,
                                Color.White.copy(alpha = 0.2f),
                                RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.MusicNote,
                            contentDescription = null,
                            tint = TextPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = song.title,
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = song.artist,
                            color = TextSecondary,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                GlassMedium,
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Rounded.SkipPrevious,
                            contentDescription = "Previous",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    FloatingActionButton(
                        onClick = { },
                        modifier = Modifier.size(44.dp),
                        containerColor = Color.Transparent,
                        elevation = FloatingActionButtonDefaults.elevation(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            SoftPurple.copy(alpha = 0.8f),
                                            SoftBlue.copy(alpha = 0.6f)
                                        )
                                    ),
                                    CircleShape
                                )
                                .border(
                                    1.dp,
                                    Color.White.copy(alpha = 0.3f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = TextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                GlassMedium,
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Rounded.SkipNext,
                            contentDescription = "Next",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        color = TextPrimary,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun LibraryContent(
    songs: List<Song>,
    onSongClick: (Song) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(songs) { song ->
            EnhancedSongItem(
                song = song,
                onClick = { onSongClick(song) }
            )
        }
    }
}

@Composable
fun PlaylistsContent(
    playlists: List<Playlist>,
    onPlaylistClick: (Playlist) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(playlists) { playlist ->
            PlaylistCard(
                playlist = playlist,
                onClick = { onPlaylistClick(playlist) }
            )
        }
    }
}

@Composable
fun PlaylistCard(
    playlist: Playlist,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .glassCard(alpha = 0.1f, cornerRadius = 20.dp)
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    SoftPink.copy(alpha = 0.6f),
                                    SoftPurple.copy(alpha = 0.4f)
                                )
                            ),
                            RoundedCornerShape(16.dp)
                        )
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.2f),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.QueueMusic,
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = playlist.name,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${playlist.songs.size} songs",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            GlassMedium,
                            CircleShape
                        )
                ) {
                    Icon(
                        Icons.Rounded.PlayArrow,
                        contentDescription = "Play",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ArtistsContent(
    artists: List<Artist>,
    onArtistClick: (Artist) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(artists) { artist ->
            ArtistCard(
                artist = artist,
                onClick = { onArtistClick(artist) }
            )
        }
    }
}

@Composable
fun ArtistCard(
    artist: Artist,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .glassCard(alpha = 0.1f, cornerRadius = 20.dp)
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    SoftGreen.copy(alpha = 0.6f),
                                    SoftTeal.copy(alpha = 0.4f)
                                )
                            ),
                            CircleShape
                        )
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.2f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.Person,
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = artist.name,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${artist.albumCount} albums • ${artist.songCount} songs",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            GlassMedium,
                            CircleShape
                        )
                ) {
                    Icon(
                        Icons.Rounded.Favorite,
                        contentDescription = "Follow",
                        tint = SoftPink,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}