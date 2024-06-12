package com.coolnexttech.fireplayer.db

import io.objectbox.Box

object PlaylistBox {
    private var box: Box<PlaylistEntity> = ObjectBox.store.boxFor(PlaylistEntity::class.java)

    fun getAll(): List<PlaylistEntity> {
        return box.all
    }

    fun get(id: Long): PlaylistEntity = box.get(id)

    fun add(entity: PlaylistEntity) = box.put(entity)

    fun addAll(entity: List<PlaylistEntity>) = box.put(entity)

    fun remove(id: Long) = box.remove(id)
}