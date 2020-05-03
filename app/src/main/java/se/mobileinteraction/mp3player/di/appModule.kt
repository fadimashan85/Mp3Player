package se.mobileinteraction.mp3player.di

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.preference.PreferenceManager
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import se.mobileinteraction.mp3player.BuildConfig
import se.mobileinteraction.mp3player.MainActivityViewModel
import se.mobileinteraction.mp3player.ui.Player.PlayerRepo
import se.mobileinteraction.mp3player.ui.Player.PlayerViewModel
import se.mobileinteraction.mp3player.ui.Recorder.RecorderRepo
import se.mobileinteraction.mp3player.ui.Recorder.RecorderViewModel
import se.mobileinteraction.mp3player.ui.SeekBar.SeekBarViewModel
import se.mobileinteraction.mp3player.ui.Storage.StorageViewModel
import se.mobileinteraction.mp3player.ui.WavRecorder.WavRecorderRepo
import se.mobileinteraction.mp3player.ui.WavRecorder.WavRecorderViewModel

@RequiresApi(Build.VERSION_CODES.O)
val appModule = module {
    factory<CallAdapter.Factory> { RxJava2CallAdapterFactory.create() }
    factory<Converter.Factory> { MoshiConverterFactory.create() }
    single {
        val clientBuilder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) clientBuilder.addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })

//        Retrofit.Builder()
//            .addCallAdapterFactory(get())
//            .addConverterFactory(get())
//            .baseUrl(androidApplication().getString(R.string.base_api))
//            .client(clientBuilder.build())
//            .build()
    }

//    single {
//        val retrofit: Retrofit = get()
//        retrofit.create(UnsplashApi::class.java)
//    }

    single {
        PreferenceManager.getDefaultSharedPreferences(androidContext())
    }

    single {
        MediaPlayer().apply {
            AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        }
    }
    single {
        androidContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }



    single {
        RecorderRepo(androidApplication())
    }

    single {
        PlayerRepo(androidApplication(), get())
    }

    single {
        WavRecorderRepo(androidContext())
    }

    single { Moshi.Builder().build() }

//    viewModel { PhotoListViewModel(get()) }
    viewModel { MainActivityViewModel(get()) }
    viewModel { RecorderViewModel(get()) }
    viewModel { PlayerViewModel(get()) }
    viewModel { WavRecorderViewModel(get()) }
    viewModel { SeekBarViewModel(get(),get()) }
    viewModel { StorageViewModel() }

}
