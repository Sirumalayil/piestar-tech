package com.pistartech.postureperfect.viewmodel

import android.Manifest
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        if (foundDevices.add(it)) {
                            _nearbyDevices.value = foundDevices.toList()
                        }
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    startDiscovery()
                }
            }
        }
    }

    fun startDiscovery() {
        foundDevices.clear()
        _nearbyDevices.value = emptyList()

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        context.registerReceiver(receiver, filter)

        val endFilter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        context.registerReceiver(receiver, endFilter)

        bluetoothAdapter?.startDiscovery()
    }

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

    fun toggleBluetooth(enabled: Boolean,context:Context) {
        bluetoothAdapter?.let { bltAdapter ->
            if (enabled) {
                if (!bltAdapter.isEnabled) {
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
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
}