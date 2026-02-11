package com.pistartech.postureperfect.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Created by Siru malayil on 26-03-2025.
 */
class SplashViewModel(application: Application): BaseViewModel(application) {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()


    
    init {
        viewModelScope.launch {
            delay(3000)
            _isLoading.value = false
        }
    }

}