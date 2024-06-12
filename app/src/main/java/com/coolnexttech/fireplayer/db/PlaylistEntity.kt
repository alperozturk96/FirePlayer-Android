package com.coolnexttech.fireplayer.db

import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany

@Entity
data class PlaylistEntity(
    @Id(assignable = true)
    var id: Long = 0,
    var title: String = "",
) {
    @Backlink(to = "playlists")
    lateinit var tracks: ToMany<TrackEntity>
}