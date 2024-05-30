package com.coolnexttech.fireplayer.db

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class TrackPlaybackEntity(
    @Id(assignable = true)
    var id: Long = 0,
    var trackId: Long = 0,
    var position: Long = 0
)