package com.example.wallpaper.app

import android.app.Application
import android.content.Context

class WallpaperApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        var instance: Context? = null
            private set
    }
}