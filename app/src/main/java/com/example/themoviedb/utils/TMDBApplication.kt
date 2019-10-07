package com.example.themoviedb.utils

import android.app.Application
import com.example.themoviedb.dagger.AppComponent
import com.example.themoviedb.dagger.AppModule
import com.example.themoviedb.dagger.DaggerAppComponent

class TMDBApplication : Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = initDagger(this)
    }

    private fun initDagger(app: TMDBApplication): AppComponent =
        DaggerAppComponent.builder()
            .appModule(AppModule(app))
            .build()
}
