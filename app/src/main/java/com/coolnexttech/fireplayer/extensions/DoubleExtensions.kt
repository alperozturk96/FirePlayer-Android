package com.coolnexttech.fireplayer.extensions

fun Double.convertToReadableTime(): String {
    val minutes = this.toInt()
    val seconds = ((this - minutes) * 60).toInt()
    return String.format("%02d:%02d", minutes, seconds)
}