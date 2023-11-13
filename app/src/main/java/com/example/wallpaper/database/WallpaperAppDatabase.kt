package com.example.wallpaper.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.wallpaper.dao.PhotosDao
import com.example.wallpaper.entity.BaseModelEntity

@Database(entities = [BaseModelEntity::class], version = 1, exportSchema = false)
abstract class WallpaperAppDatabase: RoomDatabase() {
    abstract fun getPhotosData(): PhotosDao
}