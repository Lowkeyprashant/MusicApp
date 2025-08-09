// SearchScreen.kt - Fixed Version with All Errors Resolved
package com.codewithprashant.musicapp

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithprashant.musicapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onSongClick: (Song) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val focusRequester = remember { FocusRequester() }

    val filters = listOf("All", "Songs", "Artists", "Albums", "Playlists")
    val trendingSearches = listOf(
        "The Weeknd", "Dua Lipa", "Pop Hits", "Rock Classics",
        "Chill Vibes", "Workout Mix", "Taylor Swift", "Ed Sheeran"
    )

    val searchResults = generateSearchResults(searchQuery)

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

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
            SearchTopBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onBackClick = onBackClick,
                focusRequester = focusRequester
            )

            SearchFilterRow(
                filters = filters,
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )

            if (searchQuery.isEmpty()) {
                SearchEmptyState(
                    trendingSearches = trendingSearches,
                    onTrendingClick = { searchQuery = it }
                )
            } else {
                SearchResultsContent(
                    query = searchQuery,
                    filter = selectedFilter,
                    results = searchResults,
                    onSongClick = onSongClick
                )
            }
        }
    }
}

@Composable
fun SearchTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    focusRequester: FocusRequester
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(48.dp)
                .glassCard(alpha = 0.12f, cornerRadius = 24.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back",
                tint = TextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }

        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(25.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                GlassMedium,
                                GlassLight
                            )
                        ),
                        RoundedCornerShape(25.dp)
                    ),
                placeholder = {
                    Text(
                        "Search songs, artists, albums...",
                        color = TextTertiary,
                        fontSize = 16.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Rounded.Search,
                        contentDescription = "Search",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchQueryChange("") }
                        ) {
                            Icon(
                                Icons.Rounded.Clear,
                                contentDescription = "Clear",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = SoftPurple.copy(alpha = 0.4f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    cursorColor = SoftPurple,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(25.dp),
                singleLine = true
            )
        }

        IconButton(
            onClick = { },
            modifier = Modifier
                .size(48.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            SoftBlue.copy(alpha = 0.4f),
                            SoftBlue.copy(alpha = 0.2f)
                        )
                    ),
                    CircleShape
                )
                .border(
                    1.dp,
                    SoftBlue.copy(alpha = 0.3f),
                    CircleShape
                )
        ) {
            Icon(
                Icons.Rounded.FilterList,
                contentDescription = "Filter",
                tint = SoftBlue,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun SearchFilterRow(
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(filters) { filter ->
            val isSelected = selectedFilter == filter

            Card(
                modifier = Modifier.clickable { onFilterSelected(filter) },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = if (isSelected) CardDefaults.cardElevation(defaultElevation = 8.dp) else CardDefaults.cardElevation(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            if (isSelected) {
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        SoftPurple.copy(alpha = 0.4f),
                                        SoftBlue.copy(alpha = 0.3f)
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
                                    SoftPurple.copy(alpha = 0.3f),
                                    RoundedCornerShape(20.dp)
                                )
                            } else Modifier
                        )
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = filter,
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
fun SearchEmptyState(
    trendingSearches: List<String>,
    onTrendingClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        item {
            RecentSearchesSection()
        }

        item {
            TrendingSearchesSection(
                trendingSearches = trendingSearches,
                onTrendingClick = onTrendingClick
            )
        }

        item {
            BrowseCategoriesSection()
        }
    }
}

@Composable
fun RecentSearchesSection() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Searches",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            TextButton(onClick = { }) {
                Text(
                    text = "Clear All",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val recentSearches = listOf("The Weeknd", "Chill Music", "Rock Hits")

        recentSearches.forEach { search ->
            RecentSearchItem(
                searchText = search,
                onRemoveClick = { }
            )
        }
    }
}

@Composable
fun RecentSearchItem(
    searchText: String,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            GlassLight,
                            GlassDark
                        )
                    )
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Rounded.History,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = searchText,
                color = TextSecondary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onRemoveClick) {
                Icon(
                    Icons.Rounded.Close,
                    contentDescription = "Remove",
                    tint = TextTertiary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun TrendingSearchesSection(
    trendingSearches: List<String>,
    onTrendingClick: (String) -> Unit
) {
    Column {
        Text(
            text = "Trending Searches",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(trendingSearches) { search ->
                TrendingChip(
                    text = search,
                    onClick = { onTrendingClick(search) }
                )
            }
        }
    }
}

@Composable
fun TrendingChip(
    text: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            SoftPink.copy(alpha = 0.3f),
                            SoftPurple.copy(alpha = 0.25f)
                        )
                    )
                )
                .border(
                    1.dp,
                    SoftPink.copy(alpha = 0.2f),
                    RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 16.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun BrowseCategoriesSection() {
    Column {
        Text(
            text = "Browse Categories",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        val categories = listOf(
            CategoryItem("Pop", SoftPink, Icons.Rounded.Star),
            CategoryItem("Rock", SoftBlue, Icons.Rounded.MusicNote),
            CategoryItem("Hip Hop", SoftTeal, Icons.Rounded.GraphicEq),
            CategoryItem("Electronic", SoftPurple, Icons.Rounded.ElectricalServices),
            CategoryItem("Jazz", SoftGreen, Icons.Rounded.Piano),
            CategoryItem("Classical", SoftBlue, Icons.Rounded.LibraryMusic)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            for (i in categories.indices step 2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CategoryCard(
                        category = categories[i],
                        modifier = Modifier.weight(1f)
                    )
                    if (i + 1 < categories.size) {
                        CategoryCard(
                            category = categories[i + 1],
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

data class CategoryItem(
    val name: String,
    val color: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun CategoryCard(
    category: CategoryItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(90.dp)
            .clickable { },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            category.color.copy(alpha = 0.4f),
                            category.color.copy(alpha = 0.2f)
                        )
                    )
                )
                .border(
                    1.dp,
                    category.color.copy(alpha = 0.3f),
                    RoundedCornerShape(16.dp)
                )
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    category.icon,
                    contentDescription = null,
                    tint = category.color,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = category.name,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun SearchResultsContent(
    query: String,
    filter: String,
    results: SearchResults,
    onSongClick: (Song) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        if (filter == "All" || filter == "Songs") {
            if (results.songs.isNotEmpty()) {
                item {
                    SectionHeader(title = "Songs")
                }
                items(results.songs) { song ->
                    SearchSongItem(
                        song = song,
                        onClick = { onSongClick(song) }
                    )
                }
            }
        }

        if (filter == "All" || filter == "Artists") {
            if (results.artists.isNotEmpty()) {
                item {
                    SectionHeader(title = "Artists")
                }
                items(results.artists) { artist ->
                    SearchArtistItem(
                        artist = artist,
                        onClick = { }
                    )
                }
            }
        }

        if (filter == "All" || filter == "Albums") {
            if (results.albums.isNotEmpty()) {
                item {
                    SectionHeader(title = "Albums")
                }
                items(results.albums) { album ->
                    SearchAlbumItem(
                        album = album,
                        onClick = { }
                    )
                }
            }
        }

        if (results.isEmpty(filter)) {
            item {
                NoResultsFound(query = query)
            }
        }
    }
}

@Composable
fun SearchSongItem(
    song: Song,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .glassCard(alpha = 0.08f, cornerRadius = 16.dp)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    SoftPurple.copy(alpha = 0.6f),
                                    SoftBlue.copy(alpha = 0.4f)
                                )
                            ),
                            RoundedCornerShape(12.dp)
                        )
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.15f),
                            RoundedCornerShape(12.dp)
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
                            GlassMedium,
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
}

@Composable
fun SearchArtistItem(
    artist: Artist,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .glassCard(alpha = 0.08f, cornerRadius = 16.dp)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                            Color.White.copy(alpha = 0.15f),
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
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Artist • ${artist.albumCount} albums",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            SoftBlue.copy(alpha = 0.2f),
                            CircleShape
                        )
                        .border(
                            1.dp,
                            SoftBlue.copy(alpha = 0.3f),
                            CircleShape
                        )
                ) {
                    Icon(
                        Icons.Rounded.PersonAdd,
                        contentDescription = "Follow",
                        tint = SoftBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SearchAlbumItem(
    album: Album,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .glassCard(alpha = 0.08f, cornerRadius = 16.dp)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    SoftPink.copy(alpha = 0.6f),
                                    SoftPurple.copy(alpha = 0.4f)
                                )
                            ),
                            RoundedCornerShape(12.dp)
                        )
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.15f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.Album,
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
                        text = album.title,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Album • ${album.artist} • ${album.year}",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            SoftPurple.copy(alpha = 0.2f),
                            CircleShape
                        )
                        .border(
                            1.dp,
                            SoftPurple.copy(alpha = 0.3f),
                            CircleShape
                        )
                ) {
                    Icon(
                        Icons.Rounded.PlayArrow,
                        contentDescription = "Play",
                        tint = SoftPurple,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NoResultsFound(query: String) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .glassCard(alpha = 0.1f, cornerRadius = 24.dp)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                SoftBlue.copy(alpha = 0.3f),
                                SoftPurple.copy(alpha = 0.2f)
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
                    Icons.Rounded.SearchOff,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "No results found",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Try searching with different keywords",
                color = TextSecondary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        color = TextPrimary,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    )
}

