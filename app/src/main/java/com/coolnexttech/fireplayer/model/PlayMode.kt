package com.coolnexttech.fireplayer.model

import com.coolnexttech.fireplayer.R

enum class PlayMode {
    Shuffle, Sequential;

    fun getIconId(): Int {
        return when (this) {
            Shuffle -> R.drawable.ic_shuffle
            Sequential -> R.drawable.ic_sequential
        }
    }

    fun selectNextPlayMode(): PlayMode {
       return when (this) {
            Shuffle -> Sequential
            Sequential -> Shuffle
        }
    }
}