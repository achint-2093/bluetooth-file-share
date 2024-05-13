package com.techuntried.bluetooth.domain

import com.techuntried.bluetoothshare.domain.ConnectionResult
import com.techuntried.bluetoothshare.domain.model.BluetoothMessage



sealed interface ConnectionResult {
    object ConnectionEstablished: ConnectionResult
    data class TransferSucceeded(val message: BluetoothMessage): ConnectionResult
    data class Error(val message: String): ConnectionResult
}