package com.example.topstackoverusers.domain

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.util.Log
import com.example.topstackoverusers.data.remote.ImageServiceImpl.Companion.TAG
import java.nio.ByteBuffer

interface Decoder {
    fun decode(bytes: ByteArray): Bitmap?
}
class ImageDecoder: Decoder {

    companion object {
        private const val MAX_SIZE = 200
    }

    override fun decode(bytes: ByteArray) : Bitmap? {
        return try {
            val buffer = ByteBuffer.wrap(bytes)
            val source = ImageDecoder.createSource(buffer)
            // Manually scale the image down in case it is to big could cause memory issues
            ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                val scale = MAX_SIZE.toFloat() / maxOf(info.size.width, info.size.height)
                val targetWidth = (info.size.width * scale).toInt()
                val targetHeight = (info.size.height * scale).toInt()
                decoder.setTargetSize(targetWidth, targetHeight)
                Log.d(TAG, "Image size: $targetWidth $targetHeight")
            }
        } catch (_: Exception) {
            return null
        }
    }
}