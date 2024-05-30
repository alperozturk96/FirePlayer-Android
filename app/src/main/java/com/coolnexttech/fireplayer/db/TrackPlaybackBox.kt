package com.coolnexttech.fireplayer.db

import io.objectbox.Box

object TrackPlaybackBox {
    private var box: Box<TrackPlaybackEntity> = ObjectBox.store.boxFor(TrackPlaybackEntity::class.java)

    fun remove(id: Long) = box.remove(id)

    fun add(id: Long?, position: Long?) {
        if (id == null || position == null) return
        box.put(TrackPlaybackEntity(id, id, position))
    }

    fun read(id: Long): TrackPlaybackEntity {
        return box.get(id)
    }

}