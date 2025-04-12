package com.test.chairservice

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.test.chairservice.viewmodel.SplashViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Created by Siru malayil on 26-03-2025.
 */
@SuppressLint("CustomSplashScreen")
class Splashscreen: ComponentActivity() {
    private var splashViewModel: SplashViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        splashViewModel = getViewModel()
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                splashViewModel?.isLoading?.value == true
            }
        }
        super.onCreate(savedInstanceState)
        observeSplashScreen()
    }

    private fun observeSplashScreen() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                splashViewModel?.isLoading?.collect { isLoading ->
                    if (!isLoading) {
                        val options = ActivityOptions.makeCustomAnimation(
                            this@Splashscreen, 0,0
                        )
                        startActivity(Intent(
                            this@Splashscreen,
                            MainActivity::class.java),
                            options.toBundle())
                        finish()
                    }
                }
            }
        }
    }
}