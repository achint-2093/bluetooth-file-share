package com.techuntried.bluetooth.ui.file

import com.techuntried.bluetoothshare.domain.ConnectionResult
import com.techuntried.bluetoothshare.domain.model.BluetoothDeviceItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface FileBluetoothController {
    //store all scanned devices
    val scannedDevices: StateFlow<List<BluetoothDeviceItem>>
    //for scanning devices
    fun scanDevices()
    //for set device to be discoverable on receiver device and connect when client connect
    fun waitForIncomingConnections(): Flow<ConnectionResult>
    //for closing all connections server socket and client socket
    fun closeConnection()
}