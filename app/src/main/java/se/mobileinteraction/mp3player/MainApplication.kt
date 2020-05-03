package se.mobileinteraction.mp3player

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import se.mobileinteraction.mp3player.di.appModule

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
