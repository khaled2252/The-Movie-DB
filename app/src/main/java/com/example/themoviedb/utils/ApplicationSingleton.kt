package com.example.themoviedb.utils

import android.app.Application

class ApplicationSingleton : Application() {
    companion object {
        lateinit var instance: ApplicationSingleton
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}