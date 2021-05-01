package com.unicorn.rise

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.unicorn.rise.model.MainMenu

class MainMenuAdapter : BaseQuickAdapter<MainMenu, BaseViewHolder>(R.layout.item_main_menu) {

    override fun convert(holder: BaseViewHolder, item: MainMenu) {
        holder.setText(R.id.textView, item.name)
    }

}