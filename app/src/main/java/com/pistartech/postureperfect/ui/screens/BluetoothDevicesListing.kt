package com.pistartech.postureperfect.ui.screens

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.pistartech.postureperfect.R
import com.pistartech.postureperfect.ui.theme.PrimaryColor
import com.pistartech.postureperfect.utils.BluetoothConnectionCallback
import com.pistartech.postureperfect.utils.LocalGifImage
import com.pistartech.postureperfect.utils.PairingStatus
import com.pistartech.postureperfect.utils.Utils.MY_UUID
import com.pistartech.postureperfect.utils.hasPermissions
import com.pistartech.postureperfect.viewmodel.BluetoothViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * Created by Siru malayil on 07-04-2025.
 */

@RequiresApi(Build.VERSION_CODES.P)
@Preview
@Composable
fun PreviewBluetoothDeviceListing() {
    val navController = rememberNavController()
    val viewModel: BluetoothViewModel? = null

    BluetoothDevicesListing(navController, viewModel)
}

@SuppressLint("MissingPermissions")
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun BluetoothDevicesListing(
    navController: NavHostController?,
    bluetoothViewmodel: BluetoothViewModel?) {
    val context = LocalContext.current
    Log.e("Permission", "hasPermission: ${hasPermissions(context)}")
    if (!hasPermissions(context)) return

    val connectionState = bluetoothViewmodel?.connectionState?.observeAsState(false)
//    val device: BluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice("XX:XX:XX:XX:XX:XX")

    val snackBarHostState = remember { SnackbarHostState() }
    val pairingStatus = bluetoothViewmodel?.pairingState?.collectAsState()?.value
    val nearbyDevices = bluetoothViewmodel?.nearbyDevices?.collectAsState()?.value ?: emptyList()
//    val nearbyDevices = bluetoothViewmodel?.bleDevices?.collectAsState()?.value ?: emptyList()
    LaunchedEffect(Unit) { bluetoothViewmodel?.startDiscovery() }

    LaunchedEffect(Unit) {
        bluetoothViewmodel?.setCallback(object : BluetoothConnectionCallback {
            override fun onConnected() {
                Log.d("Bluetooth", "Connected to device")
                CoroutineScope(Dispatchers.Main).launch {
                    navController?.navigate("bluetooth_connected")
                }
            }

            override fun onDisconnected() {
                Log.d("Bluetooth", "Disconnected")
            }

            override fun onDataReceived(data: String) {
                bluetoothViewmodel.updateHeatMapData(data)
                //Log.e("Bluetooth", "data: $data")
            }

            override fun onError(e: Exception) {
                Log.e("Bluetooth", "Error: ${e.message}")
            }
        })
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()
            .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            LaunchedEffect(pairingStatus) {
                pairingStatus?.let {
                    when (it) {
                        is PairingStatus.InProgress -> {
                            snackBarHostState.showSnackbar("Pairing with ${it.device.name ?: it.device.address}")
                        }
                        is PairingStatus.Success -> {
                            //snackBarHostState.showSnackbar("Paired with ${it.device.name ?: it.device.address}")
                            //navController?.navigate("bluetooth_connected")
                        }
                        is PairingStatus.Failed -> {
                            snackBarHostState.showSnackbar("Pairing failed")
                        }
                        PairingStatus.Idle -> {
                            //snackBarHostState.showSnackbar("Searching nearest devices...")
                        }
                    }
                }
            }

            Image(
                painter = painterResource(R.drawable.ic_main_bg),
                modifier = Modifier.fillMaxSize(),
                contentDescription = "background",
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
                LocalGifImage(drawable = R.raw.ic_bluetooth_anim)

                Spacer(modifier = Modifier.height(30.dp))

                Text("Scanning...",
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.poppins_bold)),
                        fontSize = 20.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text("select a chair to connect")

                Spacer(modifier = Modifier.height(10.dp))

                if (nearbyDevices.isNotEmpty()) {
                    LazyColumn {
                        items(nearbyDevices) { device ->
                            BluetoothDeviceItem(
                                device,bluetoothViewmodel,navController)
                        }
                    }
                } else {
                    Text("No devices found. Please scan.")
                }

            }
        }
    }
