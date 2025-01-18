package com.example.studentsapp.model

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.cloudinary.android.policy.GlobalUploadPolicy
import com.example.studentsapp.BuildConfig
import com.example.studentsapp.base.MyApplication
import java.io.File
import java.io.FileOutputStream

class CloudinaryModel {
    init {
        val config = mapOf(
            "cloud_name" to BuildConfig.CLOUD_NAME,
            "api_key" to BuildConfig.API_KEY,
            "api_secret" to BuildConfig.API_SECRET
        )

        MyApplication.Globals.appContext?.let {
            MediaManager.init(it, config)
            MediaManager.get().globalUploadPolicy = GlobalUploadPolicy.defaultPolicy()
        }
    }

    fun uploadImage(bitmap: Bitmap, name: String, callback: (String) -> Unit) {
        val context = MyApplication.Globals.appContext ?: return
        val  file: File = bitmapToFile(bitmap, context, name)

        MediaManager.get().upload(file.path).option("folder", "images").callback(object: UploadCallback {
            override fun onStart(requestId: String?) {
            }

            override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {

            }

            override fun onSuccess(requestId: String?, resultData: MutableMap<*, *>?) {
                val url = resultData?.get("secure_url") as? String ?: ""
                callback(url)
            }

            override fun onError(requestId: String?, error: ErrorInfo?) {
                callback("")
            }

            override fun onReschedule(requestId: String?, error: ErrorInfo?) {
            }

        }).dispatch()
    }

    private fun bitmapToFile(bitmap: Bitmap, context: Context, name: String): File {
        val file = File(context.cacheDir, "image_$name.jpg")
        FileOutputStream(file).use { stream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        }

        return file;
    }
}