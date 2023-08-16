package com.fall.game.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel: ViewModel() {
    private val _difficulty = MutableLiveData(0)
    val difficulty: LiveData<Int> = _difficulty

    fun setDifficulty(difficulty: Int) {
        _difficulty.postValue(difficulty)
    }
}