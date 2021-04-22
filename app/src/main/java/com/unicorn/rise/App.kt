package com.unicorn.rise

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)

            modules(appModule)
        }
    }

    private val appModule = module {
        single<BaseUrl> { BaseUrlImpl() }
    }
}

interface BaseUrl {
    fun baseUrl(): String
}

class BaseUrlImpl() : BaseUrl {
    override fun baseUrl(): String {
        return "http://58.16.65.7:8080"
    }
}