package com.shnayder.android.photogallery.api

import com.google.gson.annotations.SerializedName
import com.shnayder.android.photogallery.GalleryItem

//для сопоставления с объектом "photos" JSON-данных
class PhotoResponse {
    //galleryItems для хранения списка галерейных объектов и примечаний к нему с помощью @SerializedName("photo")
    //Gson автоматически создаст список и заполнит его объектами элементов галереи на основе JSON-массива "photo"
    @SerializedName("photo")
    lateinit var galleryItems: List<GalleryItem>
}