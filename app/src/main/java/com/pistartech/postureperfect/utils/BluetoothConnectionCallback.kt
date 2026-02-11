package com.pistartech.postureperfect.utils

/**
 * Created by Siru malayil on 15-04-2025.
 */
interface BluetoothConnectionCallback {
    fun onConnected()
    fun onDisconnected()
    fun onDataReceived(data: String)
    fun onError(e: Exception)
}