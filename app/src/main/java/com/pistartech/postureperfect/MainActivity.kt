package com.pistartech.postureperfect

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
import com.pistartech.postureperfect.ui.screens.Analytics
import com.pistartech.postureperfect.ui.screens.BluetoothConnectionSuccess
import com.pistartech.postureperfect.ui.screens.BluetoothDevicesListing
import com.pistartech.postureperfect.ui.screens.BluetoothToggle
import com.pistartech.postureperfect.ui.screens.Faqs
import com.pistartech.postureperfect.ui.screens.Home
import com.pistartech.postureperfect.ui.screens.ProfileScreen
import com.pistartech.postureperfect.ui.screens.SetUpProfile
import com.pistartech.postureperfect.ui.theme.ChairServiceTheme
import com.pistartech.postureperfect.viewmodel.BluetoothViewModel
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
                navController.popBackStack()
            },navController)
        }
        composable("faqs") {
            Faqs(onBackClick = {
                navController.popBackStack()
            },navController)
        }
        composable("setup_profile") {
            SetUpProfile(onBackClick = {
                navController.popBackStack()
            },navController)
        }
        composable("analytics") {
            Analytics(onBackClick = {
                navController.popBackStack()
            },navController)
        }
        composable("home") {
            Home(navController = navController, bluetoothViewmodel)
        }
        composable("bluetooth_connected") {
            BluetoothConnectionSuccess(navController = navController,
                onBackClick = {
                    navController.popBackStack()
                })
        }
    }
}
