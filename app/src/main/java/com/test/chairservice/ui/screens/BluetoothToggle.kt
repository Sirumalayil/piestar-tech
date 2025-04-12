package com.test.chairservice.ui.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lulu.chairservice.R
import com.test.chairservice.ui.theme.PrimaryColor
import com.test.chairservice.viewmodel.BluetoothViewModel

/**
 * Created by Siru malayil on 07-04-2025.
 */

@RequiresApi(Build.VERSION_CODES.P)
@Preview
@Composable
fun PreviewBlueToothToggleScreen() {
    val navController = rememberNavController()
    val bluetoothViewmodel: BluetoothViewModel? = null

    BluetoothToggle(navController, bluetoothViewmodel)
}


@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun BluetoothToggle(
    navController: NavHostController,
    bluetoothViewmodel: BluetoothViewModel?
) {
    val painter = painterResource(id = R.drawable.ic_main_bg)
    val isBluetoothEnabled by (bluetoothViewmodel?.isBluetoothEnabled?.collectAsState()
        ?: mutableStateOf(false))


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painter,
            contentDescription = "background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(R.drawable.ic_chair_with_bluetooth),
                contentDescription = "Bluetooth icon",
                alignment = Alignment.Center,
                modifier = Modifier.size(150.dp),
            )
            Text(
                text = "Setup your chair",
                style = TextStyle(
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    letterSpacing = 0.4.sp,
                    fontFamily = FontFamily(
                        Font(
                            R.font.poppins_bold
                        )
                    )
                ),
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(
                text = "Allow the device to connect to the chair's Bluetooth connect to and determine the relative position of nearby devices?",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    letterSpacing = TextUnit.Unspecified,
                    fontFamily = FontFamily(
                        Font(
                            R.font.poppins_regular
                        )
                    )
                ),
                modifier = Modifier.padding(top = 20.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 5.dp,
                            bottom = 5.dp
                        ),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Turn on Bluetooth",
                            style = TextStyle(
                                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                                fontSize = 16.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        SwitchWithCustomColors(bluetoothViewmodel,isBluetoothEnabled)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor,
                    contentColor = Color.White
                ),
                enabled = isBluetoothEnabled,
                onClick = {
                    //navController.navigate("bluetooth_listing")
                    navController.navigate("analytics")
                },
                modifier = Modifier.fillMaxWidth()
                    .height(42.dp)
            ) {
                Text("Continue")
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun SwitchWithCustomColors(bluetoothViewmodel: BluetoothViewModel?, isBluetoothEnabled: Boolean) {
    val context = LocalContext.current
    var isSwitchChecked by remember { mutableStateOf(false) }
    val isBluetoothEnabled by (bluetoothViewmodel?.isBluetoothEnabled?.collectAsState() ?: mutableStateOf(false))

    val requestBluetoothPermissions =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.all { it.value }
            if (allGranted) {
                bluetoothViewmodel?.toggleBluetooth(true,context)
                isSwitchChecked = true
            } else {
                isSwitchChecked = false
                bluetoothViewmodel?.toggleBluetooth(false,context)
                if (permissionsDeniedPermanently(context, permissions.keys.toList())) {
                    showPermissionSettingsDialog(context) // Show settings dialog if permanently denied
                } else {
                    Toast.makeText(context, "Bluetooth permissions denied", Toast.LENGTH_SHORT).show()
                }
            }
        }

    Switch(
        checked = isSwitchChecked || isBluetoothEnabled,
        onCheckedChange = { isChecked ->
            if (isChecked) {
                val requiredPermissions = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> arrayOf(
                        android.Manifest.permission.BLUETOOTH_CONNECT,
                        android.Manifest.permission.BLUETOOTH_SCAN
                    )
                    Build.VERSION.SDK_INT == Build.VERSION_CODES.Q -> arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    else -> arrayOf(
                        android.Manifest.permission.BLUETOOTH,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    )
                }
                requestBluetoothPermissions.launch(requiredPermissions)
            } else {
                isSwitchChecked = false
                bluetoothViewmodel?.toggleBluetooth(false,context) // Directly disable Bluetooth
            }
        },
        colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.primary,
            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
            uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
            uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
        )
    )
}

private fun permissionsDeniedPermanently(context: Context, permissions: List<String>): Boolean {
    return permissions.any { permission ->
        !ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, permission)
    }
}

fun showPermissionSettingsDialog(context: Context) {
    AlertDialog.Builder(context)
        .setTitle("Permission Required")
        .setMessage("Bluetooth permissions are required to continue. Please enable them in settings.")
        .setPositiveButton("Open Settings") { _, _ ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
        }
        .setNegativeButton("Cancel", null)
        .show()
}
