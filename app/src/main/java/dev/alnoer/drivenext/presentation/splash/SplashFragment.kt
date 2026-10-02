package dev.alnoer.drivenext.presentation.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dev.alnoer.drivenext.R
import dev.alnoer.drivenext.databinding.FragmentSplashBinding
import dev.alnoer.drivenext.extensions.appContainer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            delay(2.seconds)

            if (!appContainer.networkMonitor.hasInternet) {
                findNavController().navigate(R.id.splash_to_no_connection)
            } else if (!appContainer.appPreferences.isOnboardingCompleted) {
                findNavController().navigate(R.id.splash_to_onboarding)
            } else {
                findNavController().navigate(R.id.splash_to_auth)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
