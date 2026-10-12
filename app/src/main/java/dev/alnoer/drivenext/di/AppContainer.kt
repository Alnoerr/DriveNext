package dev.alnoer.drivenext.di

import android.content.Context
import dev.alnoer.drivenext.data.local.AppPreferences
import dev.alnoer.drivenext.data.auth.AuthRepository
import dev.alnoer.drivenext.data.auth.GoogleAuth
import dev.alnoer.drivenext.network.NetworkMonitor

class AppContainer(context: Context) {
    private val applicationContext = context.applicationContext

    val appPreferences: AppPreferences by lazy {
        AppPreferences(applicationContext)
    }

    val networkMonitor: NetworkMonitor by lazy {
        NetworkMonitor(applicationContext)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(applicationContext)
    }

    val googleAuth: GoogleAuth by lazy {
        GoogleAuth(applicationContext)
    }
}
