package com.unicorn.rise

import android.graphics.Color
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.utils.colorInt
import com.mikepenz.iconics.utils.sizeDp
import com.mikepenz.iconics.view.IconicsImageView
import com.unicorn.rise.model.MainMenu
import kotlinx.android.synthetic.main.item_main_menu.*

class MainMenuAdapter : BaseQuickAdapter<MainMenu, KotlinViewHolder>(R.layout.item_main_menu) {

    override fun convert(holder: KotlinViewHolder, item: MainMenu) {
        holder.setText(R.id.textView, item.name)
        val iconicImageView = holder.getView<IconicsImageView>(R.id.iconicsImageView)
        iconicImageView.icon = IconicsDrawable(context, item.icon).apply {
            colorInt = Color.parseColor("#002564")
            sizeDp = 56
        }
    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): KotlinViewHolder {
        val viewHolder = super.onCreateDefViewHolder(parent, viewType)
        viewHolder.item.safeClicks().subscribe {
            var position = viewHolder.adapterPosition
            if (position == RecyclerView.NO_POSITION) return@subscribe
//            position -= headerLayoutCount
            val item = data[position]
            if (item.name == "远程接谈") {
                RxBus.post(JoinRoomEvent())
            }
        }
        return viewHolder
    }

}