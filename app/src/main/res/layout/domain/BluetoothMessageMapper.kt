package com.techuntried.bluetooth.domain

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.techuntried.bluetoothshare.domain.model.BluetoothDeviceItem
import com.techuntried.bluetoothshare.domain.model.BluetoothMessage

fun String.toBluetoothMessage(isFromLocalUser: Boolean): BluetoothMessage {
    val name = substringBeforeLast("#")
    val message = substringAfter("#")
    return BluetoothMessage(
        message = message,
        senderName = name,
        isFromLocalUser = isFromLocalUser
    )
}

fun BluetoothMessage.toByteArray(): ByteArray {
    return "$senderName#$message".encodeToByteArray()
}



@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceDomain(): BluetoothDeviceItem {
    return BluetoothDeviceItem(
        name = name,
        address = address
    )
}