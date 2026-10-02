package dev.alnoer.drivenext.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import dev.alnoer.drivenext.R
import dev.alnoer.drivenext.databinding.FragmentOnboardingBinding
import dev.alnoer.drivenext.extensions.appContainer

class OnboardingFragment : Fragment() {
    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            updateButtons(position)
            updateIndicators(position)
        }
    }

    private val pages = listOf(
        OnboardingPage(
            imageRes = R.drawable.onboarding_1,
            titleRes = R.string.onboarding_title_1,
            descriptionRes = R.string.onboarding_description_1,
            buttonNextTextRes = R.string.onboarding_next
        ),
        OnboardingPage(
            imageRes = R.drawable.onboarding_2,
            titleRes = R.string.onboarding_title_2,
            descriptionRes = R.string.onboarding_description_2,
            buttonNextTextRes = R.string.onboarding_next
        ),
        OnboardingPage(
            imageRes = R.drawable.onboarding_3,
            titleRes = R.string.onboarding_title_3,
            descriptionRes = R.string.onboarding_description_3,
            buttonNextTextRes = R.string.onboarding_finish
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = OnboardingAdapter(this, pages)

        setupListeners()
    }

    private fun setupListeners() {
        binding.buttonSkip.setOnClickListener {
            finishOnboarding()
        }

        binding.buttonNext.setOnClickListener {
            val currentPage = binding.viewPager.currentItem

            if (currentPage < pages.lastIndex) {
                binding.viewPager.setCurrentItem(
                    currentPage + 1,
                    true
                )
            } else {
                finishOnboarding()
            }
        }

        binding.viewPager.registerOnPageChangeCallback(pageChangeCallback)
        updateButtons(binding.viewPager.currentItem)
        updateIndicators(binding.viewPager.currentItem)
    }

    override fun onDestroyView() {
        binding.viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
        binding.viewPager.adapter = null
        super.onDestroyView()
        _binding = null
    }

    private fun finishOnboarding() {
        appContainer.appPreferences.isOnboardingCompleted = true
        if (appContainer.networkMonitor.hasInternet) {
            findNavController().navigate(R.id.onboarding_to_auth)
        } else {
            findNavController().navigate(R.id.onboarding_to_no_connection)
        }
    }

    private fun updateButtons(position: Int) {
        val isLastPage = position == pages.lastIndex

        binding.buttonNext.setText(pages[position].buttonNextTextRes)

        binding.buttonSkip.isInvisible = isLastPage
    }

    private fun updateIndicators(position: Int) {
        val dots = listOf<ImageView>(
            binding.dot1,
            binding.dot2,
            binding.dot3
        )

        dots.forEachIndexed { index, view ->
            view.setImageResource(
                if (index == position) R.drawable.bg_dot_active else R.drawable.bg_dot_inactive
            )
        }
    }
}
