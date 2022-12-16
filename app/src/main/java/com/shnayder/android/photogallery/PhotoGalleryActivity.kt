package com.shnayder.android.photogallery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class PhotoGalleryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_gallery)

        //способ определения, размещен ли фрагмент: проверка на не null
        //Если пакет не null, это означает, что activity восстанавливается после уничтожения системы (например, после поворота или уничтожения процесса), и все фрагменты, которые были размещены до уничтожения, были восстановлены и добавлены обратно в соответствующие контейнеры
        val isFragmentContainerEmpty = savedInstanceState == null
        if (isFragmentContainerEmpty) {
            supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, PhotoGalleryFragment.newInstance()).commit()
        }
    }
}


