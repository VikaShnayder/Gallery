package com.shnayder.android.photogallery

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.shnayder.android.photogallery.api.FlickrFetchr
import java.util.concurrent.ConcurrentHashMap

private const val TAG = "ThumbnailDownloader"

//идентификации сообщений как запросов на загрузку.
private const val MESSAGE_DOWNLOAD = 0

//загрузка и передача изображений в PhotoGalleryFragment
//фоновый поток. зависит от продолжительности жизни фрагмента
//поочередно обрабатывать запросы на загрузку, а также предоставлять результирующее изображение для каждого отдельного запроса по мере завершения загрузки
//Т - обобщенный параметр, обобщенный тип объекта на входе
class ThumbnailDownloader<in T>
    //хранения экземпляра Handler, переданного из главного потока.
    (private val responseHandler: Handler,
     private val onThumbnailDownloaded: (T, Bitmap) -> Unit)
    : HandlerThread(TAG), LifecycleObserver {


    private var hasQuit = false

    //храниться ссылка на объект Handler, отвечающий за постановку в очередь запросов на загрузку в фоновом потоке ThumbnailDownloader
    private lateinit var requestHandler: Handler
    //хранить и загружать URL-адрес, связанный с конкретным запросом
    private val requestMap = ConcurrentHashMap<T, String>()
    //ссылка на экземпляр FlickrFetchr
    private val flickrFetchr = FlickrFetchr()

    //обработка сообщений в looper
    //Аннотация @Suppress("UNCHECKED_CAST") при проверке сообщает Lint, что вы приводите msg.obj к типу T без предварительной проверки того, относится ли msg.obj к этому типу на самом деле.
    @Suppress("UNCHECKED_CAST")
    @SuppressLint("HandlerLeak")
    override fun onLooperPrepared() {
        requestHandler = object : Handler() {
            //проверяем тип сообщения, читаем значение obj
            //вызываться, когда сообщение загрузки извлечено из очереди и готово к обработке
            override fun handleMessage(msg: Message) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    val target = msg.obj as T
                    Log.i(TAG, "Got a request for URL: ${requestMap[target]}")
                    handleRequest(target)
                }
            }
        }
    }


    //сообщяет о завершении потока
    override fun quit(): Boolean {
        hasQuit = true
        return super.quit()
    }

    //аннотация @OnLifecycleEvent(Lifecycle.Event), позволяет ассоциировать функцию в вашем классе с обратным вызовом жизненного цикла
    //запуск фонового потока при вызове функции onCreate() владельца жизненного цикла
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun setup() {
        Log.i(TAG, "Starting background thread")
        start()
        //очередь сообщений
        looper
    }

    //остановка фонового потока при вызове функции onDestroy() владельца жизненного цикла
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun tearDown() {
        Log.i(TAG, "Destroying background thread")
        quit()
    }

    //Т - идентификатор загругки
    //String - url адрес загрузки.
    fun queueThumbnail(target: T, url: String)
    {
        Log.i(TAG, "Got a URL: $url")

        //обновления requestMap и постановки нового сообщения в очередь сообщений фонового потока
        requestMap[target] = url
        //отвечать за обработку сообщения при его извлечении из очереди сообщений
        //what - MESSAGE_DOWNLOAD
        //obj - Ttarget (т.е. PhotoHolder переданное функцией queueThumbnail() )
        requestHandler.obtainMessage(MESSAGE_DOWNLOAD, target) .sendToTarget()
    }

    //выгрузка сообщений
    private fun handleRequest(target: T) {
        val url = requestMap[target] ?: return
        val bitmap = flickrFetchr.fetchPhoto(url) ?: return

        //Загрузка и вывод изображений
        responseHandler.post(Runnable {
            if (requestMap[target] != url || hasQuit) {
                return@Runnable
            }
            //удаляем из requestMap связь «PhotoHolder—URL»
            requestMap.remove(target)
            // назначаем изображение для PhotoHolder
            onThumbnailDownloaded(target, bitmap)
        })
    }
}
