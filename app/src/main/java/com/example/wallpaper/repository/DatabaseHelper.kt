package com.example.wallpaper.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.wallpaper.entity.BaseModelEntity
import com.example.wallpaper.instances.RoomInstance
import com.example.wallpaper.ui.TAG

class DatabaseHelper {

    private val photosDao = RoomInstance.db.getPhotosData()

    fun getBaseModel(query: String): LiveData<List<BaseModelEntity>> {
        Log.d(TAG, "get photos from db helper")
        return photosDao.getBaseModels(query)
    }

    fun insertBaseModels(baseModelAndUrls: List<BaseModelEntity>){
        photosDao.insertBaseModels(baseModelAndUrls)
    }

    fun getLastPageInsertedToDbWithQuery(query: String): Int {
        return if (photosDao.getLastPageInsertedToDbWithQuery(query) == null) 0 else photosDao.getLastPageInsertedToDbWithQuery(query)!!
    }
}