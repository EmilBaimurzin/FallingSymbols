package com.fall.game.ui.falling

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fall.game.core.library.GameViewModel
import com.fall.game.core.library.XYIMpl
import com.fall.game.core.library.random
import com.fall.game.domain.Symbol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class FallingViewModel : GameViewModel() {
    private val _symbols = MutableLiveData<List<Symbol>>(emptyList())
    val symbols: LiveData<List<Symbol>> = _symbols

    private val _lives = MutableLiveData(3)
    val lives: LiveData<Int> = _lives

    private val _scores = MutableLiveData(0)
    val scores: LiveData<Int> = _scores

    init {
        _playerXY.postValue(XYIMpl(0f, 0f))
    }

    var livesCallBack: ((Int) -> Unit)? = null
    var scoresCallback: ((Int) -> Unit)? = null

    fun start(
        symbolSize: Int,
        maxX: Int,
        generationTime: Long,
        fallDelay: Long,
        maxY: Int,
        playerWidth: Int,
        playerHeight: Int,
        distance: Int
    ) {
        gameScope = CoroutineScope(Dispatchers.Default)
        generateSymbols(symbolSize, maxX, generationTime)
        letItemsFall(fallDelay, maxY, symbolSize, playerWidth, playerHeight, distance)
    }

    fun setParams(scores: Int, lives: Int) {
        _scores.postValue(scores)
        _lives.postValue(lives)
    }

    private fun generateSymbols(symbolSize: Int, maxX: Int, generationTime: Long) {
        gameScope.launch {
            while (true) {
                delay(generationTime)
                val currentList = _symbols.value!!.toMutableList()
                val value = if (1 random 4 != 1) 1 random 6 else 7 random 10
                val isBomb = value !in 1..6
                currentList.add(
                    Symbol(
                        y = 0f - symbolSize,
                        x = (0 random (maxX - symbolSize)).toFloat(),
                        isBomb,
                        value
                    )
                )
                _symbols.postValue(currentList)
            }
        }
    }

    private fun letItemsFall(
        fallDelay: Long,
        maxY: Int,
        symbolSize: Int,
        playerWidth: Int,
        playerHeight: Int,
        distance: Int
    ) {
        gameScope.launch {
            while (true) {
                delay(fallDelay)
                _symbols.postValue(
                    moveSomethingDown(
                        maxY,
                        symbolSize,
                        symbolSize,
                        playerWidth,
                        playerHeight,
                        _symbols.value!!.toMutableList(),
                        { value ->
                            value as Symbol
                            if (value.isBomb) {
                                livesCallBack?.invoke(_lives.value!!)
                                scoresCallback?.invoke(_scores.value!!)
                                _lives.postValue(_lives.value!! - 1)
                                _scores.postValue(_scores.value!! - 100)
                            } else {
                                livesCallBack?.invoke(_lives.value!!)
                                scoresCallback?.invoke(_scores.value!!)
                                _scores.postValue(_scores.value!! + 10)
                            }

                        }, { value ->
                            value as Symbol
                            if (!value.isBomb) {
                                scoresCallback?.invoke(_scores.value!!)
                                livesCallBack?.invoke(_lives.value!!)
                                _lives.postValue(_lives.value!! - 1)
                                _scores.postValue(_scores.value!! - 50)
                            }
                        },
                        distance = distance
                    ).toList() as List<Symbol>
                )
            }
        }
    }

    fun initPlayer(x: Int, y: Int, playerWidth: Int) {
        _playerXY.postValue(
            XYIMpl(
                x = x / 2 - (playerWidth.toFloat() / 2),
                y = y - playerWidth.toFloat()
            )
        )
    }

    fun playerMoveLeft(limit: Float) {
        if (_playerXY.value!!.x - 4f > limit) {
            _playerXY.postValue(XYIMpl(_playerXY.value!!.x - 4, _playerXY.value!!.y))
        }
    }

    fun playerMoveRight(limit: Float) {
        if (_playerXY.value!!.x + 4f < limit) {
            _playerXY.postValue(XYIMpl(_playerXY.value!!.x + 4, _playerXY.value!!.y))
        }
    }

    override fun onCleared() {
        super.onCleared()
        stop()
    }
}