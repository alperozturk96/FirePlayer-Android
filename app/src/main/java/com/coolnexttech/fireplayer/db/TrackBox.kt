package com.coolnexttech.fireplayer.db

import io.objectbox.Box

object TrackBox {
    private var box: Box<TrackEntity> = ObjectBox.store.boxFor(TrackEntity::class.java)

    fun remove(id: Long) = box.remove(id)

    fun save(trackEntity: TrackEntity) {
        box.put(trackEntity)
    }

    fun read(id: Long): TrackEntity? {
        return box.get(id)
    }

}