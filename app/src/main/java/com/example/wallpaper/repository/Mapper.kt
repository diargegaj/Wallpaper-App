package com.example.wallpaper.repository

import com.example.wallpaper.entity.BaseModelEntity
import com.example.wallpaper.model.Urls
import com.example.wallpaper.model.BaseModel

class Mapper {

    fun covertListOfBaseModelToListOfBaseModelEntity(page: Int,query: String, baseModelList:List<BaseModel>): List<BaseModelEntity>{
        val baseModelEntityList: ArrayList<BaseModelEntity> = ArrayList()

        for (baseModel in baseModelList){
            baseModelEntityList.add(convertBaseModelToBaseModelEntity(page, query, baseModel))
        }

        return baseModelEntityList
    }

    private fun convertBaseModelToBaseModelEntity(page: Int,query: String, baseModel: BaseModel): BaseModelEntity {
        return BaseModelEntity(
            baseModel.id,
            page,
            query,
            baseModel.description,
            baseModel.alt_description,
            Urls(
                baseModel.urls.raw,
                baseModel.urls.full,
                baseModel.urls.regular,
                baseModel.urls.small,
                baseModel.urls.thumb
            )
        )
    }


}