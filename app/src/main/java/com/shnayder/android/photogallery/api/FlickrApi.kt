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

    @GET(
        "services/rest/?method=flickr.interestingness.getList" +
        "&api_key=797a0541816a0691221561183f2c43b7" +
        "&format=json" +
        // =1 значит убрать из ответа круглые скобки
        "&nojsoncallback=1" +
        //добавить URL-адрес мини-версии изображения, если таковая есть
        "&extras=url_s"
    )
    fun fetchPhotos(): Call<String>
}