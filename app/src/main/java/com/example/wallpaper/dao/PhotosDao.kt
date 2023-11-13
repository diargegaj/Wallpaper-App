package com.example.wallpaper.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.wallpaper.entity.BaseModelEntity

@Dao
interface PhotosDao {
    @Insert
    fun insertBaseModel(baseModel: BaseModelEntity)

    @Insert
    fun insertBaseModels(baseModelList: List<BaseModelEntity>)

    @Query("SELECT MAX(page) FROM base_model WHERE `query` = :query")
    fun getLastPageInsertedToDbWithQuery(query: String): Int?

    @Query("SELECT * FROM base_model WHERE `query` = :query")
    fun getBaseModels(query: String): LiveData<List<BaseModelEntity>>

}
