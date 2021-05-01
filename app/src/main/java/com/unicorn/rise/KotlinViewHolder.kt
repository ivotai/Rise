package com.unicorn.rise

import android.view.View
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import kotlinx.android.extensions.LayoutContainer

class KotlinViewHolder(view: View) : BaseViewHolder(view = view), LayoutContainer {

    override val containerView: View = view

}