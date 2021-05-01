package com.unicorn.rise

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.tee3.avd.AVDEngine
import cn.tee3.avd.Room
import cn.tee3.avd.User
import cn.tee3.n2m.ui.activity.JoinRoomActivity
import cn.tee3.n2m.ui.activity.RoomLandscapeActivity
import cn.tee3.n2m.ui.util.N2MSetting
import cn.tee3.n2m.ui.util.ToastUtil
import com.ruffian.library.widget.RConstraintLayout
import com.tbruyelle.rxpermissions3.RxPermissions
import com.unicorn.rise.model.MainMenu

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fun setBackgroundColorNormalArray() {
            val helper = findViewById<RConstraintLayout>(R.id.root).helper
            helper.backgroundColorNormalArray = intArrayOf(
                Color.parseColor("#0094D2"),
                Color.parseColor("#002564"),
            )
        }
        setBackgroundColorNormalArray()

        fun initRecyclerView() {
            val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
            recyclerView.layoutManager = GridLayoutManager(this, 3)
            recyclerView.setHasFixedSize(true)

            val mainMenuAdapter = MainMenuAdapter()
            recyclerView.adapter = mainMenuAdapter
            mainMenuAdapter.setNewInstance(MainMenu.all)
        }
        initRecyclerView()

        RxBus.registerEvent(this, JoinRoomEvent::class.java, {
            val intent = Intent()
            intent.setClass(this, JoinRoomActivity::class.java)
            startActivity(intent)
        })

        RxPermissions(this)
            .request(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
            .subscribe { granted ->
                if (granted) { // Always true pre-M
                    // I can control the camera now
                } else {
                    finish()
                    // Oups permission denied
                }
            }
    }

    private fun joinConference() {
        if (N2MSetting.getInstance().isMultiLive) { // only supported 640x480
            N2MSetting.getInstance().saveVideoResolution(1)
            AVDEngine.instance().setOption(
                AVDEngine.Option.eo_camera_capability_default,
                N2MSetting.getInstance().videoResolutionOption
            )
        }
        AVDEngine.instance().setOption(AVDEngine.Option.eo_audio_autoGainControl_Enable, "false")
        val room = Room.obtain(ConstValue.roomId)
        room!!.setOption(Room.Option.ro_audio_option_codec, "opus")
        if (null == room) {
            ToastUtil.showToast(this, R.string.errNum)
            return
        }
        room.setOption(Room.Option.ro_media_use_dtls, "false")
        val user = User(N2MSetting.getInstance().userId, "测试用户", "")
        val password: String = ""
            room.join(user, password, listener)
    }


    private val listener = Room.JoinResultListener {
        if (0 != it) {
            val err = getString(R.string.joinError) + it
            ToastUtil.showLongToast(this, err)
            return@JoinResultListener
        }

        val intent = Intent()
        intent.setClass(this, RoomLandscapeActivity::class.java)
        startActivity(intent)
    }

}