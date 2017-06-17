package ru.horse315.firebaseauth

import android.app.Application
import com.google.firebase.FirebaseApp

class AppContext : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
    }
}