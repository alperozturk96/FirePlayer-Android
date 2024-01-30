package com.coolnexttech.fireplayer.model

import com.coolnexttech.fireplayer.R

enum class PlayMode {
    Shuffle, Sequential, Loop;

    fun getIconId(): Int {
        return when (this) {
            Shuffle -> R.drawable.ic_shuffle
            Sequential -> R.drawable.ic_sequential
            Loop -> R.drawable.ic_loop
        }
    }

    fun selectNextPlayMode(): PlayMode {
       return when (this) {
            Shuffle -> Sequential
            Sequential -> Loop
            Loop -> Shuffle
        }
    }
}