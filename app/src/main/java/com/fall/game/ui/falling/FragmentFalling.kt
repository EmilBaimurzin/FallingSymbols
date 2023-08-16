package com.fall.game.ui.falling

import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fall.game.R
import com.fall.game.core.library.l
import com.fall.game.databinding.FragmentFallingBinding
import com.fall.game.domain.SP
import com.fall.game.ui.other.VMCallback
import com.fall.game.ui.other.ViewBindingFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class FragmentFalling :
    ViewBindingFragment<FragmentFallingBinding>(FragmentFallingBinding::inflate) {
    private val args: FragmentFallingArgs by navArgs()
    private val callbackViewModel: VMCallback by activityViewModels()
    private val sp by lazy { SP(requireContext()) }
    private val viewModel: FallingViewModel by viewModels()
    private var moveScope = CoroutineScope(Dispatchers.Default)
    private val xy by lazy {
        val display = requireActivity().windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        Pair(size.x, size.y)
    }
    private var symbolSize = 100
    private var generationTime = 3000L
    private var fallDelay = 12L
    private var distance = 5

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.livesCallBack = {
            lifecycleScope.launch {
                delay(40)
                sp.setLives(it)

                delay(40)
                sp.setLives(it)
                delay(40)
                sp.setLives(it)
            }
        }

        viewModel.scoresCallback = {
            lifecycleScope.launch {
                delay(40)
                l("yes")
                sp.setScores(viewModel.scores.value!!)
                sp.setLives(viewModel.lives.value!!)

                delay(40)
                sp.setScores(viewModel.scores.value!!)
                sp.setLives(viewModel.lives.value!!)

                delay(40)
                sp.setScores(viewModel.scores.value!!)
                sp.setLives(viewModel.lives.value!!)
            }
        }

        if (args.isNew && savedInstanceState == null) {
            sp.setScores(0)
            sp.setLives(0)
        }

        when (sp.getDifficulty()) {
            1 -> {
                symbolSize = 100
                generationTime = 4000L
                fallDelay = 12L
                distance = 5
            }

            2 -> {
                symbolSize = 70
                generationTime = 2000L
                fallDelay = 10L
                distance = 8
            }

            else -> {
                symbolSize = 50
                generationTime = 1000L
                fallDelay = 8L
                distance = 10
            }
        }

        if (savedInstanceState == null && sp.getScores() != 0 && !args.isNew) {
            viewModel.setParams(sp.getScores(), sp.getLives())
        }

        callbackViewModel.settingsCallback = {
            viewModel.pauseState = false
        }

        binding.home.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.settings.setOnClickListener {
            viewModel.stop()
            viewModel.pauseState = true
            findNavController().navigate(R.id.action_fragmentFalling_to_fragmentSettings)
        }

        viewModel.playerXY.observe(viewLifecycleOwner) {
            binding.player.apply {
                x = it.x
                y = it.y
            }
        }

        viewModel.scores.observe(viewLifecycleOwner) {
            binding.scores.text = it.toString()
        }

        viewModel.lives.observe(viewLifecycleOwner) {
            binding.livesLayout.removeAllViews()
            repeat(it) {
                val heartView = ImageView(requireContext())
                heartView.layoutParams = LinearLayout.LayoutParams(dpToPx(25), dpToPx(25)).apply {
                    marginStart = dpToPx(3)
                    marginEnd = dpToPx(3)
                }
                heartView.setImageResource(R.drawable.life)
                binding.livesLayout.addView(heartView)
            }

            if (it == 0 && viewModel.gameState) {
                lifecycleScope.launch(Dispatchers.Main) {
                    viewModel.gameState = false
                    viewModel.stop()
                    sp.setLives(0)
                    sp.setScores(0)
                    findNavController().navigate(
                        FragmentFallingDirections.actionFragmentFallingToDialogEnd(
                            viewModel.scores.value!!
                        )
                    )
                }
            }
        }

        viewModel.symbols.observe(viewLifecycleOwner) {
            binding.symbolsLayout.removeAllViews()
            it.forEach { symbol ->
                val symbolView = ImageView(requireContext())
                symbolView.layoutParams =
                    ViewGroup.LayoutParams(dpToPx(symbolSize), dpToPx(symbolSize))
                val img = when (symbol.value) {
                    1 -> R.drawable.symbol01
                    2 -> R.drawable.symbol02
                    3 -> R.drawable.symbol03
                    4 -> R.drawable.symbol04
                    5 -> R.drawable.symbol05
                    6 -> R.drawable.symbol06
                    7 -> R.drawable.bomb01
                    8 -> R.drawable.bomb02
                    9 -> R.drawable.bomb03
                    else -> R.drawable.bomb04
                }
                symbolView.setImageResource(img)
                symbolView.x = symbol.x
                symbolView.y = symbol.y
                binding.symbolsLayout.addView(symbolView)
            }
        }

        lifecycleScope.launch {
            delay(20)
            if (viewModel.playerXY.value!!.y == 0f) {
                viewModel.initPlayer(xy.first, xy.second, binding.player.width)
            }

            if (viewModel.gameState && !viewModel.pauseState) {
                viewModel.start(
                    dpToPx(symbolSize),
                    xy.first,
                    generationTime,
                    fallDelay,
                    xy.second,
                    binding.player.width,
                    binding.player.height,
                    distance = distance
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setButtons()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setButtons() {
        binding.root.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    moveScope.launch {
                        while (true) {
                            if (motionEvent.x > xy.first / 2) {
                                viewModel.playerMoveRight((xy.first - binding.player.width).toFloat())
                                delay(2)
                            } else {
                                viewModel.playerMoveLeft(0f)
                                delay(2)
                            }
                        }
                    }
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    moveScope.launch {
                        while (true) {
                            if (motionEvent.x > xy.first / 2) {
                                viewModel.playerMoveRight((xy.first - binding.player.width).toFloat())
                                delay(2)
                            } else {
                                viewModel.playerMoveLeft(0f)
                                delay(2)
                            }
                        }
                    }
                    true
                }

                else -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    false
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putShort("d", 1)
    }

    override fun onPause() {
        super.onPause()
        viewModel.stop()
    }

    private fun dpToPx(dp: Int): Int {
        val displayMetrics = resources.displayMetrics
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }
}