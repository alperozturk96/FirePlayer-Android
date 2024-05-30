package com.coolnexttech.fireplayer

import android.app.Application
import android.content.Context
import com.coolnexttech.fireplayer.db.ObjectBox
import java.lang.ref.WeakReference

lateinit var appContext: WeakReference<Context>

class FirePlayer: Application() {

    override fun onCreate() {
        super.onCreate()
        ObjectBox.init(this)
        appContext = WeakReference(this)
    }
}
