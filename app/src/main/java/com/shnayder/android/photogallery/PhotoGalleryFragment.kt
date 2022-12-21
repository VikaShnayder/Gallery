package com.shnayder.android.photogallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shnayder.android.photogallery.api.FlickrApi
import com.shnayder.android.photogallery.api.FlickrFetchr
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

        //вызов функции fetchPhotos для запроса «получить недавние интересные фотографии»
        val flickrLiveData: LiveData<String> = FlickrFetchr().fetchPhotos()
        flickrLiveData.observe(
            this,
            Observer{ responseString ->
                Log.d(TAG, "Response received: $responseString")
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
