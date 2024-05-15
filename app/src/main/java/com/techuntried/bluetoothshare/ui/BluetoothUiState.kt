package com.techuntried.bluetoothshare.ui

import com.techuntried.bluetoothshare.domain.model.BluetoothDeviceItem

data class BluetoothUiState(
    val scannedDevices: List<BluetoothDeviceItem> = emptyList(),
    val pairedDevices: List<BluetoothDeviceItem> = emptyList(),
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val errorMessage: String? = null,
)

data class ConnectionUiState(
    val errorMessage: String? = null,
    val scannedDevices: List<BluetoothDeviceItem> = emptyList(),
    val connectionState: ConnectionState?=null
)

enum class ConnectionState {
    Connecting,
    Waiting,
    Connected,
    Failed,
    Scanning
}

