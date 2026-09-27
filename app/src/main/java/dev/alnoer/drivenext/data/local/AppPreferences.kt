package dev.alnoer.drivenext.data.local

import android.content.Context
import androidx.core.content.edit

class AppPreferences(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    var isOnboardingCompleted: Boolean
        get() = sharedPreferences.getBoolean(ONBOARDING_COMPLETED, false)
        set(value) {
            sharedPreferences.edit {
                putBoolean(ONBOARDING_COMPLETED, value)
            }
        }

    companion object {
        private const val PREFERENCES_NAME = "app_preferences"
        private const val ONBOARDING_COMPLETED = "is_onboarding_completed"
    }
}