package com.pistartech.postureperfect.ui.screens

import android.annotation.SuppressLint
import android.app.Activity
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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
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
import com.pistartech.postureperfect.R
import com.pistartech.postureperfect.ui.theme.PrimaryColor
import com.pistartech.postureperfect.viewmodel.BluetoothViewModel

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
        ?: remember { mutableStateOf(false) })
    var switchChecked by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(!isBluetoothEnabled) }

    val requestBluetoothPermissions = rememberBluetoothPermissionHandler(
        bluetoothViewModel = bluetoothViewmodel,
        onPermissionResult = { granted ->
            switchChecked = granted
        }
    )

    ShowBluetoothEnableRequestDialog(
        showDialog = showDialog,
        onDismiss = { showDialog = false },
        requestBluetoothPermissions = requestBluetoothPermissions
    )

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

                        SwitchWithCustomColors(
                            bluetoothViewModel = bluetoothViewmodel,
                            isBluetoothEnabled = isBluetoothEnabled,
                            requestBluetoothPermissions = requestBluetoothPermissions,
                            onSwitchChanged = { checked -> switchChecked = checked })
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
                    navController.navigate("bluetooth_listing")
                },
                modifier = Modifier.fillMaxWidth()
                    .height(42.dp)
            ) {
                Text("Continue")
            }
        }
    }
}

@Composable
fun ShowBluetoothEnableRequestDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    requestBluetoothPermissions: (Array<String>) -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Enable Bluetooth",
                    fontSize = 18.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily(Font(R.font.poppins_bold))
                )
            },
            text = {
                Text(
                    fontSize = 14.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                    text = "This app requires Bluetooth to be enabled to function properly.")
            },
            confirmButton = {
                Box(modifier = Modifier
                    .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        modifier = Modifier.width(150.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryColor,
                            contentColor = Color.White
                        ),
                        onClick = {
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
                            requestBluetoothPermissions(requiredPermissions)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(8.dp) // Rounded rectangle
                    ) {
                        Text("Ok",
                            fontFamily = FontFamily(Font(R.font.poppins_medium)),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            },
            shape = RoundedCornerShape(16.dp), // Rectangle with soft corners
            containerColor = Color.White,
            tonalElevation = 8.dp
        )
    }
}

@Composable
fun rememberBluetoothPermissionHandler(
    bluetoothViewModel: BluetoothViewModel?,
    onPermissionResult: (granted: Boolean) -> Unit
): (Array<String>) -> Unit {
    val context = LocalContext.current
    var showPermissionDialog by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            bluetoothViewModel?.toggleBluetooth(true, context)
            bluetoothViewModel?.startDiscovery()
        } else {
            bluetoothViewModel?.toggleBluetooth(false, context)
            if (permissionsDeniedPermanently(context, permissions.keys.toList())) {
                showPermissionDialog = true
            } else {
                Toast.makeText(context, "Bluetooth permissions denied", Toast.LENGTH_SHORT).show()
            }
        }
        onPermissionResult(allGranted)
    }

    if (showPermissionDialog) ShowPermissionSettingsDialog(context)

    return remember {
        { permissions: Array<String> ->
            launcher.launch(permissions)
        }
    }
}



@SuppressLint("UnrememberedMutableState")
@Composable
fun SwitchWithCustomColors(
    bluetoothViewModel: BluetoothViewModel?,
    isBluetoothEnabled: Boolean,
    requestBluetoothPermissions: (Array<String>) -> Unit,
    onSwitchChanged: (Boolean) -> Unit
) {
    var isSwitchChecked by remember { mutableStateOf(false) }
    val context = LocalContext.current

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
                requestBluetoothPermissions(requiredPermissions)
            } else {
                isSwitchChecked = false
                bluetoothViewModel?.toggleBluetooth(
                    false,
                    context = context)
            }
            onSwitchChanged(isChecked)
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

@Composable
fun ShowPermissionSettingsDialog(context: Context) {
    var openDialog by remember { mutableStateOf(true) }
    if (openDialog) {
        AlertDialog(
            onDismissRequest = {
                openDialog = false
            },
            title = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Permission Required",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily(Font(R.font.poppins_bold))
                )
            },
            text = {
                Text(
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                    text = "Bluetooth permissions are required to continue. Please enable them in settings.")
            },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .border(
                            width = 1.dp,
                            color = PrimaryColor,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .clip(RoundedCornerShape(4.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // OK Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                                context.startActivity(intent)
                            }
                            .background(Color.White)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("OK", color = PrimaryColor)
                    }

                    // Vertical divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(PrimaryColor)
                    )

                    // Cancel Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                openDialog = false
                            }
                            .background(Color.White)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Cancel", color = PrimaryColor)
                    }
                }
            },
            shape = RoundedCornerShape(16.dp), // Rectangle with soft corners
            containerColor = Color.White,
            tonalElevation = 8.dp
        )
    }
}
