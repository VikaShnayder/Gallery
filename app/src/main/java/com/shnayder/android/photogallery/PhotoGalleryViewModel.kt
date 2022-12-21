package com.shnayder.android.photogallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.shnayder.android.photogallery.api.FlickrFetchr


class PhotoGalleryViewModel : ViewModel() {
    //свойство для хранения объекта «живых» данных, содержащего список элементов галереи
    val galleryItemLiveData: LiveData<List<GalleryItem>>

    private val flickrFetchr = FlickrFetchr()
    private val mutableSearchTerm = MutableLiveData<String>()


    init {
        mutableSearchTerm.value = "planets"
        //вызов функции searchPhotos(" ") для поиска фото на сайте
        //сохраним результат веб-запроса для получения данных фото при первой инициализации ViewModel (запуск приложения)
        galleryItemLiveData = Transformations.switchMap(mutableSearchTerm) { searchTerm ->
            flickrFetchr.searchPhotos(searchTerm)
        }
    }

    fun fetchPhotos(query: String = "") {
        mutableSearchTerm.value = query
    }
}