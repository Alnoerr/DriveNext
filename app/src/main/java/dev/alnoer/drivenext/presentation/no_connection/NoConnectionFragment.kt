package dev.alnoer.drivenext.presentation.no_connection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.alnoer.drivenext.R
import dev.alnoer.drivenext.databinding.FragmentNoConnectionBinding
import dev.alnoer.drivenext.extensions.appContainer

class NoConnectionFragment : Fragment() {
    private var _binding: FragmentNoConnectionBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoConnectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.retryButton.setOnClickListener {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
