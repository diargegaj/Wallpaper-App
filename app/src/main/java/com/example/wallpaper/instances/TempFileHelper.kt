package com.example.wallpaper.instances

import android.os.Environment
import android.util.Log
import com.example.wallpaper.ui.TAG
import java.io.File

object TempFileHelper {

    val tempFolder by lazy {
        val directory = File(Environment.getExternalStorageDirectory().toString() + "/EditedPhotos/")

        if (!directory.exists()){
            directory.mkdir()
        }

        directory
    }

    fun deleteFilesFromAppTempFolder() {
        tempFolder.list().forEach {
            Log.d(TAG, "file name $it")
            val t = File(tempFolder, it).delete()
            Log.d(TAG, "File deleted: $t")
        }
    }
}