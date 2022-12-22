package com.shnayder.android.photogallery

import com.google.gson.annotations.SerializedName


//метаинформация одной фотографии: название, идентификатор и URL-адрес для загрузки изображения
data class GalleryItem(
    var ispublic: String = "",
    var id: String = "",
    //@SerializedName(" ") используем чтобы точно показать какое поле в json берем
    @SerializedName("url_s") var url: String = ""
)
