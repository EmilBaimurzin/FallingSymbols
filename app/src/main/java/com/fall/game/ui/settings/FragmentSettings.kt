package com.fall.game.ui.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fall.game.R
import com.fall.game.databinding.FragmentSettingsBinding
import com.fall.game.domain.SP
import com.fall.game.ui.other.VMCallback
import com.fall.game.ui.other.ViewBindingFragment

class FragmentSettings: ViewBindingFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {
    private val sp by lazy { SP(requireContext()) }
    private val callbackViewModel: VMCallback by activityViewModels()
    private val viewModel: SettingsViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.difficulty.value!! == 0) {
            viewModel.setDifficulty(sp.getDifficulty())
        }

        binding.easy.setOnClickListener {
            viewModel.setDifficulty(1)
        }

        binding.medium.setOnClickListener {
            viewModel.setDifficulty(2)
        }

        binding.hard.setOnClickListener {
            viewModel.setDifficulty(3)
        }

        binding.save.setOnClickListener {
            sp.setDifficulty(viewModel.difficulty.value!!)
            callbackViewModel.settingsCallback?.invoke()
            findNavController().popBackStack()
        }

        viewModel.difficulty.observe(viewLifecycleOwner) {
            when (it) {
                1 -> {
                    binding.easy.setImageResource(R.drawable.easy)
                    binding.medium.setImageResource(R.drawable.medium02)
                    binding.hard.setImageResource(R.drawable.hard02)
                }
                2 -> {
                    binding.easy.setImageResource(R.drawable.easy02)
                    binding.medium.setImageResource(R.drawable.medium)
                    binding.hard.setImageResource(R.drawable.hard02)
                }
                else -> {
                    binding.easy.setImageResource(R.drawable.easy02)
                    binding.medium.setImageResource(R.drawable.medium02)
                    binding.hard.setImageResource(R.drawable.hard)
                }
            }
        }
    }
}