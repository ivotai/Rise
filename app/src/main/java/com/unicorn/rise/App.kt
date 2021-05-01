package com.unicorn.rise

import android.app.Application
import android.content.Context
import cn.tee3.avd.AVDEngine
import cn.tee3.avd.ErrorCode
import cn.tee3.avd.RoomInfo
import com.blankj.utilcode.util.ToastUtils
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)

            modules(appModule)
        }


        init(this)

    }

    override fun onTerminate() {
        super.onTerminate()
        AVDEngine.instance().uninit()
    }

    private val listener = object : AVDEngine.Listener {
        override fun onInitResult(result: Int) {
            if (ErrorCode.AVD_OK != result) {
                val err = "初始化失败 $result"
                ToastUtils.showLong(err)
                return
            }
        }

        override fun onUninitResult(p0: Int) {
        }

        override fun onGetRoomResult(p0: Int, p1: RoomInfo?) {
        }

        override fun onFindRoomsResult(p0: Int, p1: MutableList<RoomInfo>?) {
        }

        override fun onScheduleRoomResult(p0: Int, p1: String?) {
        }

        override fun onCancelRoomResult(p0: Int, p1: String?) {
        }
    }


    private fun init(context: Context) {
        AVDEngine.instance().init(context, listener, serverUrl, appKey, secretKey)
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