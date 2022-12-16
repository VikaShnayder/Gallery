package com.shnayder.android.photogallery.api

import retrofit2.Call
import retrofit2.http.GET

//интерфейс использует аннотации Retrofit для определения вызовов API.
interface FlickrApi {
    //GET-запрос, отправляется на главную страницу, настраивает Call, возвращаемый функцией fetchContents(), на выполнение GET-запроса
    @GET("/")
    //выполнении вызова генерируется один соответствующий веб-отклик
    //ответ будет получен в виде строки
    fun fetchContents(): Call<String>
}