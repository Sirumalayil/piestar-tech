package com.test.chairservice

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.test.chairservice.ui.screens.Analytics
import com.test.chairservice.ui.screens.BluetoothDevicesListing
import com.test.chairservice.ui.screens.BluetoothToggle
import com.test.chairservice.ui.screens.Faqs
import com.test.chairservice.ui.screens.Home
import com.test.chairservice.ui.screens.ProfileScreen
import com.test.chairservice.ui.screens.SetUpProfile
import com.test.chairservice.ui.theme.ChairServiceTheme
import com.test.chairservice.viewmodel.BluetoothViewModel
import org.koin.androidx.compose.koinViewModel


class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChairServiceTheme {
                MainScreen()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val bluetoothViewmodel: BluetoothViewModel = koinViewModel()

    NavHost(
        navController = navController,
        startDestination = "bluetooth"
    ) {
        composable("bluetooth") {
            BluetoothToggle(navController, bluetoothViewmodel)
        }
        composable("bluetooth_listing") {
            BluetoothDevicesListing(navController,bluetoothViewmodel)
        }
        composable("profile") {
            ProfileScreen(onBackClick = {

            },navController)
        }
        composable("faqs") {
            Faqs(onBackClick = {

            },navController)
        }
        composable("setup_profile") {
            SetUpProfile(onBackClick = {

            },navController)
        }
        composable("analytics") {
            Analytics(onBackClick = {

            },navController)
        }
        composable("home") {
            Home(navController = navController)
        }
    }
}
