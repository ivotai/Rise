package com.unicorn.rise

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.ToastUtils
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.core.Observable
import java.net.ConnectException
import java.util.concurrent.TimeUnit

fun View.safeClicks(): Observable<Unit> = this.clicks()
    .throttleFirst(2, TimeUnit.SECONDS)

//fun RecyclerView.addDefaultItemDecoration(size: Int) {
//    HorizontalDividerItemDecoration.Builder(context)
//        .colorResId(R.color.md_grey_300)
//        .size(size)
//        .build().let { this.addItemDecoration(it) }
//}

fun Fragment.startAct(cls: Class<*>, finishSelf: Boolean = false) {
    startActivity(Intent(context, cls))
    if (finishSelf) activity?.finish()
}

fun Fragment.finish() {
    activity?.finish()
}

fun Context.startAct(cls: Class<*>) = startActivity(Intent(this, cls))

fun TextView.isEmpty(): Boolean = trimText().isEmpty()

fun TextView.trimText() = text.toString().trim()

fun String?.toast() = this.let { ToastUtils.showShort(it) }

fun ViewPager2.removeEdgeEffect() {
    (getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
}


fun Throwable.getPrompt(): String? = when (this) {
    is ConnectException -> "没联网吧？"
    else -> this.message
}

fun Throwable.showPrompt() = getPrompt().toast()
