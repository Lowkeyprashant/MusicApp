package com.codewithprashant.musicapp

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E),
                        Color(0xFF0F3460)
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SettingsTopBar(onBackClick = onBackClick)
            }

            item {
                ProfileSection()
            }

            item {
                PlaybackSection()
            }

            item {
                AudioSection()
            }

            item {
                DownloadSection()
            }

            item {
                NotificationSection()
            }

            item {
                PrivacySection()
            }

            item {
                AboutSection()
            }
        }
    }
}

@Composable
fun SettingsTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .background(
                    Color.White.copy(alpha = 0.1f),
                    CircleShape
                )
        ) {
            Icon(
                Icons.Rounded.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = "Settings",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ProfileSection() {
    SettingsSection(title = "Profile") {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF667EEA),
                                Color(0xFF764BA2)
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                Color.White.copy(alpha = 0.2f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Music Lover",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "musiclover@example.com",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Premium Member",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Icon(
                        Icons.Rounded.ChevronRight,
                        contentDescription = "Edit Profile",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PlaybackSection() {
    var isOfflineMode by remember { mutableStateOf(false) }
    var isGaplessPlayback by remember { mutableStateOf(true) }
    var isCrossfade by remember { mutableStateOf(false) }
    var audioQuality by remember { mutableStateOf("High") }

    SettingsSection(title = "Playback") {
        SettingsToggleItem(
            icon = Icons.Rounded.CloudOff,
            title = "Offline Mode",
            subtitle = "Only play downloaded music",
            isChecked = isOfflineMode,
            onCheckedChange = { isOfflineMode = it }
        )

        SettingsToggleItem(
            icon = Icons.Rounded.SkipNext,
            title = "Gapless Playback",
            subtitle = "Seamless transitions between songs",
            isChecked = isGaplessPlayback,
            onCheckedChange = { isGaplessPlayback = it }
        )

        SettingsToggleItem(
            icon = Icons.Rounded.Tune,
            title = "Crossfade",
            subtitle = "Fade between tracks",
            isChecked = isCrossfade,
            onCheckedChange = { isCrossfade = it }
        )

        SettingsDropdownItem(
            icon = Icons.Rounded.HighQuality,
            title = "Audio Quality",
            subtitle = "Streaming quality",
            options = listOf("Low", "Normal", "High", "Very High"),
            selectedOption = audioQuality,
            onOptionSelected = { audioQuality = it }
        )
    }
}

@Composable
fun AudioSection() {
    var isNormalization by remember { mutableStateOf(true) }
    var bassBoost by remember { mutableStateOf(0.5f) }
    var trebleBoost by remember { mutableStateOf(0.3f) }

    SettingsSection(title = "Audio") {
        SettingsToggleItem(
            icon = Icons.Rounded.VolumeUp,
            title = "Volume Normalization",
            subtitle = "Keep volume consistent across tracks",
            isChecked = isNormalization,
            onCheckedChange = { isNormalization = it }
        )

        SettingsSliderItem(
            icon = Icons.Rounded.GraphicEq,
            title = "Bass Boost",
            subtitle = "Enhance low frequencies",
            value = bassBoost,
            onValueChange = { bassBoost = it }
        )

        SettingsSliderItem(
            icon = Icons.Rounded.Equalizer,
            title = "Treble Boost",
            subtitle = "Enhance high frequencies",
            value = trebleBoost,
            onValueChange = { trebleBoost = it }
        )

        SettingsClickableItem(
            icon = Icons.Rounded.Tune,
            title = "Equalizer",
            subtitle = "Customize audio frequencies",
            onClick = { }
        )

        SettingsClickableItem(
            icon = Icons.Rounded.SurroundSound,
            title = "Spatial Audio",
            subtitle = "3D audio experience",
            onClick = { }
        )
    }
}

@Composable
fun DownloadSection() {
    var autoDownload by remember { mutableStateOf(false) }
    var wifiOnly by remember { mutableStateOf(true) }
    var downloadQuality by remember { mutableStateOf("High") }
    var storageLocation by remember { mutableStateOf("Internal Storage") }

    SettingsSection(title = "Downloads") {
        SettingsToggleItem(
            icon = Icons.Rounded.Download,
            title = "Auto Download",
            subtitle = "Automatically download liked songs",
            isChecked = autoDownload,
            onCheckedChange = { autoDownload = it }
        )

        SettingsToggleItem(
            icon = Icons.Rounded.Wifi,
            title = "WiFi Only",
            subtitle = "Download only on WiFi",
            isChecked = wifiOnly,
            onCheckedChange = { wifiOnly = it }
        )

        SettingsDropdownItem(
            icon = Icons.Rounded.HighQuality,
            title = "Download Quality",
            subtitle = "Quality for offline songs",
            options = listOf("Normal", "High", "Very High", "Lossless"),
            selectedOption = downloadQuality,
            onOptionSelected = { downloadQuality = it }
        )

        SettingsDropdownItem(
            icon = Icons.Rounded.Storage,
            title = "Storage Location",
            subtitle = "Where to save downloads",
            options = listOf("Internal Storage", "SD Card"),
            selectedOption = storageLocation,
            onOptionSelected = { storageLocation = it }
        )

        SettingsClickableItem(
            icon = Icons.Rounded.Delete,
            title = "Clear Cache",
            subtitle = "Free up storage space",
            onClick = { }
        )
    }
}

@Composable
fun NotificationSection() {
    var pushNotifications by remember { mutableStateOf(true) }
    var newReleases by remember { mutableStateOf(true) }
    var playlistUpdates by remember { mutableStateOf(false) }
    var concertAlerts by remember { mutableStateOf(true) }

    SettingsSection(title = "Notifications") {
        SettingsToggleItem(
            icon = Icons.Rounded.Notifications,
            title = "Push Notifications",
            subtitle = "Allow app notifications",
            isChecked = pushNotifications,
            onCheckedChange = { pushNotifications = it }
        )

        SettingsToggleItem(
            icon = Icons.Rounded.NewReleases,
            title = "New Releases",
            subtitle = "Notify about new music from followed artists",
            isChecked = newReleases,
            onCheckedChange = { newReleases = it }
        )

        SettingsToggleItem(
            icon = Icons.Rounded.PlaylistPlay,
            title = "Playlist Updates",
            subtitle = "Notify when playlists are updated",
            isChecked = playlistUpdates,
            onCheckedChange = { playlistUpdates = it }
        )

        SettingsToggleItem(
            icon = Icons.Rounded.Event,
            title = "Concert Alerts",
            subtitle = "Notify about concerts near you",
            isChecked = concertAlerts,
            onCheckedChange = { concertAlerts = it }
        )
    }
}

@Composable
fun PrivacySection() {
    var shareListening by remember { mutableStateOf(false) }
    var allowAnalytics by remember { mutableStateOf(true) }
    var personalizedAds by remember { mutableStateOf(false) }

    SettingsSection(title = "Privacy") {
        SettingsToggleItem(
            icon = Icons.Rounded.Share,
            title = "Share Listening Activity",
            subtitle = "Let friends see what you're playing",
            isChecked = shareListening,
            onCheckedChange = { shareListening = it }
        )

        SettingsToggleItem(
            icon = Icons.Rounded.Analytics,
            title = "Usage Analytics",
            subtitle = "Help improve the app",
            isChecked = allowAnalytics,
            onCheckedChange = { allowAnalytics = it }
        )

        SettingsToggleItem(
            icon = Icons.Rounded.AdUnits,
            title = "Personalized Ads",
            subtitle = "Show relevant advertisements",
            isChecked = personalizedAds,
            onCheckedChange = { personalizedAds = it }
        )

        SettingsClickableItem(
            icon = Icons.Rounded.Security,
            title = "Privacy Policy",
            subtitle = "Read our privacy policy",
            onClick = { }
        )

        SettingsClickableItem(
            icon = Icons.Rounded.Assignment,
            title = "Terms of Service",
            subtitle = "Read terms and conditions",
            onClick = { }
        )
    }
}

@Composable
fun AboutSection() {
    SettingsSection(title = "About") {
        SettingsClickableItem(
            icon = Icons.Rounded.Info,
            title = "App Version",
            subtitle = "1.0.0 (Build 123)",
            onClick = { }
        )

        SettingsClickableItem(
            icon = Icons.Rounded.Update,
            title = "Check for Updates",
            subtitle = "See if new version is available",
            onClick = { }
        )

        SettingsClickableItem(
            icon = Icons.Rounded.Help,
            title = "Help & Support",
            subtitle = "Get help or contact support",
            onClick = { }
        )

        SettingsClickableItem(
            icon = Icons.Rounded.BugReport,
            title = "Report a Bug",
            subtitle = "Help us fix issues",
            onClick = { }
        )

        SettingsClickableItem(
            icon = Icons.Rounded.Star,
            title = "Rate the App",
            subtitle = "Leave a review on the Play Store",
            onClick = { }
        )

        SettingsClickableItem(
            icon = Icons.Rounded.Logout,
            title = "Sign Out",
            subtitle = "Sign out of your account",
            onClick = { },
            textColor = Color(0xFFEF4444)
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.05f),
                                Color.White.copy(alpha = 0.02f)
                            )
                        )
                    )
                    .padding(4.dp)
            ) {
                Column {
                    content()
                }
            }
        }
    }
}

