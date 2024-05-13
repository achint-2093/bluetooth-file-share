package com.techuntried.bluetooth.ui

import com.techuntried.bluetoothshare.domain.model.BluetoothDeviceItem
import com.techuntried.bluetoothshare.domain.model.BluetoothMessage

data class BluetoothUiState(
    val scannedDevices: List<BluetoothDeviceItem> = emptyList(),
    val pairedDevices: List<BluetoothDeviceItem> = emptyList(),
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val errorMessage: String? = null,
    val messages: List<BluetoothMessage> = emptyList()
)