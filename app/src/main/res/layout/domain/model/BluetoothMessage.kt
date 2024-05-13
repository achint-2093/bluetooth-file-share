package com.techuntried.bluetooth.domain.model

data class BluetoothMessage(
    val message: String,
    val senderName: String,
    val isFromLocalUser: Boolean
)
