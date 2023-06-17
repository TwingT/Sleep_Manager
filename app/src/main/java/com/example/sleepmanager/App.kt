package com.example.sleepmanager

import android.app.Application
import com.example.sleepmanager.data.MySharedPreferences

class App : Application() {
    companion object {
        lateinit var prefs : MySharedPreferences
    }

    override fun onCreate() {
        prefs = MySharedPreferences(applicationContext)
        super.onCreate()
    }
}