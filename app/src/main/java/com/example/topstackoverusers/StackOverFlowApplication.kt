package com.example.topstackoverusers

import android.app.Application
import com.example.topstackoverusers.di.AppContainer

class StackOverFlowApplication : Application() {
    val appContainer by lazy { AppContainer() }
}