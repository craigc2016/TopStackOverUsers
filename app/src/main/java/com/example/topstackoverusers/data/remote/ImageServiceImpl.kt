package com.example.topstackoverusers.data.remote

import java.net.URL

interface ImageService {
    fun loadImage(url: String) : ByteArray
}

class ImageServiceImpl : ImageService {

    companion object {
        val TAG = "ImageServiceImpl"
    }

    override fun loadImage(url: String): ByteArray {
        val connection = URL(url).openConnection()
        connection.connectTimeout = 5000
        connection.readTimeout = 5000
        connection.connect()

        return connection.getInputStream().use {
            it.readBytes()
        }
    }
}


