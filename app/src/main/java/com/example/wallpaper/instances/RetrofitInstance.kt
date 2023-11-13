package com.example.wallpaper.instances

import com.example.wallpaper.API.UnsplashApiService
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val retroift: Retrofit = Retrofit.Builder()
        .baseUrl(UnsplashApiService.URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    val unsplashServic: UnsplashApiService by lazy {
        retroift.create(UnsplashApiService::class.java)
    }
}