package com.coolnexttech.fireplayer.db

import io.objectbox.Box

object TrackBox {
    private var box: Box<TrackEntity> = ObjectBox.store.boxFor(TrackEntity::class.java)

    fun remove(id: Long) = box.remove(id)

    fun add(trackEntity: TrackEntity) = box.put(trackEntity)

    fun read(id: Long): TrackEntity? = box.get(id)

    fun getAll(): List<TrackEntity> = box.all

    fun addAll(tracks: List<TrackEntity>) = box.put(tracks)
}