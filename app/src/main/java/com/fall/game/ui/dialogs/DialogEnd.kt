package com.fall.game.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fall.game.R
import com.fall.game.core.library.ViewBindingDialog
import com.fall.game.databinding.DialogEndBinding
import com.fall.game.domain.SP
import com.fall.game.ui.one.FragmentOneDirections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class DialogEnd : ViewBindingDialog<DialogEndBinding>(DialogEndBinding::inflate) {
    private val args: DialogEndArgs by navArgs()
    private val sp by lazy {
        SP(requireContext())
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Dialog_No_Border)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.setCancelable(false)
        dialog!!.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                findNavController().popBackStack(R.id.fragmentMain, false, false)
                true
            } else {
                false
            }
        }
        binding.scores.text = args.scores.toString()

        lifecycleScope.launch(Dispatchers.Default) {

            repeat(20) {
                delay(20)
                sp.setLives(0)
                sp.setScores(0)
            }
        }

        binding.playAgain.setOnClickListener {
            findNavController().popBackStack(R.id.fragmentMain, false, false)
            findNavController().navigate(
                FragmentOneDirections.actionFragmentMainToFragmentFalling(
                    true
                )
            )
        }

        binding.close.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) {
                sp.setLives(0)
                sp.setScores(0)

                delay(20)
                sp.setLives(0)
                sp.setScores(0)

                delay(20)
                sp.setLives(0)
                sp.setScores(0)

                delay(20)
            }
            findNavController().popBackStack(R.id.fragmentMain, false, false)
        }

        binding.starLayout.removeAllViews()
        val stars = when (args.scores) {
            in 0..300 -> 1
            in 301..600 -> 2
            in 601..1000 -> 3
            else -> 4
        }

        repeat(stars) {
            val imgView = ImageView(requireContext())
            imgView.layoutParams = LinearLayout.LayoutParams(dpToPx(55), dpToPx(55))
            imgView.setImageResource(R.drawable.star)
            binding.starLayout.addView(imgView)
        }
    }

    private fun dpToPx(dp: Int): Int {
        val displayMetrics = resources.displayMetrics
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }
}