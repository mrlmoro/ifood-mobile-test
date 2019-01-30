package br.com.murilomoro.tweetlikes.data.local

import android.content.SharedPreferences
import com.google.gson.Gson

/**
 * Created by Murilo Moro on 26/01/19.
 */
interface Preferences {
    fun getString(key: String): String

    fun putString(key: String, value: String)

    fun <T> getData(key: String, clazz: Class<T>): T?

    fun <T> putData(key: String, data: T)

    fun contains(key: String): Boolean
}

class PreferencesImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : Preferences {

    override fun getString(key: String): String = sharedPreferences.getString(key, "") ?: ""

    override fun putString(key: String, value: String) {
        sharedPreferences.edit()
            .putString(key, value)
            .apply()
    }

    override fun <T> getData(key: String, clazz: Class<T>): T? {
        val json = getString(key)
        return try {
            gson.fromJson(json, clazz)
        } catch (ex: Exception) {
            null
        }
    }

    override fun <T> putData(key: String, data: T) {
        val json = gson.toJson(data)
        putString(key, json)
    }

    override fun contains(key: String): Boolean = sharedPreferences.contains(key)

}