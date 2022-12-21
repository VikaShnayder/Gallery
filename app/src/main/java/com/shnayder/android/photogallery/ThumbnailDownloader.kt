package com.shnayder.android.photogallery

import android.os.HandlerThread
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

private const val TAG = "ThumbnailDownloader"

//загрузка и передача изображений в PhotoGalleryFragment
//фоновый поток. зависит от продолжительности жизни фрагмента
//поочередно обрабатывать запросы на загрузку, а также предоставлять результирующее изображение для каждого отдельного запроса по мере завершения загрузки
//Т - обобщенный параметр, обобщенный тип объекта на входе
class ThumbnailDownloader<in T> : HandlerThread(TAG), LifecycleObserver {
    private var hasQuit = false

    //сообщяет о завершении потока
    override fun quit(): Boolean {
        hasQuit = true
        return super.quit()
    }

    //аннотация @OnLifecycleEvent(Lifecycle.Event), позволяет ассоциировать функцию в вашем классе с обратным вызовом жизненного цикла
    //остановка фонового потока при вызове функции onCreate() владельца жизненного цикла
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun setup() {
        Log.i(TAG, "Starting background thread")
    }

    //остановка фонового потока при вызове функции onDestroy() владельца жизненного цикла
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun tearDown() {
        Log.i(TAG, "Destroying background thread")
    }



    //Т - идентификатор загругки
    //String - url адрес загрузки.
    fun queueThumbnail(target: T, url: String)
    {
        Log.i(TAG, "Got a URL: $url")
    }
}
