package com.example.mp3player.utils

import android.content.Context
import com.example.mp3player.MainApplication
import org.koin.android.ext.android.getKoin
import org.koin.core.Koin


fun Context.koin() : Koin {
    return (this.applicationContext as? MainApplication)?.getKoin() ?: error("Koin can only be fetched from an application context")
}
