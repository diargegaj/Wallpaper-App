package com.example.wallpaper.model

import com.google.gson.annotations.SerializedName

data class SearchPhotoModel(
        @SerializedName("results")
        val baseModel: List<BaseModel>
)
