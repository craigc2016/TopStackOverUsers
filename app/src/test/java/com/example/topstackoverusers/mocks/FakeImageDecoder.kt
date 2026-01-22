package com.example.topstackoverusers.mocks

import android.graphics.Bitmap
import com.example.topstackoverusers.domain.Decoder
import okio.IOException

class FakeImageDecoder(private val bitmap: Bitmap? = null): Decoder {

    override fun decode(bytes: ByteArray): Bitmap {
        return bitmap ?: throw IOException("Decode Failed")
    }
}