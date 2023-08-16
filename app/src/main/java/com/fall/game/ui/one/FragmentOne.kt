package com.fall.game.ui.one

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.fall.game.R
import com.fall.game.databinding.FragmentOneBinding
import com.fall.game.domain.SP
import com.fall.game.ui.other.ViewBindingFragment

class FragmentOne : ViewBindingFragment<FragmentOneBinding>(FragmentOneBinding::inflate) {
    private val sp by lazy {
        SP(requireContext())
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.exit.setOnClickListener {
            requireActivity().finish()
        }
        binding.newGame.setOnClickListener {
            findNavController().navigate(FragmentOneDirections.actionFragmentMainToFragmentFalling(true))
        }

        binding.coontinue.setOnClickListener {
            findNavController().navigate(FragmentOneDirections.actionFragmentMainToFragmentFalling(false))
        }

        binding.privacyText.setOnClickListener {
            requireActivity().startActivity(
                Intent(
                    ACTION_VIEW,
                    Uri.parse("https://www.google.com")
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (sp.getScores() == 0) {
            binding.coontinue.setImageResource(R.drawable.continue02)
            binding.coontinue.isEnabled = false
        } else {
            binding.coontinue.setImageResource(R.drawable._continue)
            binding.coontinue.isEnabled = true
        }
    }
}