//    startBluetoothServer(BluetoothAdapter.getDefaultAdapter())
//    DisposableEffect(Unit) {
//        val pairingReceiver = object : BroadcastReceiver() {
//            override fun onReceive(context: Context?, intent: Intent?) {
//                when (intent?.action) {
//                    BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
//                        val device =
//                            intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
//                        val bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1)
//
//                        when (bondState) {
//                            BluetoothDevice.BOND_BONDING -> {
//                                bluetoothViewmodel?.updatePairingState(PairingStatus.InProgress)
//                            }
//                            BluetoothDevice.BOND_BONDED -> {
//                                bluetoothViewmodel?.updatePairingState(PairingStatus.Success(device!!))
//                            }
//                            BluetoothDevice.BOND_NONE -> {
//                                bluetoothViewmodel?.updatePairingState(PairingStatus.Failed)
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        val filter = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
//        context.registerReceiver(pairingReceiver, filter)
//
//        // Properly return DisposableEffectResult
//        onDispose {
//            context.unregisterReceiver(pairingReceiver)
//        }
//    }
}

@SuppressLint("MissingPermission")
@Composable
fun BluetoothDeviceItem(
    device: BluetoothDevice,
    bluetoothViewmodel: BluetoothViewModel?,
    navController: NavHostController?
) {
    val showDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
            horizontalAlignment = Alignment.Start) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically)
            {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_chair),
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        device.name ?: device.address,
                        style = TextStyle(
                            color = PrimaryColor,
                            textAlign = TextAlign.Start,
                            fontWeight = FontWeight.Normal,
                            fontFamily = FontFamily(Font(R.font.poppins_medium))
                        )
                    )
                }
                TextButton(
                    onClick = {
                        showDialog.value = true
                    }
                ) {
                    Text(
                        text = "Connect",
                        style = TextStyle(
                            color = PrimaryColor,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily(Font(R.font.poppins_bold))
                        )
                    )
                }
            }
        }
    }
    BluetoothBondingHandler(bluetoothViewmodel)

    if (showDialog.value) {
        ShowPairingConsentAlert(
            onPair = {
                bluetoothViewmodel?.connectToDevice(device)
                bluetoothViewmodel?.updatePairingState(PairingStatus.InProgress(device))
            },
            onCancel = {
                showDialog.value = false
            },
            onDismiss = {
                showDialog.value = false
            }
        )
    }
}

@Composable
fun ShowPairingConsentAlert(
    onPair: () -> Unit,
    onCancel: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Pair Device",
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
                text = "Are you sure you wan to pair this device?")
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
//                    .border(
//                        width = 1.dp,
//                        color = PrimaryColor,
//                        shape = RoundedCornerShape(4.dp)
//                    )
//                    .clip(RoundedCornerShape(4.dp)),
//                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onPair()
                            onDismiss()
                        }
                        .background(Color.White)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Pair", color = PrimaryColor)
                }
//                Box(
//                    modifier = Modifier
//                        .width(1.dp)
//                        .fillMaxHeight()
//                        .background(PrimaryColor)
//                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onCancel()
                        }
                        .background(Color.White)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Cancel", color = PrimaryColor)
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White,
        tonalElevation = 8.dp
    )
}

@Composable
fun BluetoothBondingHandler(bluetoothViewModel: BluetoothViewModel?) {
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val bondingReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val action = intent?.action
                if (action == BluetoothDevice.ACTION_BOND_STATE_CHANGED) {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1)
                    val prevBondState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, -1)

                    if (bondState == BluetoothDevice.BOND_BONDED &&
                        prevBondState == BluetoothDevice.BOND_BONDING
                    ) {
                        Log.d("Bluetooth", "Bonded with ${device?.name}")
                        device?.let {
                            bluetoothViewModel?.initiateConnection(it)
                        }
                    }
                }
            }
        }

        val filter = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        context.registerReceiver(bondingReceiver, filter)

        onDispose {
            context.unregisterReceiver(bondingReceiver)
        }
    }
}
