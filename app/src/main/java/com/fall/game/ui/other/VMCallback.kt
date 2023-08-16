package com.fall.game.ui.other

import androidx.lifecycle.ViewModel

class VMCallback: ViewModel() {
    var settingsCallback: (() -> Unit)? = null
}