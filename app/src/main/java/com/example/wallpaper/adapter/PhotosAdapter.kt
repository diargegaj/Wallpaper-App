package com.example.wallpaper.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.wallpaper.R
import com.example.wallpaper.entity.BaseModelEntity
import com.example.wallpaper.ui.TAG


class PhotosAdapter: RecyclerView.Adapter<PhotosAdapter.PhotoViewHolder>() {
    private var links: ArrayList<BaseModelEntity> = ArrayList()
    private var imageInteraction: ImageInteraction? = null
    @JvmName("setLinks1")

    fun setLinks(links: ArrayList<BaseModelEntity>){
        this.links = links
        notifyDataSetChanged()
    }

    fun setOnClickListener(imageInteraction: ImageInteraction){
        this.imageInteraction = imageInteraction
    }

    fun add(baseModel: BaseModelEntity){
        links.add(baseModel)
        notifyItemInserted(links.size-1)
    }

    fun addAll(baseModelList: List<BaseModelEntity>) {
        links.addAll(baseModelList)
        notifyDataSetChanged()
    }

    fun addBaseModelsAndClearOthers(baseModelList: List<BaseModelEntity>){
        links.clear()
        Log.d(TAG, "clear and add other models", Throwable())
        links.addAll(baseModelList)
        notifyDataSetChanged()
    }

    fun clearAll(){
        links.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.photo_item, parent, false)
        return PhotoViewHolder(view, imageInteraction)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val model = links[position]
        Log.d(TAG, "on bind view holder $position ${model.altDescription}")
        holder.fillView(model.description, model.urls.small, model.urls.full)
    }

    override fun getItemCount(): Int {
        return links.size
    }

    interface ImageInteraction{
        fun openPhoto(lowQualityLink: String, fullQualityLink: String, description: String?)
    }

    class PhotoViewHolder(itemView: View, private var imageInteraction: ImageInteraction?) : RecyclerView.ViewHolder(itemView) {
        private var image: ImageView = itemView.findViewById(R.id.photo)
        private var photoTitle: TextView = itemView.findViewById(R.id.photo_title)

        fun fillView(photoTitle: String?, lowQualityLink: String, fullQualityLink: String){
            Glide.with(itemView)
                .load(lowQualityLink)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(image)

            image.setOnClickListener{
                imageInteraction?.openPhoto(lowQualityLink, fullQualityLink, photoTitle)
            }

            this.photoTitle.text = photoTitle
        }
    }
}