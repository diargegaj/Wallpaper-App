package com.example.wallpaper.API

import com.example.wallpaper.model.BaseModel
import com.example.wallpaper.model.SearchPhotoModel
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashApiService {
    @GET("photos/?per_page=30")
    fun getPhotos(@Query("client_id") clientId: String, @Query("page") page: String): Observable<List<BaseModel>>

    @GET("search/photos/?per_page=30")
    fun searchPhotos(@Query("query") query: String, @Query("client_id") clientId: String, @Query("page") page: String): Observable<SearchPhotoModel>

    companion object{
        const val CLIENT_ID = "vyy3DkAHac_AN_WSETYm4w0xuAWjxdDpbN6CzvWIWhw"
        const val URL = "https://api.unsplash.com/"
    }
}