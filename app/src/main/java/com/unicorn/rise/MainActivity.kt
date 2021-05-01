package com.unicorn.rise

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ruffian.library.widget.RConstraintLayout

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
    }

}