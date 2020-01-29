package com.example.mp3player

import android.app.Application
import com.example.mp3player.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

import timber.log.Timber

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
        startKoin {
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
}
