package com.techuntried.bluetooth.ui.file

import com.techuntried.bluetoothshare.domain.model.BluetoothDeviceItem

data class FileUiState(
    val devices:List<BluetoothDeviceItem> = emptyList(),
    val connectionState:ConnectionState?=null,
    val errorMessage:String?=null
)

enum class ConnectionState{
    Connected,Connecting,Failed,Disconnect,Waiting,Scanning
}
