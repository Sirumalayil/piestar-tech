package com.pistartech.postureperfect.ui.screens

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.pistartech.postureperfect.R
import com.pistartech.postureperfect.ui.theme.PrimaryColor
import com.pistartech.postureperfect.utils.LocalGifImage
import com.pistartech.postureperfect.viewmodel.BluetoothViewModel

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


@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun BluetoothDevicesListing(
    navController: NavHostController?,
    bluetoothViewmodel: BluetoothViewModel?) {

    val nearbyDevices = bluetoothViewmodel?.nearbyDevices?.collectAsState()?.value ?: emptyList()

    LaunchedEffect(Unit) { bluetoothViewmodel?.startDiscovery() }

    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
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
                        BluetoothDeviceItem(device,navController)
                    }
                }
            } else {
                Text("No devices found. Please scan.")
            }

        }
    }
}

@Composable
fun BluetoothDeviceItem(device: BluetoothDevice, navController: NavHostController?) {
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
                Icon(
                    painter = painterResource(id = R.drawable.ic_chair),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Log.e("TAG","Device: $device")
                if (ActivityCompat.checkSelfPermission(
                        LocalContext.current,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return@Card
                }
                Text(device.name ?: device.address, style = TextStyle(
                    color = PrimaryColor,
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily(Font(R.font.poppins_regular))
                ))
                TextButton(
                    onClick = {
                        pairDevice(device, navController)
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
}

fun pairDevice(device: BluetoothDevice, navController: NavHostController?) {
    try {
        val method = device.javaClass.getMethod("createBond")
        method.invoke(device)
        navController?.navigate("")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

val pairingReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                val device =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                val bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1)

                when (bondState) {
                    BluetoothDevice.BOND_BONDED -> {
                        Log.d("Bluetooth", "Paired with ${device?.name}")
                        // You can now connect to the device using socket, if needed
                    }
                    BluetoothDevice.BOND_NONE -> {
                        Log.d("Bluetooth", "Pairing failed or unpaired")
                    }
                }
            }
        }
    }
}

