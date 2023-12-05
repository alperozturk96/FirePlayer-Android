package com.coolnexttech.fireplayer.model

import com.coolnexttech.fireplayer.R

enum class FilterOptions {
    Title, Artist, Album;

    fun searchTitleId(): Int {
        return when (this) {
            Title -> R.string.home_search_in_titles_text
            Album -> R.string.home_search_in_albums_text
            Artist -> R.string.home_search_in_artists_text
        }
    }

    fun filterOptionIconId(): Int {
        return when (this) {
            Title -> R.drawable.ic_title
            Album -> R.drawable.ic_album
            Artist -> R.drawable.ic_artist
        }
    }

    fun selectNextFilterOption(): FilterOptions {
        return when (this) {
            Title -> Artist
            Artist -> Album
            Album -> Title
        }
    }
}
