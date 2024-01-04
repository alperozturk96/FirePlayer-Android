package com.coolnexttech.fireplayer.player.helper

import androidx.annotation.OptIn
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi

@OptIn(UnstableApi::class)
class MediaSessionForwardingPlayer(
    player: Player,
    private val playPreviousTrack: () -> Unit,
    private val playNextTrack: () -> Unit
) :
    ForwardingPlayer(player) {

    override fun getAvailableCommands(): Player.Commands {
        return super.getAvailableCommands().buildUpon()
            .add(Player.COMMAND_SEEK_TO_NEXT)
            .build()
    }

    override fun isCommandAvailable(command: Int): Boolean {
        return availableCommands.contains(command)
    }

    override fun seekToPrevious() {
        playPreviousTrack()
    }

    override fun seekToNext() {
        playNextTrack()
    }
}
