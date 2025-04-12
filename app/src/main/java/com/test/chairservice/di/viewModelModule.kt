package com.test.chairservice.di


import com.test.chairservice.viewmodel.BluetoothViewModel
import com.test.chairservice.viewmodel.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Created by Siru malayil on 26-03-2025.
 */

val viewModelModule = module {
    viewModel  {BluetoothViewModel()}
    viewModel  {SplashViewModel()}
}