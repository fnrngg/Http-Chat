package com.example.httpchat.preferences
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.example.httpchat.application.MyApplication

object PreferencesManager {

    private const val HTTP_CHAT_USERNAME = "HTTP_CHAT_USERNAME"

    private val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(MyApplication.instance)
    }

    fun saveUser(user: String) {
        preferences.edit().apply {
            putString(HTTP_CHAT_USERNAME, user)
        }.apply()
    }

    fun getUser(): String {
        return preferences.getString(HTTP_CHAT_USERNAME, "").toString()
    }
}