package com.example.timemanagementapp

import android.app.Application
import com.example.timemanagementapp.data.AppContainer
import com.example.timemanagementapp.data.AppDataContainer

class TimelyApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}