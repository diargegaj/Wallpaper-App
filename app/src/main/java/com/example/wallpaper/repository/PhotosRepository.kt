package com.example.wallpaper.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.wallpaper.API.UnsplashApiService
import com.example.wallpaper.entity.BaseModelEntity
import com.example.wallpaper.instances.RetrofitInstance
import io.reactivex.schedulers.Schedulers
import com.example.wallpaper.ui.TAG

class PhotosRepository {
    private val mapper = Mapper()
    private val databaseHelper: DatabaseHelper = DatabaseHelper()

    fun getLastPageInsertedToDbWithQuery(query: String): Int{
        return databaseHelper.getLastPageInsertedToDbWithQuery(query)
    }

    fun searchPhotos(query: String, page: Int): LiveData<List<BaseModelEntity>> {

        if (query.isEmpty()){
            val dis = RetrofitInstance.unsplashServic.getPhotos("vyy3DkAHac_AN_WSETYm4w0xuAWjxdDpbN6CzvWIWhw", page.toString())
                .subscribeOn(Schedulers.io())
                .map {baseModelList -> mapper.covertListOfBaseModelToListOfBaseModelEntity(page, query, baseModelList) }
                .subscribe(
                    { baseModelsEntity ->
                        Log.d(TAG, "on next repo")
                        databaseHelper.insertBaseModels(baseModelsEntity)
                    },
                    {
                        it.printStackTrace()
                    }
                )

            return databaseHelper.getBaseModel(query)

        }else {
            Log.d(TAG, "search photos from repo")

            val dis = RetrofitInstance.unsplashServic.searchPhotos(query, UnsplashApiService.CLIENT_ID, page.toString())
                .subscribeOn(Schedulers.io())
                .map { searchPhotosModel -> searchPhotosModel.baseModel }
                .map { baseModels -> mapper.covertListOfBaseModelToListOfBaseModelEntity(page, query, baseModels) }
                .subscribe(
                    { baseModel ->
                        baseModel.take(5).forEach {
                            Log.d(TAG, "search photos on next $query,  results: $it")
                        }
                        databaseHelper.insertBaseModels(baseModel)
                    },
                    {
                        it.printStackTrace()
                    }
                )

            return databaseHelper.getBaseModel(query)
        }

    }
}