data class SearchResults(
    val songs: List<Song> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val albums: List<Album> = emptyList(),
    val playlists: List<Playlist> = emptyList()
) {
    fun isEmpty(filter: String): Boolean {
        return when (filter) {
            "All" -> songs.isEmpty() && artists.isEmpty() && albums.isEmpty() && playlists.isEmpty()
            "Songs" -> songs.isEmpty()
            "Artists" -> artists.isEmpty()
            "Albums" -> albums.isEmpty()
            "Playlists" -> playlists.isEmpty()
            else -> true
        }
    }
}

private fun generateSearchResults(query: String): SearchResults {
    if (query.isEmpty()) return SearchResults()

    val sampleSongs = listOf(
        Song(title = "Blinding Lights", artist = "The Weeknd", album = "After Hours", duration = "3:20", albumArt = ""),
        Song(title = "Watermelon Sugar", artist = "Harry Styles", album = "Fine Line", duration = "2:54", albumArt = ""),
        Song(title = "Levitating", artist = "Dua Lipa", album = "Future Nostalgia", duration = "3:23", albumArt = ""),
        Song(title = "Anti-Hero", artist = "Taylor Swift", album = "Midnights", duration = "3:20", albumArt = ""),
        Song(title = "As It Was", artist = "Harry Styles", album = "Harry's House", duration = "2:47", albumArt = ""),
        Song(title = "Good 4 U", artist = "Olivia Rodrigo", album = "SOUR", duration = "2:58", albumArt = ""),
        Song(title = "Stay", artist = "The Kid LAROI & Justin Bieber", album = "F*CK LOVE 3", duration = "2:21", albumArt = ""),
        Song(title = "Heat Waves", artist = "Glass Animals", album = "Dreamland", duration = "3:58", albumArt = "")
    )

    val sampleArtists = listOf(
        Artist(id = "1", name = "The Weeknd", albumCount = 5, songCount = 52),
        Artist(id = "2", name = "Harry Styles", albumCount = 3, songCount = 34),
        Artist(id = "3", name = "Dua Lipa", albumCount = 2, songCount = 28),
        Artist(id = "4", name = "Taylor Swift", albumCount = 10, songCount = 156),
        Artist(id = "5", name = "Olivia Rodrigo", albumCount = 1, songCount = 11),
        Artist(id = "6", name = "Glass Animals", albumCount = 3, songCount = 42)
    )

    val sampleAlbums = listOf(
        Album(id = "1", title = "After Hours", artist = "The Weeknd", year = 2020, songs = emptyList()),
        Album(id = "2", title = "Fine Line", artist = "Harry Styles", year = 2019, songs = emptyList()),
        Album(id = "3", title = "Future Nostalgia", artist = "Dua Lipa", year = 2020, songs = emptyList()),
        Album(id = "4", title = "Midnights", artist = "Taylor Swift", year = 2022, songs = emptyList()),
        Album(id = "5", title = "Harry's House", artist = "Harry Styles", year = 2022, songs = emptyList()),
        Album(id = "6", title = "SOUR", artist = "Olivia Rodrigo", year = 2021, songs = emptyList()),
        Album(id = "7", title = "Dreamland", artist = "Glass Animals", year = 2020, songs = emptyList())
    )

    val filteredSongs = sampleSongs.filter {
        it.title.contains(query, ignoreCase = true) ||
                it.artist.contains(query, ignoreCase = true) ||
                it.album.contains(query, ignoreCase = true)
    }

    val filteredArtists = sampleArtists.filter {
        it.name.contains(query, ignoreCase = true)
    }

    val filteredAlbums = sampleAlbums.filter {
        it.title.contains(query, ignoreCase = true) ||
                it.artist.contains(query, ignoreCase = true)
    }

    return SearchResults(
        songs = filteredSongs,
        artists = filteredArtists,
        albums = filteredAlbums
    )
}