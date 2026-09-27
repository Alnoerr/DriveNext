package dev.alnoer.drivenext.presentation.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dev.alnoer.drivenext.R
import dev.alnoer.drivenext.extensions.appContainer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class SplashFragment : Fragment(R.layout.fragment_splash) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            delay(2.seconds)

            if (!appContainer.appPreferences.isOnboardingCompleted) {
                findNavController().navigate(R.id.splash_to_onboarding)
            } else if (appContainer.networkMonitor.hasInternet) {
                findNavController().navigate(R.id.splash_to_auth)
            } else {
                findNavController().navigate(R.id.splash_to_no_connection)
            }
        }
    }
}
