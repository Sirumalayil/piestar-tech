package com.pistartech.postureperfect.viewmodel

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Created by Siru malayil on 28-02-2025.
 */
class BluetoothViewModel: BaseViewModel() {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val bluetoothLeScanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner

    private var _isBluetoothEnabled: MutableStateFlow<Boolean> = MutableStateFlow(bluetoothAdapter?.isEnabled == true)
    var isBluetoothEnabled: StateFlow<Boolean> = _isBluetoothEnabled

    private var _nearByDevices: MutableStateFlow<List<BluetoothDevice>> = MutableStateFlow(emptyList())
    var nearByDevices: StateFlow<List<BluetoothDevice>> = _nearByDevices


    fun toggleBluetooth(enabled: Boolean,context:Context) {
        bluetoothAdapter?.let { bltAdapter ->
            if (enabled) {
                if (!bltAdapter.isEnabled) {
                    // **Prompt user to enable Bluetooth**
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    context.startActivity(enableBtIntent)
                }
                startScan()
            }
            else {
                bltAdapter.disable()
                _nearByDevices.value = emptyList()
            }
            _isBluetoothEnabled.value = enabled
        }
    }

    @SuppressLint("MissingPermission")
    fun startScan() {
        if (bluetoothLeScanner == null) {
            Log.e("TAG", "Bluetooth LE Scanner is null")
            return
        }

        bluetoothLeScanner.startScan(object: ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                result?.device?.let { device ->
                    viewModelScope.launch {
                        val updatedList = _nearByDevices.value.orEmpty().toMutableList()
                        if (updatedList.none { it.address == device.address }) { // Check by MAC address
                            updatedList.add(device)
                            _nearByDevices.value = updatedList
                            Log.d("TAG", "Device found: ${device.name ?: "Unknown"} - ${device.address}")
                        }
                    }
                }
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                Log.e("TAG","Scan failed: $errorCode")
            }
        })
    }

    fun stopScan() {
        bluetoothLeScanner?.stopScan(object : ScanCallback() {})
    }
}