package com.example.wallpaper.ui

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wallpaper.R
import com.example.wallpaper.adapter.PaginationScrollListener
import com.example.wallpaper.adapter.PhotosAdapter
import com.example.wallpaper.entity.*
import com.example.wallpaper.viewModels.PhotosViewModel
import com.jakewharton.rxbinding2.widget.RxSearchView
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit


const val TAG = "Wallpaper_DEBUG"
class MainActivity : AppCompatActivity() {

    private var search: SearchView? = null
    private lateinit var photosViewModel:PhotosViewModel
    private lateinit var adapter:PhotosAdapter
    private lateinit var photosAdapterLayoutManager: GridLayoutManager
    private lateinit var photosRecyclerView: RecyclerView
    private val totalPages = 5
    private var isLoading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        photosAdapterLayoutManager = GridLayoutManager(this, 3)
        photosRecyclerView = findViewById(R.id.photosRecycleView)
        photosRecyclerView.layoutManager = photosAdapterLayoutManager

        photosViewModel = ViewModelProvider(this).get(PhotosViewModel::class.java)
        adapter = PhotosAdapter()
        adapter.setOnClickListener(object : PhotosAdapter.ImageInteraction {

            override fun openPhoto(lowQualityLink: String, fullQualityLink: String, description: String?) {
                Intent(this@MainActivity, FullPhotoActivity::class.java).apply {
                    putExtra(LOW_LINK, lowQualityLink)
                    putExtra(FULL_LINK, fullQualityLink)
                    putExtra(DESCRIPTION, description)
                    startActivity(this)
                }
            }
        })

        photosRecyclerView.addOnScrollListener(object : PaginationScrollListener(photosAdapterLayoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                photosViewModel.currentPage = photosViewModel.currentPage + 1
                Log.d(TAG, "current page: ${photosViewModel.currentPage}")
                loadNextPage()
            }

            override fun getTotalPageCount(): Int {
                return totalPages
            }

            override fun isLastPage(): Boolean {
                return getTotalPageCount() - photosViewModel.currentPage <= 0
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

        })

        photosRecyclerView.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK){
            if (requestCode == PICK_FROM_GALLERY_REQUEST_CODE && data != null) {
                val selectedImage = data.data
                Intent(this, EditPhotoActivity::class.java).also {
                    it.putExtra(EXTRA_URI, selectedImage.toString())
                    startActivity(it)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        search = menu?.findItem(R.id.app_bar_search)?.actionView as SearchView
        val edit = menu.findItem(R.id.editIcon)

        edit.setOnMenuItemClickListener {
            Log.d(TAG, "Edit Icon Clicked")
            val pickFromGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickFromGallery, PICK_FROM_GALLERY_REQUEST_CODE)
            return@setOnMenuItemClickListener true
        }

        val dis = RxSearchView.queryTextChanges(search!!)
                .debounce(200, TimeUnit.MILLISECONDS)
                .map(CharSequence::toString)
                .observeOn(AndroidSchedulers.mainThread())
                .map { query ->
                        Log.d(TAG, "query is not empty $query")
                        photosViewModel.currentPage = photosViewModel.getLastPageInsertedToDbWithQuery(query) + 1
                        query
                }
                .subscribe(
                        { query ->
                            searchPhotos(query, photosViewModel.currentPage)
                            photosViewModel.currentPage += 1
                        },
                        {
                            it.printStackTrace()
                        }
                )


        return super.onCreateOptionsMenu(menu)
    }

    private fun searchPhotos(query: String, page: Int) {
        Log.d(TAG, "search photos main activity")
        photosViewModel.getLastLiveData()?.removeObservers(this)
        Log.d(TAG, "removed last live data...")
        photosViewModel.searchPhotos(query, page).observe(this@MainActivity) { baseModel ->
            baseModel.forEach{
                Log.d(TAG, "search photos main acticity $query,  results: ${it.altDescription}")
            }
            if (baseModel != null) adapter.addBaseModelsAndClearOthers(baseModel)
        }
    }

    private fun loadNextPage() {
        Log.d(TAG, "load next page")

        searchPhotos("${search!!.query}", photosViewModel.currentPage)
    }

    companion object{
        const val LOW_LINK = "LOW_LINK"
        const val FULL_LINK = "FULL_LINK"
        const val DESCRIPTION = "DESCRIPTION"
        const val EXTRA_URI = "EXTRA_URI"
        const val PICK_FROM_GALLERY_REQUEST_CODE = 200
    }
}