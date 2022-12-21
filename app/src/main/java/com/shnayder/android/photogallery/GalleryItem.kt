package com.shnayder.android.photogallery


//метаинформация одной фотографии: название, идентификатор и URL-адрес для загрузки изображения
data class GalleryItem(
    var title: String = "",
    var id: String = "",
    var url: String = ""
)
