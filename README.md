# 🎵 MusicApp (Work In Progress) 🚧

MusicApp is an Android app built using **Jetpack Compose** that allows users to explore and play music stored on their devices. It fetches songs using `MediaStore` and provides a beautiful UI for listing and interacting with tracks.

> **🚧 This project is currently under active development. Expect frequent changes and improvements.**

---

## ✨ Features

- 📂 Load songs from device storage
- 🎨 Modern UI with Material 3 + Jetpack Compose
- 🔐 Runtime permission handling (READ_MEDIA_AUDIO / READ_EXTERNAL_STORAGE)
- 🎧 Play interactions with song previews (via Toast for now)
- 📀 Support for demo music when no songs are found

---

## 📁 Project Structure

- `MainActivity.kt` – App entry point and main UI setup
- `MusicComponents.kt` – UI components like song cards, list, empty/loading states
- `MusicViewModel.kt` – ViewModel logic to handle permissions and load songs
- `Song.kt` – Data class for song metadata

---

## 🚀 Getting Started# MusicApp

📌 Requirements
Android Studio Flamingo or newer

Min SDK 21+

Jetpack Compose

Internet not required (offline music only)

🛠️ Upcoming Features
🔊 Actual media playback

🖼️ Album art extraction improvements

🔍 Search & filter songs

🎵 Playlist creation

🌙 Dark theme support
