package com.example.wallpaper.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.wallpaper.entity.BaseModelEntity
import com.example.wallpaper.repository.PhotosRepository
import com.example.wallpaper.ui.TAG

class PhotosViewModel: ViewModel() {
    private val photosRepository = PhotosRepository()
    var currentPage: Int
    var searchPhotos: LiveData<List<BaseModelEntity>>? = null

    init {
        currentPage = photosRepository.getLastPageInsertedToDbWithQuery("")
    }

    fun getLastPageInsertedToDbWithQuery(query: String): Int{
        return photosRepository.getLastPageInsertedToDbWithQuery(query)
    }

    fun searchPhotos(query: String, page: Int): LiveData<List<BaseModelEntity>> {
        Log.d(TAG, "view model search photos")
        val searchPhotos = photosRepository.searchPhotos(query, page)
        this.searchPhotos = searchPhotos
        return searchPhotos
    }

    fun getLastLiveData(): LiveData<List<BaseModelEntity>>? {
        return searchPhotos
    }
}