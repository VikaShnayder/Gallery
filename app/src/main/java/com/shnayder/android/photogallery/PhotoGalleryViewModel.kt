package com.shnayder.android.photogallery

import android.app.Application
import androidx.lifecycle.*
import com.shnayder.android.photogallery.api.FlickrFetchr

class PhotoGalleryViewModel(private val app: Application) : AndroidViewModel(app) {
    //свойство для хранения объекта «живых» данных, содержащего список элементов галереи
    val galleryItemLiveData: LiveData<List<GalleryItem>>

    private val flickrFetchr = FlickrFetchr()
    private val mutableSearchTerm = MutableLiveData<String>()

    //Открытие поисковой фразы
    val searchTerm: String get() = mutableSearchTerm.value ?: ""


    init {
        mutableSearchTerm.value = QueryPreferences.getStoredQuery(app)

        //вызов функции searchPhotos(" ") для поиска фото на сайте
        //сохраним результат веб-запроса для получения данных фото при первой инициализации ViewModel (запуск приложения)
        galleryItemLiveData = Transformations.switchMap(mutableSearchTerm) { searchTerm ->
            if (searchTerm.isBlank()) {
                flickrFetchr.fetchPhotos()
            } else {
                flickrFetchr.searchPhotos(searchTerm)
            }
        }
    }

    fun fetchPhotos(query: String = "") {
        QueryPreferences.setStoredQuery(app, query)
        mutableSearchTerm.value = query
    }
}