package com.example.wallpaper.ui.processes

import android.graphics.Bitmap

interface ImageProcessor {
    fun processImage(bitmap: Bitmap): Bitmap
}