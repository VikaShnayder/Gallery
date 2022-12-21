package com.shnayder.android.photogallery.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
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
            .baseUrl("https://www.flickr.com/")
            //конвертер, который заставляет Retrofit десериализовать ответ в строки
            .addConverterFactory(ScalarsConverterFactory.create())
            //build() - возвращает экземпляр Retrofit
            .build()
        flickrApi = retrofit.create(FlickrApi::class.java)
    }


    //ставит в очередь сетевой запрос и обертывает результат в LiveData
    fun fetchContents(): LiveData<String> {
        val responseLiveData: MutableLiveData<String> = MutableLiveData()

        // генерации объекта retrofit2.Call, представляющего собой исполняемый веб-запрос.
        val flickrRequest: Call<String> = flickrApi.fetchContents()

        //выполнение веб-запроса
        //enqueue(...) выполняет веб-запрос, находящийся в объекте Call в фоновом потоке
        flickrRequest.enqueue(object : Callback<String> {
            //вызывается если ответа от сервера нет
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e(TAG, "Failed to fetchphotos", t)
            }

            //вызывается если ответ от сервера получен
            override fun onResponse(call: Call<String>,response: Response<String>) {
                Log.d(TAG, "Response received")
                responseLiveData.value = response.body()
            }
        })
        return responseLiveData
    }



}