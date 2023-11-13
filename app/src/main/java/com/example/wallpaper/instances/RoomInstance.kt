package com.example.wallpaper.instances

import androidx.room.Room
import com.example.wallpaper.app.WallpaperApp
import com.example.wallpaper.database.WallpaperAppDatabase

object RoomInstance {
    val db:WallpaperAppDatabase by lazy {
        Room.databaseBuilder(WallpaperApp.instance!!, WallpaperAppDatabase::class.java, "wallpaper-app-db")
                .allowMainThreadQueries()
                .build()
    }
}