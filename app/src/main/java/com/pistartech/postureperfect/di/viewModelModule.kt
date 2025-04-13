package com.pistartech.postureperfect.di


import com.pistartech.postureperfect.viewmodel.BluetoothViewModel
import com.pistartech.postureperfect.viewmodel.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Created by Siru malayil on 26-03-2025.
 */

val viewModelModule = module {
    viewModel  {BluetoothViewModel()}
    viewModel  {SplashViewModel()}
}