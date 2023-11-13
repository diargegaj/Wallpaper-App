package com.example.wallpaper.ui.processes

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.RenderScript
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.wallpaper.edit_image.ScriptC_Invert

class InvertImageProcessor(
    context: Context,
    lifecycleOwner: LifecycleOwner
) : LifecycleObserver, ImageProcessor {

    private val renderScript: RenderScript = RenderScript.create(context)
    private val script: ScriptC_Invert = ScriptC_Invert(renderScript)
    private lateinit var input: Allocation
    private lateinit var output: Allocation
    private lateinit var outputBitmap: Bitmap

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun processImage(bitmap: Bitmap): Bitmap {
        if (!::input.isInitialized || input.bytesSize != bitmap.byteCount){
            destroyAllocations()
            input = Allocation.createFromBitmap(renderScript, bitmap)
            output = Allocation.createFromBitmap(renderScript, bitmap)
            outputBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        }
        input.copyFrom(bitmap)
        script.forEach_root(input, output)
        output.copyTo(outputBitmap)
        return outputBitmap
    }

    private fun destroyAllocations() {
        if (::input.isInitialized) input.destroy()
        if (::output.isInitialized) output.destroy()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(){
        script.destroy()
        renderScript.destroy()
    }
}