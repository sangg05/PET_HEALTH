package com.example.pet_health.repository


import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    var isRememberMe: Boolean
        get() = prefs.getBoolean("remember_me", false)
        set(value) = prefs.edit().putBoolean("remember_me", value).apply()

    var savedEmail: String?
        get() = prefs.getString("saved_email", "")
        set(value) = prefs.edit().putString("saved_email", value).apply()

    var savedPassword: String?
        get() = prefs.getString("saved_password", "")
        set(value) = prefs.edit().putString("saved_password", value).apply()
}
