package dev.alnoer.drivenext.presentation.onboarding

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingAdapter(
    fragment: Fragment,
    private val pages: List<OnboardingPage>
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return pages.size
    }

    override fun createFragment(position: Int): Fragment {
        return OnboardingPageFragment.newInstance(pages[position])
    }
}
