package com.example.mp3player.di

import androidx.preference.PreferenceManager
import com.example.mp3player.BuildConfig
import com.example.mp3player.MainActivityViewModel
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

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



    single { Moshi.Builder().build() }

//    viewModel { PhotoListViewModel(get()) }
    viewModel { MainActivityViewModel() }

}
