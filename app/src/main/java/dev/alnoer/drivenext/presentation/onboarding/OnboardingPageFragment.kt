package dev.alnoer.drivenext.presentation.onboarding

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import dev.alnoer.drivenext.R

class OnboardingPageFragment : Fragment(R.layout.fragment_onboarding_page) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val image = view.findViewById<ImageView>(R.id.onboarding_image)
        val title = view.findViewById<TextView>(R.id.onboarding_title)
        val description = view.findViewById<TextView>(R.id.onboarding_description)

        image.setImageResource(requireArguments().getInt(ARG_IMAGE))
        title.setText(requireArguments().getInt(ARG_TITLE))
        description.setText(requireArguments().getInt(ARG_DESCRIPTION))
    }

    companion object {
        private const val ARG_IMAGE = "imageRes"
        private const val ARG_TITLE = "titleRes"
        private const val ARG_DESCRIPTION = "descriptionRes"

        fun newInstance(page: OnboardingPage): OnboardingPageFragment =
            OnboardingPageFragment().apply {
                arguments = bundleOf(
                    ARG_IMAGE to page.imageRes,
                    ARG_TITLE to page.titleRes,
                    ARG_DESCRIPTION to page.descriptionRes
                )
            }
    }
}