@Composable
fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF6366F1),
                            Color(0xFF8B5CF6)
                        )
                    ),
                    RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF6366F1),
                uncheckedThumbColor = Color.White.copy(alpha = 0.7f),
                uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
            )
        )
    }
}

@Composable
fun SettingsClickableItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    textColor: Color = Color.White
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    if (textColor == Color.White) {
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF6366F1),
                                Color(0xFF8B5CF6)
                            )
                        )
                    } else {
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFEF4444),
                                Color(0xFFDC2626)
                            )
                        )
                    },
                    RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                color = textColor.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }

        Icon(
            Icons.Rounded.ChevronRight,
            contentDescription = "Open",
            tint = textColor.copy(alpha = 0.5f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun SettingsSliderItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF6366F1),
                                Color(0xFF8B5CF6)
                            )
                        ),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color(0xFF6366F1),
                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
fun SettingsDropdownItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF6366F1),
                                Color(0xFF8B5CF6)
                            )
                        ),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }

            Text(
                text = selectedOption,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                Icons.Rounded.ArrowDropDown,
                contentDescription = "Dropdown",
                tint = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(
                Color(0xFF1A1A2E),
                RoundedCornerShape(8.dp)
            )
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            color = if (option == selectedOption) Color(0xFF6366F1) else Color.White
                        )
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}