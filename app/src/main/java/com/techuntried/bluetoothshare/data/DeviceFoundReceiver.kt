package com.techuntried.bluetoothshare.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.UUID

class DeviceFoundReceiver(
    private val onDeviceFound: (BluetoothDevice) -> Unit
) : BroadcastReceiver() {

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            BluetoothDevice.ACTION_FOUND -> {
                val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(
                        BluetoothDevice.EXTRA_DEVICE,
                        BluetoothDevice::class.java
                    )
                } else {
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                }
                val deviceUUIDs = device?.uuids
                if (deviceUUIDs != null) {
                    for (uuid in deviceUUIDs) {
                        if (uuid.uuid == UUID.fromString(AndroidBluetoothController.SERVICE_UUID)) {
                            device.let(onDeviceFound)
                            break
                        }
                    }
                }
            }
        }
    }
}