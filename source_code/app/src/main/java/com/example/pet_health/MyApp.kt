package com.example.pet_health

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Kiểm tra nếu chưa có FirebaseApp, thì initialize
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
            Log.d("FirebaseInit", "FirebaseApp initialized")
        } else {
            Log.d("FirebaseInit", "FirebaseApp already initialized")
        }
    }
}