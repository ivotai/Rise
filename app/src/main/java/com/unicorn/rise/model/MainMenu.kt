package com.unicorn.rise.model

import com.mikepenz.iconics.typeface.IIcon
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome

data class MainMenu(
    val name: String,
    val icon: IIcon = FontAwesome.Icon.faw_user
) {
    companion object {
        val all: MutableList<MainMenu>
            get() {
                return mutableListOf(
                    MainMenu(name = "远程接谈", icon = FontAwesome.Icon.faw_users),
                    MainMenu(name = "信息互动", icon = FontAwesome.Icon.faw_comment_dots),
                    MainMenu(name = "材料须知", icon = FontAwesome.Icon.faw_file_word),
                    MainMenu(name = "进度查询", icon = FontAwesome.Icon.faw_tasks),
                    MainMenu(name = "信访历史", icon = FontAwesome.Icon.faw_clock),
                    MainMenu(name = "回执签收", icon = FontAwesome.Icon.faw_check_square)
                )
            }
    }
}
