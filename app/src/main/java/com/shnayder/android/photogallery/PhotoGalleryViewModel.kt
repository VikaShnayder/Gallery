package com.shnayder.android.photogallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.shnayder.android.photogallery.api.FlickrFetchr


class PhotoGalleryViewModel : ViewModel() {
    //свойство для хранения объекта «живых» данных, содержащего список элементов галереи
    val galleryItemLiveData: LiveData<List<GalleryItem>>
    init {
        //вызов функции fetchPhotos для запроса «получить недавние интересные фотографии»
        //сохраним результат веб-запроса для получения данных фото при первой инициализации ViewModel (запуск приложения)
        galleryItemLiveData = FlickrFetchr().fetchPhotos()
    }
}