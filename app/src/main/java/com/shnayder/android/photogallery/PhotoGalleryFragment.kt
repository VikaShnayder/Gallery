package com.shnayder.android.photogallery

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
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
    private lateinit var photoGalleryViewModel: PhotoGalleryViewModel
    private lateinit var photoRecyclerView: RecyclerView

    //Использование объекта Retrofit для создания экземпляра API (реализации интерфейса FlickrApi)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        photoGalleryViewModel = ViewModelProviders.of(this).get(PhotoGalleryViewModel::class.java)

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

    //Наблюдение за LiveData «живыми» данными ViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photoGalleryViewModel.galleryItemLiveData.observe(
            viewLifecycleOwner,
            Observer { galleryItems ->
                //наблюдения за доступностью и изменением данных
                photoRecyclerView.adapter = PhotoAdapter(galleryItems)
            })
    }



//    вывод заголовка изображения
//    private class PhotoHolder(itemTextView: TextView) : RecyclerView.ViewHolder(itemTextView)
//    {
//        val bindTitle: (CharSequence) -> Unit = itemTextView::setText
//    }

    //RecyclerView.Adapter выдает PhotoHolder из галереи
//    private class PhotoAdapter(private val galleryItems: List<GalleryItem>) : RecyclerView.Adapter<PhotoHolder>() {
//        override fun onCreateViewHolder( parent: ViewGroup, viewType: Int ): PhotoHolder {
//            val textView = TextView(parent.context)
//            return PhotoHolder(textView)
//        }
//        override fun getItemCount(): Int = galleryItems.size
//        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
//            val galleryItem = galleryItems[position]
//            holder.bindTitle(galleryItem.id)
//        }
//    }



    private class PhotoHolder(private val itemImageView: ImageView) : RecyclerView.ViewHolder(itemImageView) {
        val bindDrawable: (Drawable) -> Unit = itemImageView::setImageDrawable
    }

    //RecyclerView.Adapter выдает PhotoHolder из галереи
    private inner class PhotoAdapter(private val galleryItems: List<GalleryItem>) : RecyclerView.Adapter<PhotoHolder>() {
        override fun onCreateViewHolder( parent: ViewGroup, viewType: Int ): PhotoHolder {
            val view = layoutInflater.inflate(
                R.layout.list_item_gallery,
                parent,
                false
            ) as ImageView
            return PhotoHolder(view)
        }
        override fun getItemCount(): Int = galleryItems.size
        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            val galleryItem = galleryItems[position]
            val placeholder: Drawable = ContextCompat.getDrawable(
                    requireContext(),
                //временное изображение до завершения загрузки
                    R.drawable.bill_up_close
                ) ?: ColorDrawable()
            holder.bindDrawable(placeholder)

        }
    }


    companion object {
        fun newInstance() = PhotoGalleryFragment()
    }
}
