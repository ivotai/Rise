package com.unicorn.rise

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ruffian.library.widget.RConstraintLayout
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
            mainMenuAdapter.setNewInstance(
                mutableListOf(
                    MainMenu(name = "远程接谈"),
                    MainMenu(name = "信息互动"),
                    MainMenu(name = "材料须知"),
                    MainMenu(name = "进度查询"),
                    MainMenu(name = "信访历史"),
                    MainMenu(name = "回执签收")
                )
            )
        }
        initRecyclerView()
    }

}