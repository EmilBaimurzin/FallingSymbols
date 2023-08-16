package com.fall.game.domain

import android.content.Context

class SP (private val context: Context) {
    private val sp = context.getSharedPreferences("SP", Context.MODE_PRIVATE)

    fun getDifficulty(): Int = sp.getInt("DIFF", 1)
    fun setDifficulty(difficulty: Int) = sp.edit().putInt("DIFF", difficulty).apply()

    fun setLives(life: Int) = sp.edit().putInt("LIVES", life).apply()
    fun getLives(): Int = sp.getInt("LIVES", 0)

    fun setScores(scores: Int) = sp.edit().putInt("SCORES", scores).apply()
    fun getScores(): Int = sp.getInt("SCORES", 0)

}