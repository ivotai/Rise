package com.unicorn.rise

import androidx.lifecycle.LifecycleOwner
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject

object RxBus {

    private val subject: Subject<Any> = PublishSubject.create<Any>().toSerialized()

    fun post(obj: Any) {
        subject.onNext(obj)
    }

    fun <T> registerEvent(lifecycleOwner: LifecycleOwner, clazz: Class<T>, consumer: Consumer<T>) {
        subject.ofType(clazz)
//            .lifeOnMain(lifecycleOwner)
            .subscribe(consumer)
    }

}