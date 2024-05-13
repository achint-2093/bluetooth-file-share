package com.techuntried.bluetooth.domain


import com.techuntried.bluetoothshare.domain.ConnectionResult
import com.techuntried.bluetoothshare.domain.model.BluetoothDeviceItem
import com.techuntried.bluetoothshare.domain.model.BluetoothMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothController {
    val isConnected: StateFlow<Boolean>
    val scannedDevices: StateFlow<List<BluetoothDeviceItem>>
    val pairedDevices: StateFlow<List<BluetoothDeviceItem>>
    val errors: SharedFlow<String>

    fun startDiscovery()
    fun stopDiscovery()

    fun startBluetoothServer(): Flow<ConnectionResult>
    fun connectToDevice(device: BluetoothDeviceItem): Flow<ConnectionResult>

    suspend fun trySendMessage(message: String): BluetoothMessage?

    fun closeConnection()
    fun release()
}