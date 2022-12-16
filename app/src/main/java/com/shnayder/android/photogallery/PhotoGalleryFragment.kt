package com.shnayder.android.photogallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shnayder.android.photogallery.api.FlickrApi
import retrofit2.Retrofit


class PhotoGalleryFragment : Fragment() {
    private lateinit var photoRecyclerView: RecyclerView

    //Использование объекта Retrofit для создания экземпляра API (реализации интерфейса FlickrApi)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //настройка и сборка экземпляра Retrofit
        //build() - возвращает экземпляр Retrofit
        val retrofit: Retrofit = Retrofit.Builder().baseUrl("https://www.flickr.com/").build()

        //создание экземпляра интерфейса API
        val flickrApi: FlickrApi = retrofit.create(FlickrApi::class.java)
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
