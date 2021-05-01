package com.unicorn.rise

import android.content.Context
import androidx.multidex.MultiDex
import cn.tee3.n2m.N2MApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module


class App : N2MApplication() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)

            modules(appModule)
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
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