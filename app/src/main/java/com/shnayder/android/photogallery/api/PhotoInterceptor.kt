package com.shnayder.android.photogallery.api

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

private const val API_KEY = "797a0541816a0691221561183f2c43b7"

class PhotoInterceptor : Interceptor {
    //доступ к запросу
    override fun intercept(chain: Interceptor.Chain): Response {
        //chain.request() для доступа к исходному запросу.
        val originalRequest: Request = chain.request()

        //originalRequest.url() извлекает исходный URL из запроса
        //а затем используется HttpUrl.Builder для добавления параметров запроса.
        val newUrl: HttpUrl = originalRequest.url().newBuilder()
                .addQueryParameter("api_key",API_KEY)
                .addQueryParameter("format","json")
                .addQueryParameter("nojsoncallback","1")
                .addQueryParameter("extras","url_s")
                .addQueryParameter("safesearch","1")
                .build()

        //Builder создает новый запрос на основе оригинального запроса и заменяет исходный URL на новый
        val newRequest: Request = originalRequest.newBuilder().url(newUrl).build()
        //функция для создания ответа
        return chain.proceed(newRequest)
    }
}
