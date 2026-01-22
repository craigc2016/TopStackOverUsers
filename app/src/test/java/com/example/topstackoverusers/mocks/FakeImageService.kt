package com.example.topstackoverusers.mocks

import com.example.topstackoverusers.data.remote.ImageService


class FakeImageService(private val bytes: ByteArray? = null): ImageService {

    override fun loadImage(url: String): ByteArray {
        return bytes ?: throw Exception("Failed to load image")
    }
}