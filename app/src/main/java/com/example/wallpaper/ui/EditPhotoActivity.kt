package com.example.wallpaper.ui

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.wallpaper.R
import com.example.wallpaper.instances.TempFileHelper
import com.example.wallpaper.ui.MainActivity.Companion.EXTRA_URI
import com.example.wallpaper.ui.MainActivity.Companion.FULL_LINK
import com.example.wallpaper.ui.MainActivity.Companion.LOW_LINK
import com.example.wallpaper.ui.processes.ImageProcessor
import com.example.wallpaper.ui.processes.InvertImageProcessor
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*


class EditPhotoActivity : AppCompatActivity() {

    private lateinit var image: ImageView
//    private lateinit var imageAsBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_photo)

        image = findViewById(R.id.photoToEdit)
//        imageAsBitmap = transformImageViewToBitmap(image)

        val invertColors: ImageView = findViewById(R.id.invertColorsIcon)
        val brightnessIcon: ImageView = findViewById(R.id.brightnessIcon)
        val seekBar: SeekBar = findViewById(R.id.seekBar)
        val cropIcon: ImageView = findViewById(R.id.cropIcon)

        val lowQualityLink = intent.getStringExtra(LOW_LINK)
        val fullQualityLink = intent.getStringExtra(FULL_LINK)
        val imgUri = intent.getStringExtra(EXTRA_URI)

        Log.d(TAG, "link: $fullQualityLink")
        Log.d(TAG, "imguri: $imgUri")

        if (imgUri != null) {
            Glide.with(this)
                    .load(Uri.parse(imgUri))
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(image)
        } else {
            Glide.with(this)
                    .load(fullQualityLink)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(image)
        }

        invertColors.setOnClickListener {
            val rsImageProcessor = InvertImageProcessor(this, this)

            image.setImageBitmap(processImage(image, rsImageProcessor))
        }

        brightnessIcon.setOnClickListener {
            if (seekBar.visibility == View.VISIBLE) seekBar.visibility = View.INVISIBLE else seekBar.visibility = View.VISIBLE
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.d(TAG, "progress: $progress")
                image.colorFilter = setBrightness(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        cropIcon.setOnClickListener {
            val bitmapDrawable = image.drawable as BitmapDrawable

            val uri = getImageUri(bitmapDrawable.bitmap)

            Log.d(TAG, "uri of the file: $uri")

            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this)
        }
    }

//    private fun transformImageViewToBitmap(image: ImageView): Bitmap {
//        val bitmapDrawable = image.drawable as BitmapDrawable
//        return bitmapDrawable.bitmap
//    }

    fun setBrightness(progress: Int): PorterDuffColorFilter {
        Log.d(TAG, "progress: $progress")
        val br = 80
        return if (progress >= br) {
            val value = (progress - br) * 255 / br
            PorterDuffColorFilter(Color.argb(value, 255, 255, 255), PorterDuff.Mode.SRC_OVER)
        } else {
            val value = (br - progress) * 255 / br
            PorterDuffColorFilter(Color.argb(value, 0, 0, 0), PorterDuff.Mode.SRC_ATOP)
        }
    }

    private fun getImageUri(inImage: Bitmap): Uri? {
        val matrix = Matrix()
        matrix.postScale(image.scaleX, image.scaleY)

        val resizedBitmap = Bitmap.createBitmap(inImage, 0, 0,
                inImage.width, inImage.height, matrix, true)


        val file = File(TempFileHelper.tempFolder, "${UUID.randomUUID()}.jpeg")
        val fOut = FileOutputStream(file)

        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut)

        return Uri.fromFile(file)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri = result.uri
                image.setImageURI(resultUri)
                clearTempEditedFolder()
                Log.d(TAG, "result uri: $resultUri")
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                error.stackTrace
            }
        }
    }

    private fun clearTempEditedFolder() {
        TempFileHelper.deleteFilesFromAppTempFolder()
    }

    private fun processImage(imageView: ImageView, imageProcessor: ImageProcessor): Bitmap {
        val bitmapDrawable: BitmapDrawable = imageView.drawable as BitmapDrawable

        return imageProcessor.processImage(bitmapDrawable.bitmap)
    }

    @SuppressLint("ResourceType")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        val save: MenuItem? = menu?.findItem(R.id.app_bar_save)
        val setAsHomeScreenWallpaper = menu?.findItem(R.id.setAsHomeScreenWallpaperIcon)
        val setAsLockScreenWallpaper = menu?.findItem(R.id.setAsLockScreenWallpaperIcon)
        val setBothScreensWallpaper = menu?.findItem(R.id.setBothWallpaperIcon)

        save?.setOnMenuItemClickListener {
            saveImageToGalley()

            true
        }

        setAsHomeScreenWallpaper?.setOnMenuItemClickListener {
            Log.d(TAG, "set home screen wallpaper icon clicked")

            if (setImageAsHomeScreenWallpaper()){
                Toast.makeText(this, "Image seted as Home Screen Wallpaper", Toast.LENGTH_SHORT).show()
            }else {
                Toast.makeText(this, "Cannot set image as Home Screen Wallpaper", Toast.LENGTH_SHORT).show()
            }

            true
        }

        setAsLockScreenWallpaper?.setOnMenuItemClickListener {
            Log.d(TAG, "set lock screen wallpaper icon clicked")

            if (setImageAsLockScreenWallpaper()){
                Toast.makeText(this, "Image seted as Lock Screen Wallpaper", Toast.LENGTH_SHORT).show()
            }else {
                Toast.makeText(this, "Cannot set image as Lock Screen Wallpaper", Toast.LENGTH_SHORT).show()
            }

            true
        }

        setBothScreensWallpaper?.setOnMenuItemClickListener {
            Log.d(TAG, "set both screen wallpaper icon clicked")
            setImageInBothScreens()

            Toast.makeText(this, "Image seted in both Screens", Toast.LENGTH_SHORT).show()
            true
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun setImageAsHomeScreenWallpaper(): Boolean{
        val wallpaperManager = WallpaperManager.getInstance(this)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            val bitmapDrawable = image.drawable as BitmapDrawable

            wallpaperManager.setBitmap(bitmapDrawable.bitmap, null, true, WallpaperManager.FLAG_SYSTEM)
            return true
        }
        return false
    }

    private fun setImageInBothScreens() {
        val wallpaperManager = WallpaperManager.getInstance(this)

        val bitmapDrawable = image.drawable as BitmapDrawable
        wallpaperManager.setBitmap(bitmapDrawable.bitmap)
    }

    private fun setImageAsLockScreenWallpaper(): Boolean{
        val wallpaperManager = WallpaperManager.getInstance(this)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            val bitmapDrawable = image.drawable as BitmapDrawable

            wallpaperManager.setBitmap(bitmapDrawable.bitmap, null, true, WallpaperManager.FLAG_LOCK)
            return true
        }
        return false
    }

    private fun saveImageToGalley() {
        val matrix = Matrix()
        matrix.postScale(image.scaleX, image.scaleY)

        val bitmapDrawable = image.drawable as BitmapDrawable
        val resizedBitmap = Bitmap.createBitmap(bitmapDrawable.bitmap, 0, 0,
                bitmapDrawable.bitmap.width, bitmapDrawable.bitmap.height, matrix, true)

        val bytes = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

        MediaStore.Images.Media.insertImage(
                this@EditPhotoActivity.contentResolver,
                resizedBitmap,
                UUID.randomUUID().toString() + ".png",
                "drawing"
        )

        Toast.makeText(this, "The image saved to Pictures.", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "image saved.")
    }
}
