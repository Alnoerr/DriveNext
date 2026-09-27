package dev.alnoer.drivenext.presentation.onboarding

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import dev.alnoer.drivenext.R
import dev.alnoer.drivenext.extensions.appContainer

class OnboardingFragment : Fragment(R.layout.fragment_onboarding) {
    private lateinit var viewPager: ViewPager2
    private lateinit var buttonNext: Button
    private lateinit var buttonSkip: TextView
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

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.viewPager)
        buttonNext = view.findViewById(R.id.buttonNext)
        buttonSkip = view.findViewById(R.id.buttonSkip)

        viewPager.adapter = OnboardingAdapter(this, pages)

        setupListeners()
    }

    private fun setupListeners() {
        buttonSkip.setOnClickListener {
            finishOnboarding()
        }

        buttonNext.setOnClickListener {
            val currentPage = viewPager.currentItem

            if (currentPage < pages.lastIndex) {
                viewPager.setCurrentItem(
                    currentPage + 1,
                    true
                )
            } else {
                finishOnboarding()
            }
        }

        viewPager.registerOnPageChangeCallback(pageChangeCallback)
        updateButtons(viewPager.currentItem)
        updateIndicators(viewPager.currentItem)
    }

    override fun onDestroyView() {
        viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
        viewPager.adapter = null
        super.onDestroyView()
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

        buttonNext.setText(pages[position].buttonNextTextRes)

        buttonSkip.isInvisible = isLastPage
    }

    private fun updateIndicators(position: Int) {
        val dots = listOf<ImageView>(
            requireView().findViewById(R.id.dot1),
            requireView().findViewById(R.id.dot2),
            requireView().findViewById(R.id.dot3)
        )

        dots.forEachIndexed { index, view ->
            view.setImageResource(
                if (index == position) R.drawable.bg_dot_active else R.drawable.bg_dot_inactive
            )
        }
    }
}
