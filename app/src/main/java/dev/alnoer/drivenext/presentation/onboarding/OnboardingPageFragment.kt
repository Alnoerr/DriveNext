package dev.alnoer.drivenext.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.alnoer.drivenext.databinding.FragmentOnboardingPageBinding

class OnboardingPageFragment : Fragment() {
    private var _binding: FragmentOnboardingPageBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.onboardingImage.setImageResource(requireArguments().getInt(ARG_IMAGE))
        binding.onboardingTitle.setText(requireArguments().getInt(ARG_TITLE))
        binding.onboardingDescription.setText(requireArguments().getInt(ARG_DESCRIPTION))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_IMAGE = "imageRes"
        private const val ARG_TITLE = "titleRes"
        private const val ARG_DESCRIPTION = "descriptionRes"

        fun newInstance(page: OnboardingPage): OnboardingPageFragment =
            OnboardingPageFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_IMAGE, page.imageRes)
                    putInt(ARG_TITLE, page.titleRes)
                    putInt(ARG_DESCRIPTION, page.descriptionRes)
                }
            }
    }
}
