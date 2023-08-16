package com.fall.game.domain

import com.fall.game.core.library.XY

data class Symbol(
    override var y: Float,
    override var x: Float,
    val isBomb: Boolean,
    val value: Int
) : XY