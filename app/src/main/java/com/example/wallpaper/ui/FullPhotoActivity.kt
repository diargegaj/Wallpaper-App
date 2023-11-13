package com.example.wallpaper.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.wallpaper.R
import com.example.wallpaper.ui.MainActivity.Companion.DESCRIPTION
import com.example.wallpaper.ui.MainActivity.Companion.FULL_LINK
import com.example.wallpaper.ui.MainActivity.Companion.LOW_LINK
import java.io.File
import java.util.*
import kotlin.math.sqrt


class FullPhotoActivity: AppCompatActivity() {
    private var lowQualityLink: String? = null
    private var fullQualityLink: String? = null
    private lateinit var image: ImageView

    private var scalediff = 0f
    private val NONE = 0
    private val DRAG = 1
    private val ZOOM = 2
    private var mode = NONE
    private var oldDist = 1f


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.full_photo_layout)

        image= findViewById(R.id.fullPhoto)
        lowQualityLink = intent.getStringExtra(LOW_LINK)
        fullQualityLink = intent.getStringExtra(FULL_LINK)

        val description: String? = if (intent.getStringExtra(DESCRIPTION) != null) intent.getStringExtra(DESCRIPTION) else "No info to show"
        val options: LinearLayout = findViewById(R.id.options)
        val downloadIcon: ImageView = findViewById(R.id.downloadIcon)
        val infoIcon: ImageView = findViewById(R.id.infoIcon)
        val editIcon: ImageView = findViewById(R.id.editIcon)

        val requestOptions = RequestOptions()
        requestOptions.placeholder(R.drawable.ic_loading)
        requestOptions.error(R.drawable.ic_error)

        Glide.with(this)
                .setDefaultRequestOptions(requestOptions)
                .load(fullQualityLink)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(image)

        val layoutParams = RelativeLayout.LayoutParams(1920, 1080)
        layoutParams.leftMargin = 0
        layoutParams.topMargin = 0
        layoutParams.bottomMargin = 0
        layoutParams.rightMargin = 0

        image.layoutParams = layoutParams

        image.setOnTouchListener(object : View.OnTouchListener {

            var parms: RelativeLayout.LayoutParams? = layoutParams
            var startwidth = 0
            var startheight = 0

            var dx = 0f
            var dy = 0f
            var x = 0f
            var y = 0f

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                val view = v as ImageView

                (view.drawable as BitmapDrawable).setAntiAlias(true)

                when (event!!.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_DOWN -> {
                        Log.d(TAG, "Action Down")
                        startwidth = parms!!.width
                        startheight = parms!!.height
                        dx = event.rawX - parms!!.leftMargin
                        dy = event.rawY - parms!!.topMargin
                        mode = DRAG

                    }
                    MotionEvent.ACTION_POINTER_DOWN -> {
                        Log.d(TAG, "Action pointer down")
                        oldDist = spacing(event)
                        if (oldDist > 10f) {
                            mode = ZOOM
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        Log.d(TAG, "Action UP")

                        view.layoutParams = layoutParams

                        view.scaleX = 1.0f
                        view.scaleY = 1.0f
//                        view.scaleType = ImageView.ScaleType.FIT_CENTER

                        options.visibility = LinearLayout.VISIBLE
                        mode = NONE

                    }
                    MotionEvent.ACTION_POINTER_UP -> {
                    }

                    MotionEvent.ACTION_MOVE -> {
                        options.visibility = LinearLayout.INVISIBLE

                        if (mode == DRAG) {
                            Log.d(TAG, "Action Move/ DRAG")

                            x = event.rawX
                            y = event.rawY
                            val leftMargin = (x - dx).toInt()
                            val topMargin = (y - dy).toInt()
                            val rightMargin = parms!!.leftMargin + 5 * parms!!.width
                            val bottomMargin = parms!!.topMargin + 10 * parms!!.height

                            view.layoutParams = changeImagePosition(leftMargin, topMargin, rightMargin, bottomMargin)

                        } else if (mode == ZOOM) {
                            Log.d(TAG, "Action Move/ Zoom")

                            if (event.pointerCount == 2) {
                                x = event.rawX
                                y = event.rawY

                                val newDist: Float = spacing(event)
                                if (newDist > 10f) {
                                    val scale: Float = newDist / oldDist * view.scaleX
                                    if (scale > 0.6) {
                                        scalediff = scale
                                        view.scaleX = scale
                                        view.scaleY = scale
                                    }
                                }
                                x = event.rawX
                                y = event.rawY

                                val leftMargin = (x - dx + scalediff).toInt()
                                val topMargin = (y - dy + scalediff).toInt()
                                val rightMargin = parms!!.leftMargin + 5 * parms!!.width
                                val bottomMargin = parms!!.topMargin + 10 * parms!!.height

                                view.layoutParams = changeImagePosition(leftMargin, topMargin, rightMargin, bottomMargin)
                            } else {
                                Log.d(TAG, "CHANGE MODE TO DRAG")
                                mode = DRAG
                            }
                        }
                    }
                }
                return true
            }
        })

        downloadIcon.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                downloadImage("${UUID.randomUUID()}", fullQualityLink as String)
            }else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), EXTERNAL_STORAGE_REQUEST_CODE)
            }
        }

        infoIcon.setOnClickListener {
            AlertDialog.Builder(this)
                    .setMessage(description)
                    .show()
        }

        editIcon.setOnClickListener {
            Intent(this, EditPhotoActivity::class.java).apply {
                putExtra(LOW_LINK, lowQualityLink)
                putExtra(FULL_LINK, fullQualityLink)
                startActivity(this)
            }
        }
    }

    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt((x * x + y * y).toDouble()).toFloat()
    }

    private fun downloadImage(fileName: String, downloadUrlOfImage: String) {
        try {
            val dm: DownloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadUri = Uri.parse(downloadUrlOfImage)
            val request = DownloadManager.Request(downloadUri)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(fileName)
                    .setMimeType("image/jpeg")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "${File.separator}$fileName.jpg")
            dm.enqueue(request)
            Toast.makeText(this, "Image download started.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Image download failed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun changeImagePosition(leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int): RelativeLayout.LayoutParams{
        val params: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(850, 500)
        params.leftMargin = leftMargin
        params.topMargin = topMargin

        params.rightMargin = rightMargin
        params.bottomMargin = bottomMargin
        return params
    }

    private fun resetImageToDefaultPosition(): RelativeLayout.LayoutParams{
        return changeImagePosition(0, 0, 0, 0)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == EXTERNAL_STORAGE_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            downloadImage("test", lowQualityLink as String)
        }else{
            Toast.makeText(this, "Permission not granted.", Toast.LENGTH_SHORT).show()
        }
    }

    companion object{
        const val EXTERNAL_STORAGE_REQUEST_CODE = 100
    }
}