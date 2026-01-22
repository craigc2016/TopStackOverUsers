package com.example.topstackoverusers.domain

import android.graphics.Bitmap
import com.example.topstackoverusers.data.repository.StackOverFlowRepository

class LoadImageUseCase(
    private val stackOverFlowRepository: StackOverFlowRepository,
    private val imageDecoder: Decoder
) {
    suspend operator fun invoke(url: String) : Bitmap? {
        val bytes = stackOverFlowRepository.loadImage(url)
        return imageDecoder.decode(bytes)
    }
}