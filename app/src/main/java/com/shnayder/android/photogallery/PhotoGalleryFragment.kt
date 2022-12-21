package com.shnayder.android.photogallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shnayder.android.photogallery.api.FlickrApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

private const val TAG = "PhotoGalleryFragment"


class PhotoGalleryFragment : Fragment() {
    private lateinit var photoRecyclerView: RecyclerView

    //Использование объекта Retrofit для создания экземпляра API (реализации интерфейса FlickrApi)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //настройка и сборка экземпляра Retrofit
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://www.flickr.com/")
            //конвертер, который заставляет Retrofit десериализовать ответ в строки
            .addConverterFactory(ScalarsConverterFactory.create())
            //build() - возвращает экземпляр Retrofit
            .build()

        //создание экземпляра интерфейса API
        val flickrApi: FlickrApi = retrofit.create(FlickrApi::class.java)
        // генерации объекта retrofit2.Call, представляющего собой исполняемый веб-запрос.
        val flickrHomePageRequest: Call<String> = flickrApi.fetchContents()

        //выполнение веб-запроса
        //enqueue(...) выполняет веб-запрос, находящийся в объекте Call в фоновом потоке
        flickrHomePageRequest.enqueue(object : Callback<String> {
            //вызывается если ответа от сервера нет
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e(TAG, "Failed to fetchphotos" , t)
            }
            //вызывается если ответ от сервера получен
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d(TAG, "Response received:${response.body()}")
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_photo_gallery, container, false)
        photoRecyclerView = view.findViewById(R.id.photo_recycler_view)
        photoRecyclerView.layoutManager = GridLayoutManager(context, 3)
        return view
    }
    companion object {
        fun newInstance() = PhotoGalleryFragment()
    }
}
