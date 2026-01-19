package com.example.topstackoverusers.data.remote

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.net.URL

interface ImageService {
    fun loadImage(url: String) : Bitmap?
}

class ImageServiceImpl : ImageService {

    companion object {
        val TAG = "ImageServiceImpl"
    }

    override fun loadImage(url: String): Bitmap? {
        return try {
            val connection = URL(url).openConnection()
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.useCaches = true
            connection.doInput = true
            connection.connect()

            connection.getInputStream().use {
                BitmapFactory.decodeStream(it)
            }
        }catch (e: Exception) {
            Log.e(TAG, "Error loading image", e)
            return null
        }
    }
}

