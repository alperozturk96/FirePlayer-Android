package com.coolnexttech.fireplayer.db

import io.objectbox.Box


object PlaylistBox {
    private var box: Box<PlaylistEntity> = ObjectBox.store.boxFor(PlaylistEntity::class.java)

    fun getAll(): List<PlaylistEntity> {
        return box.all
    }

    fun add(entity: PlaylistEntity) = box.put(entity)

    fun addAll(entity: List<PlaylistEntity>) = box.put(entity)

    fun getByTitle(title: String): PlaylistEntity? {
        box.all.forEach {
            if (it.title == title) {
                return it
            }
        }

        return null
    }

    fun removeByTitle(title: String) {
        val entity = getByTitle(title) ?: return
        box.remove(entity)
    }
}