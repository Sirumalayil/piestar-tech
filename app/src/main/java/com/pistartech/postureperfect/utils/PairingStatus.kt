package com.pistartech.postureperfect.utils

import android.bluetooth.BluetoothDevice

/**
 * Created by Siru malayil on 14-04-2025.
 */
sealed class PairingStatus {
    object Idle : PairingStatus()
    data class InProgress (val device: BluetoothDevice): PairingStatus()
    data class Success(val device: BluetoothDevice) : PairingStatus()
    object Failed : PairingStatus()
}