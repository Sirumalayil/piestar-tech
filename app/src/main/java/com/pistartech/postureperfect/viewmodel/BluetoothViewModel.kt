package com.pistartech.postureperfect.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pistartech.postureperfect.utils.BluetoothConnectionCallback
import com.pistartech.postureperfect.utils.PairingStatus
import com.pistartech.postureperfect.utils.Utils.MY_UUID
import com.pistartech.postureperfect.utils.hasPermissions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * Created by Siru malayil on 28-02-2025.
 */
class BluetoothViewModel(application: Application): BaseViewModel(application) {
    private val context = getApplication<Application>().applicationContext
//    private val bluetoothAdapter: BluetoothAdapter? =
//        (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothSocket: BluetoothSocket? = null
    private var connectionCallback: BluetoothConnectionCallback? = null

    private val _nearbyDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val nearbyDevices: StateFlow<List<BluetoothDevice>> = _nearbyDevices

    private val foundDevices = mutableSetOf<BluetoothDevice>()

    private var _isBluetoothEnabled: MutableStateFlow<Boolean> = MutableStateFlow(bluetoothAdapter?.isEnabled == true)
    var isBluetoothEnabled: StateFlow<Boolean> = _isBluetoothEnabled

    private val _pairingState = MutableStateFlow<PairingStatus>(PairingStatus.Idle)
    val pairingState: StateFlow<PairingStatus> = _pairingState

    // State to track connection status
    private val _connectionState = MutableLiveData<Boolean>()
    val connectionState: LiveData<Boolean> get() = _connectionState

    private val totalCells = 32 * 32
    private var lastUpdateTime = 0L
    private val updateInterval = 500L
    private val _receivedFloatData = mutableStateOf(List(totalCells) { 0f })
    val receivedFloatData: State<List<Float>> = _receivedFloatData


    fun updateHeatMapData(rawData: String) {
//        val rawValues = rawData.split(",").mapNotNull { it.toFloatOrNull() }
//
//        if (rawValues.isNotEmpty()) {
//            val minVal = rawValues.minOrNull() ?: 0f
//            val maxVal = rawValues.maxOrNull() ?: 1f
//            val range = (maxVal - minVal).takeIf { it != 0f } ?: 1f
//
//            val normalized = rawValues.map { ((it - minVal) / range).coerceIn(0f, 1f) }
//
//            _receivedFloatData.value = (_receivedFloatData.value + normalized)
//                .takeLast(totalCells) // Keep only the latest 900 values
//        }

        /*viewModelScope.launch(Dispatchers.Default) {
            val now = System.currentTimeMillis()
            if (now - lastUpdateTime < updateInterval) return@launch // Skip if updated too recently
            lastUpdateTime = now

            val rawValues = rawData.split(",").mapNotNull { it.toFloatOrNull() }

            if (rawValues.isNotEmpty()) {
                val minVal = rawValues.minOrNull() ?: 0f
                val maxVal = rawValues.maxOrNull() ?: 1f
                val range = (maxVal - minVal).takeIf { it != 0f } ?: 1f

                val normalized = rawValues.map { ((it - minVal) / range).coerceIn(0f, 1f) }

                _receivedFloatData.value = (_receivedFloatData.value + normalized)
                    .takeLast(totalCells)
            }
        }*/

        viewModelScope.launch(Dispatchers.Default) {
            val now = System.currentTimeMillis()
            if (now - lastUpdateTime < updateInterval) return@launch
            lastUpdateTime = now

            val rawValues = rawData.split(",").mapNotNull { it.trim().toFloatOrNull() }

            if (rawValues.isNotEmpty()) {
                val minVal = rawValues.minOrNull() ?: 0f
                val maxVal = rawValues.maxOrNull() ?: 1f
                val range = (maxVal - minVal).takeIf { it != 0f } ?: 1f

                val normalized = rawValues.map { ((it - minVal) / range).coerceIn(0f, 1f) }

                // Maintain rolling buffer of exactly 1024 values
                val currentData = _receivedFloatData.value
                val updated = (currentData + normalized).takeLast(totalCells)
                _receivedFloatData.value = updated
            }
        }
    }


    fun setCallback(callback: BluetoothConnectionCallback) {
        connectionCallback = callback
    }

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

    fun startDiscovery() {
        if (bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter.cancelDiscovery()
        }
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

    fun connectToDevice(device: BluetoothDevice) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID)
                bluetoothAdapter?.cancelDiscovery()
                bluetoothSocket?.connect()

                _connectionState.postValue(true)
                connectionCallback?.onConnected()
                readData(bluetoothSocket)

                val output = bluetoothSocket?.outputStream
                output?.write("Hello Arduino".toByteArray())

            } catch (e: IOException) {
                e.printStackTrace()
                _connectionState.postValue(false)
                connectionCallback?.onError(e)
            }
        }
    }

    private fun readData(bluetoothSocket: BluetoothSocket?) {
        try {
            val input = bluetoothSocket?.inputStream
            val buffer = ByteArray(1024)

            while (true) {
                val bytes = input?.read(buffer)
                if (bytes != null) {
                    if (bytes > 0) {
                        val data = String(buffer, 0, bytes)
                        connectionCallback?.onDataReceived(data)
                    } else {
                        break
                    }
                }
            }
        } catch (e: IOException) {
            connectionCallback?.onDisconnected()
        }
    }


    fun disconnect() {
        bluetoothSocket?.close()
        _connectionState.postValue(false)
        connectionCallback?.onDisconnected()
    }
}

