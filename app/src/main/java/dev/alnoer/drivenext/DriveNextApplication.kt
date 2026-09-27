package dev.alnoer.drivenext

import android.app.Application
import dev.alnoer.drivenext.di.AppContainer

class DriveNextApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
