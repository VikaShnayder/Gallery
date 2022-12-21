package com.shnayder.android.photogallery.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shnayder.android.photogallery.GalleryItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

private const val TAG = "FlickrFetchr"


//Класс репозитория инкапсулирует логику доступа к данным из одного источника или набора источников.
//Он определяет, как получать и хранить определенный набор данных
class FlickrFetchr {

    //создание экземпляра интерфейса API
    private val flickrApi: FlickrApi

    init {
        //настройка и сборка экземпляра Retrofit
        val retrofit: Retrofit =Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            //конвертер, который заставляет Retrofit десериализовать ответ в строки
            .addConverterFactory(GsonConverterFactory.create())
            //build() - возвращает экземпляр Retrofit
            .build()
        flickrApi = retrofit.create(FlickrApi::class.java)
    }


    //ставит в очередь сетевой запрос и обертывает результат в LiveData
    fun fetchPhotos(): LiveData<List<GalleryItem>> {
        val responseLiveData: MutableLiveData<List<GalleryItem>> = MutableLiveData()

        // генерации объекта retrofit2.Call, представляющего собой исполняемый веб-запрос.
        val flickrRequest: Call<FlickrResponse> = flickrApi.fetchPhotos()

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



}