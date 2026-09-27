package dev.alnoer.drivenext.presentation.no_connection

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.alnoer.drivenext.R
import dev.alnoer.drivenext.extensions.appContainer

class NoConnectionFragment : Fragment(R.layout.fragment_no_connection) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val retryButton = requireView().findViewById<View>(R.id.retry_button)

        retryButton.setOnClickListener {
            if (appContainer.networkMonitor.hasInternet) {
                val destination = if (appContainer.appPreferences.isOnboardingCompleted) {
                    R.id.no_connection_to_auth
                } else {
                    R.id.no_connection_to_onboarding
                }
                findNavController().navigate(destination)
            }
        }
    }
}
