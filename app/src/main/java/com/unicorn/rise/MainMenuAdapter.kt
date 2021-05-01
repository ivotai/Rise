package com.unicorn.rise

import android.graphics.Color
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.utils.colorInt
import com.mikepenz.iconics.utils.sizeDp
import com.mikepenz.iconics.view.IconicsImageView
import com.unicorn.rise.model.MainMenu

class MainMenuAdapter : BaseQuickAdapter<MainMenu, BaseViewHolder>(R.layout.item_main_menu) {

    override fun convert(holder: BaseViewHolder, item: MainMenu) {
        holder.setText(R.id.textView, item.name)
        val iconicImageView = holder.getView<IconicsImageView>(R.id.iconicsImageView)
        iconicImageView.icon = IconicsDrawable(context, item.icon).apply {
            colorInt = Color.parseColor("#002564")
            sizeDp = 56
        }
    }

}