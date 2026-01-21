package com.example.topstackoverusers.data.remote

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.util.Log
import java.net.URL
import java.nio.ByteBuffer

interface ImageService {
    fun loadImage(url: String) : Bitmap?
}

class ImageServiceImpl : ImageService {

    companion object {
        val TAG = "ImageServiceImpl"
        private const val MAX_SIZE = 200
    }

    override fun loadImage(url: String): Bitmap? {
        return try {
            val connection = URL(url).openConnection()
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.connect()

            connection.getInputStream().use {
                val bytes = it.readBytes()
                val buffer = ByteBuffer.wrap(bytes)
                val source = ImageDecoder.createSource(buffer)
                ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                    val scale = MAX_SIZE.toFloat() / maxOf(info.size.width, info.size.height)
                    val targetWidth = (info.size.width * scale).toInt()
                    val targetHeight = (info.size.height * scale).toInt()
                    decoder.setTargetSize(targetWidth, targetHeight)
                    Log.d(TAG, "Image size: $targetWidth $targetHeight")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading image", e)
            return null
        }
    }
}

