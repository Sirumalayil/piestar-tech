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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * Created by Siru malayil on 28-02-2025.
 */
class BluetoothViewModel(application: Application): BaseViewModel(application) {
    private val context = getApplication<Application>().applicationContext

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothSocket: BluetoothSocket? = null
    private var connectionCallback: BluetoothConnectionCallback? = null

    private val _nearbyDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val nearbyDevices: StateFlow<List<BluetoothDevice>> = _nearbyDevices

    private val foundDevices = mutableSetOf<BluetoothDevice>()

    private var _isBluetoothEnabled = MutableStateFlow(bluetoothAdapter?.isEnabled == true)
    var isBluetoothEnabled: StateFlow<Boolean> = _isBluetoothEnabled

    private val _pairingState = MutableStateFlow<PairingStatus>(PairingStatus.Idle)
    val pairingState: StateFlow<PairingStatus> = _pairingState

    // State to track connection status
    private val _connectionState = MutableLiveData<Boolean>()
    val connectionState: LiveData<Boolean> get() = _connectionState

    private val _navigateToConnected = MutableSharedFlow<Unit>()
    val navigateToConnected: SharedFlow<Unit> = _navigateToConnected.asSharedFlow()

    private val totalCells = 32 * 32
    private var lastUpdateTime = 0L
    private val updateInterval = 500L
    private val _receivedFloatData = mutableStateOf(List(totalCells) { 0f })
    val receivedFloatData: State<List<Float>> = _receivedFloatData

    private var discoveryReceiverRegistered = false
    private var connectionJob: Job? = null

    fun updateHeatMapData(rawData: String) {
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
        @SuppressLint("MissingPermission")
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
                    foundDevices.clear()
                    _nearbyDevices.value = emptyList()
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    // Discovery finished
                }
                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1)
                    val prevBondState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, -1)

                    when (bondState) {
                        BluetoothDevice.BOND_BONDING -> {
                            updatePairingState(PairingStatus.InProgress(device!!))
                        }
                        BluetoothDevice.BOND_BONDED -> {
                            updatePairingState(PairingStatus.Success(device!!))
                            if (prevBondState == BluetoothDevice.BOND_BONDING) {
                                initiateConnection(device)
                            }
                        }
                        BluetoothDevice.BOND_NONE -> {
                            updatePairingState(PairingStatus.Failed)
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startDiscovery() {
        if (bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter.cancelDiscovery()
        }
        
        if (!discoveryReceiverRegistered) {
            val filter = IntentFilter().apply {
                addAction(BluetoothDevice.ACTION_FOUND)
                addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
                addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
                addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
            }
            context.registerReceiver(receiver, filter)
            discoveryReceiverRegistered = true
        }

        foundDevices.clear()
        _nearbyDevices.value = emptyList()
        bluetoothAdapter?.startDiscovery()
    }

    @SuppressLint("MissingPermission")
    fun stopDiscovery() {
        if (bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter.cancelDiscovery()
        }
        if (discoveryReceiverRegistered) {
            try {
                context.unregisterReceiver(receiver)
                discoveryReceiverRegistered = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopDiscovery()
        disconnect()
    }

    fun toggleBluetooth(enabled: Boolean, context: Context) {
        if (!hasPermissions(context)) return
        bluetoothAdapter?.let { bltAdapter ->
            if (enabled) {
                if (!bltAdapter.isEnabled) {
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(enableBtIntent)
                }
            } else {
                // Note: Disabling bluetooth programmatically is deprecated/restricted on newer Android versions
                // This might not work as expected on Android 13+
                _nearbyDevices.value = emptyList()
            }
            _isBluetoothEnabled.value = bltAdapter.isEnabled
        }
    }

    @SuppressLint("MissingPermission")
    fun initiateConnection(device: BluetoothDevice) {
        connectToDevice(device)
    }

    @SuppressLint("MissingPermission")
    fun connectToDevice(device: BluetoothDevice) {
        connectionJob?.cancel()
        connectionJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                bluetoothSocket?.close()
                bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID)
                bluetoothAdapter?.cancelDiscovery()
                bluetoothSocket?.connect()

                _connectionState.postValue(true)
                connectionCallback?.onConnected()
                _navigateToConnected.emit(Unit)
                
                readData(bluetoothSocket)

            } catch (e: IOException) {
                Log.e("Bluetooth", "Connection failed", e)
                _connectionState.postValue(false)
                connectionCallback?.onError(e)
            }
        }
    }

    private fun readData(bluetoothSocket: BluetoothSocket?) {
        try {
            val input = bluetoothSocket?.inputStream ?: return
            val buffer = ByteArray(1024)

            while (true) {
                val bytes = input.read(buffer)
                if (bytes > 0) {
                    val data = String(buffer, 0, bytes)
                    connectionCallback?.onDataReceived(data)
                } else {
                    break
                }
            }
        } catch (e: IOException) {
            Log.e("Bluetooth", "Disconnected during read", e)
            _connectionState.postValue(false)
            connectionCallback?.onDisconnected()
        }
    }


    fun disconnect() {
        connectionJob?.cancel()
        try {
            bluetoothSocket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        bluetoothSocket = null
        _connectionState.postValue(false)
        connectionCallback?.onDisconnected()
    }
}
