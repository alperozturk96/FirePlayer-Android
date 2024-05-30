package com.coolnexttech.fireplayer.db

import android.content.Context
import io.objectbox.BoxStore

object ObjectBox {
    private lateinit var store: BoxStore

    fun init(context: Context) {
        store = MyObjectBox.builder()
            .androidContext(context)
            .build()
    }
}