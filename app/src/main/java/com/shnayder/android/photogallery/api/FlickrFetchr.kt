package com.shnayder.android.photogallery.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shnayder.android.photogallery.GalleryItem
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

private const val TAG = "FlickrFetchr"


//FlickrFetchr разбирает данные JSON в списке GalleryItem и публикует список  возвращаемому объекту LiveData

//Класс репозитория инкапсулирует логику доступа к данным из одного источника или набора источников.
//Он определяет, как получать и хранить определенный набор данных
class FlickrFetchr {

    //создание экземпляра интерфейса API
    private val flickrApi: FlickrApi

    init {
        //Добавление перехватчика PhotoInterceptor() в конфигурацию Retrofit
        val client = OkHttpClient.Builder()
            .addInterceptor(PhotoInterceptor())
            .build()

        //настройка и сборка экземпляра Retrofit
        val retrofit: Retrofit =Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            //конвертер, который заставляет Retrofit десериализовать ответ в строки
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            //build() - возвращает экземпляр Retrofit
            .build()
        flickrApi = retrofit.create(FlickrApi::class.java)
    }

    fun fetchPhotos(): LiveData<List<GalleryItem>> {
        return fetchPhotoMetadata(flickrApi.fetchPhotos())
    }

    fun searchPhotos(query: String): LiveData<List<GalleryItem>> {
        return fetchPhotoMetadata(flickrApi.searchPhotos(query))
    }

    //ставит в очередь сетевой запрос и обертывает результат в LiveData
    private fun fetchPhotoMetadata(flickrRequest: Call<FlickrResponse>) : LiveData<List<GalleryItem>> {
        val responseLiveData: MutableLiveData<List<GalleryItem>> = MutableLiveData()

        //выполнение веб-запроса
        //enqueue(...) выполняет веб-запрос, находящийся в объекте Call в фоновом потоке
        flickrRequest.enqueue(object : Callback<FlickrResponse> {
            //вызывается если ответа от сервера нет
            override fun onFailure(call: Call<FlickrResponse>, t: Throwable) {
                Log.e(TAG, "Failed to fetchphotos", t)
            }

            //вызывается если ответ от сервера получен
            //выделить список элементов галереи из ответа и обновить LiveData
            override fun onResponse(call: Call<FlickrResponse>,response: Response<FlickrResponse>) {
                Log.d(TAG, "Response received")

                val flickrResponse: FlickrResponse? = response.body()
                val photoResponse: PhotoResponse? = flickrResponse?.photos
                var galleryItems: List<GalleryItem> = photoResponse?.galleryItems?: mutableListOf()

                //отфильтровывает элементы галереи с пустыми значениями URL-адреса
                galleryItems = galleryItems.filterNot {
                    it.url.isBlank()
                }
                responseLiveData.value = galleryItems
            }
        })
        return responseLiveData
    }


    //загружать данные по заданномуURL-адресу и декодировать их в изображение Bitmap
    //Аннотация @WorkerThread указывает, что эта функция должна вызываться только в фоновом потоке
    @WorkerThread
    fun fetchPhoto(url: String): Bitmap? {
        //execute() синхронно выполняет веб-запрос
        val response: Response<ResponseBody> = flickrApi.fetchUrlBytes(url).execute()
        //создание Bitmap из данных в потоке
        val bitmap = response.body()?.byteStream()?.use(BitmapFactory::decodeStream)
        Log.i(TAG, "Decoded bitmap=$bitmap from Response=$response")
        //возвращаем растровое изображение
        return bitmap
    }

}