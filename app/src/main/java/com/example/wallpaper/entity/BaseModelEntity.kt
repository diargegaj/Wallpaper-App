package com.example.wallpaper.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.wallpaper.model.Urls


@Entity(tableName = "base_model")
data class BaseModelEntity (
	@ColumnInfo(name = "id") val id : String?,
	@ColumnInfo(name = "page") val page: Int,
	@ColumnInfo(name = "query") val query: String,
	@ColumnInfo(name = "description") val description : String?,
	@ColumnInfo(name ="alt_description") val altDescription : String?,
	@Embedded val urls: Urls
){
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "base_model_id")
	var baseModelId: Int = 0
}