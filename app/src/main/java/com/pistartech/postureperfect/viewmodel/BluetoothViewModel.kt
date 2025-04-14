package com.pistartech.postureperfect.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.pistartech.postureperfect.utils.PairingStatus
import com.pistartech.postureperfect.utils.Utils.MY_UUID
import com.pistartech.postureperfect.utils.hasPermissions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.IOException
import java.util.UUID

/**
 * Created by Siru malayil on 28-02-2025.
 */
class BluetoothViewModel(application: Application): BaseViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    private val bluetoothAdapter: BluetoothAdapter? =
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter

    private val _nearbyDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val nearbyDevices: StateFlow<List<BluetoothDevice>> = _nearbyDevices

    private val foundDevices = mutableSetOf<BluetoothDevice>()

    private var _isBluetoothEnabled: MutableStateFlow<Boolean> = MutableStateFlow(bluetoothAdapter?.isEnabled == true)
    var isBluetoothEnabled: StateFlow<Boolean> = _isBluetoothEnabled

    private val _pairingState = MutableStateFlow<PairingStatus>(PairingStatus.Idle)
    val pairingState: StateFlow<PairingStatus> = _pairingState

    fun updatePairingState(status: PairingStatus) {
        _pairingState.value = status
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        if (foundDevices.add(it)) {
                            _nearbyDevices.value = foundDevices.toList()
                        }
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    // Optionally clear old found devices
                    foundDevices.clear()

                    // Add already bonded devices just once
//                    val bondedDevices = bluetoothAdapter?.bondedDevices ?: emptySet()
//                    bondedDevices.forEach {
//                        foundDevices.add(it)
//                    }

                    _nearbyDevices.value = foundDevices.toList()
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    //startDiscovery()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startDiscovery() {
        foundDevices.clear()
        _nearbyDevices.value = emptyList()

        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
        context.registerReceiver(receiver, filter)

        val endFilter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        context.registerReceiver(receiver, endFilter)
        bluetoothAdapter?.startDiscovery()
    }

    //@SuppressLint("MissingPermission")
    fun stopDiscovery() {
        bluetoothAdapter?.cancelDiscovery()
        try {
            context.unregisterReceiver(receiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopDiscovery()
    }

    @SuppressLint("MissingPermission")
    fun toggleBluetooth(enabled: Boolean,context:Context) {
        if (!hasPermissions(context)) return
        bluetoothAdapter?.let { bltAdapter ->
            if (enabled) {
                if (!bltAdapter.isEnabled) {
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    context.startActivity(enableBtIntent)
                }
            }
            else {
                val enableBtIntent = Intent("android.bluetooth.adapter.action.REQUEST_DISABLE")
                context.startActivity(enableBtIntent)
                _nearbyDevices.value = emptyList()
            }
            _isBluetoothEnabled.value = enabled
        }
    }

    fun connectToDevice(device: BluetoothDevice, context: Context) {
//        if (!hasPermissions(context = context)) return
//        if (device.bondState == BluetoothDevice.BOND_NONE) {
//            device.createBond()
//        } else if (device.bondState == BluetoothDevice.BOND_BONDED) {
//            initiateConnection(device)
//        }

        Thread {
            try {
                val socket = device.createRfcommSocketToServiceRecord(MY_UUID)
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                socket.connect()
                Log.d("BluetoothClient", "Connected to server!")

                val inputStream = socket.inputStream
                val outputStream = socket.outputStream

                // Example: Send message to server
                outputStream.write("Hello from Client".toByteArray())

                // Read response
                val buffer = ByteArray(1024)
                val bytes = inputStream.read(buffer)
                val response = String(buffer, 0, bytes)
                Log.d("BluetoothClient", "Received: $response")
            } catch (e: IOException) {
                Log.e("BluetoothClient", "Connection error: ${e.message}", e)
            }
        }.start()
    }

    fun initiateConnection(device: BluetoothDevice) {
        val uuid = MY_UUID
        try {
            val socket = device.createRfcommSocketToServiceRecord(uuid)
            bluetoothAdapter?.cancelDiscovery()
            socket.connect()
            // Socket connected, use input/output stream
        } catch (e: IOException) {
            Log.e("Bluetooth", "Socket connect failed", e)
        }
    }
}

