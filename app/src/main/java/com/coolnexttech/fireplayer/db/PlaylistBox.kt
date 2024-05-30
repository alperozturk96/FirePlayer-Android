package com.coolnexttech.fireplayer.db

import io.objectbox.Box


object PlaylistBox {
    private var box: Box<PlaylistEntity> = ObjectBox.store.boxFor(PlaylistEntity::class.java)

    fun getAll(): List<PlaylistEntity> {
        return box.all
    }

    fun add(entity: PlaylistEntity) = box.put(entity)
}