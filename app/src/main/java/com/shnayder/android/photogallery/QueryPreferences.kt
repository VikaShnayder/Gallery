package com.shnayder.android.photogallery

import android.content.Context
import android.preference.PreferenceManager

//ключа для хранения запроса (применяется во всех операциях чтения или записи запроса)
private const val PREF_SEARCH_QUERY = "searchQuery"

//QueryPreferences — это синглтон
//предоставлять удобный интерфейс для чтения/записи запроса в хранилище общих настроек
object QueryPreferences {

    //возвращает значение запроса, хранящееся в общих настройках
    fun getStoredQuery(context: Context): String {
        //возвращает экземпляр с именем по умолчанию и приватными разрешениями
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString(PREF_SEARCH_QUERY, "")!!
    }

    //записывает запрос в хранилище общих настроек для заданного контекста
    fun setStoredQuery(context: Context, query: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(PREF_SEARCH_QUERY, query)
            .apply()

    }
}