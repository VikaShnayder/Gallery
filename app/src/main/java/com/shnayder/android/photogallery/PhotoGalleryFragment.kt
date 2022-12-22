package com.shnayder.android.photogallery

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
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
    //создание экземпляра фонового потока
    private lateinit var thumbnailDownloader: ThumbnailDownloader<PhotoHolder>

    //Использование объекта Retrofit для создания экземпляра API (реализации интерфейса FlickrApi)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //сохранение фрагмента
        retainInstance = true
        //чтобы зарегистрировать фрагмент для получения обратных вызовов меню
        setHasOptionsMenu(true)


        photoGalleryViewModel = ViewModelProviders.of(this).get(PhotoGalleryViewModel::class.java)

        //класс thumbnailDownloader получает вызовов о создании жизненного цикла PhotoGalleryFragment
        val responseHandler = Handler()
        thumbnailDownloader = ThumbnailDownloader(responseHandler) {
                photoHolder, bitmap -> val drawable = BitmapDrawable(resources, bitmap)
                photoHolder.bindDrawable(drawable)
        }

        lifecycle.addObserver(thumbnailDownloader.fragmentLifecycleObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewLifecycleOwner.lifecycle.addObserver(
            thumbnailDownloader.viewLifecycleObserver
        )
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


    override fun onDestroyView() {
        super.onDestroyView()
        viewLifecycleOwner.lifecycle.removeObserver(
            thumbnailDownloader.viewLifecycleObserver
        )
    }
    //класс thumbnailDownloader получает вызовов об уничтожении жизненного цикла PhotoGalleryFragment
    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(
            thumbnailDownloader.fragmentLifecycleObserver
        )
    }

    //Элементы, перечисленные в разметке меню, добавляются на панель приложения
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_photo_gallery, menu)

        val searchItem: MenuItem = menu.findItem(R.id.menu_item_search)

        val searchView = searchItem.actionView as SearchView

        searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                //выполняется когда пользователь отправляет запрос
                override fun onQueryTextSubmit(queryText: String): Boolean {
                    Log.d(TAG, "QueryTextSubmit: $queryText")
                    photoGalleryViewModel.fetchPhotos(queryText)
                    return true
                }

                override fun onQueryTextChange(queryText: String): Boolean {
                    Log.d(TAG, "QueryTextChange: $queryText")
                    return false
                }
            })

            //Предварительное заполнение SearchView
            setOnSearchClickListener {
                searchView.setQuery(photoGalleryViewModel.searchTerm, false)
            }
        }
    }

    // Очистка сохраненного запроса (когда пользователь выберет элемент «Clear Search» в меню)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_clear -> {
                photoGalleryViewModel.fetchPhotos("")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

            //вызов функции потока, передаем папку PhotoHolder где разместим фотографии и URL-адрес GalleryItem для скачивания
            thumbnailDownloader.queueThumbnail(holder, galleryItem.url)
        }
    }


    companion object {
        fun newInstance() = PhotoGalleryFragment()
    }
}
