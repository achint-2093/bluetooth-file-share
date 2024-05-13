package com.techuntried.bluetooth.ui.file

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import com.techuntried.bluetoothshare.domain.ConnectionResult
import com.techuntried.bluetoothshare.domain.model.BluetoothDeviceItem
import com.techuntried.bluetoothshare.domain.toBluetoothDeviceDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import java.util.UUID

@SuppressLint("MissingPermission")
class FileController(private val context: Context) : FileBluetoothController {

    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private var serverSocket: BluetoothServerSocket? = null
    private var clientSocket: BluetoothSocket? = null

    val foundDeviceReceiver = FoundDeviceReceiver { device ->
        val bluetoothDevice = com.techuntried.bluetoothshare.domain.toBluetoothDeviceDomain()
        _scannedDevices.update { devices ->
            val newDevice = com.techuntried.bluetoothshare.domain.toBluetoothDeviceDomain()
            if (newDevice in devices) devices else devices + newDevice
        }
    }

    private val _scannedDevices = MutableStateFlow<List<BluetoothDeviceItem>>(emptyList())
    override val scannedDevices: StateFlow<List<BluetoothDeviceItem>>
        get() = _scannedDevices.asStateFlow()

    override fun scanDevices() {
        context.registerReceiver(foundDeviceReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
        bluetoothAdapter?.startDiscovery()
    }

    override fun waitForIncomingConnections(): Flow<ConnectionResult> {
        return flow {
            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                return@flow
            }
            serverSocket = bluetoothAdapter?.listenUsingRfcommWithServiceRecord(
                "File_Transfer", UUID.fromString(
                    MY_UUID
                )
            )
            var shouldLoop = true
            while (shouldLoop) {
                clientSocket = try {
                    serverSocket?.accept()
                } catch (e: Exception) {
                    shouldLoop = false
                    emit(ConnectionResult.Error(e.message.toString()))
                    null
                }
                emit(ConnectionResult.ConnectionEstablished)
                clientSocket?.let {
                    serverSocket?.close()
                }
            }
        }.onCompletion {
            closeConnection()
        }.flowOn(Dispatchers.IO)
    }

    override fun closeConnection() {
        serverSocket?.close()
        clientSocket?.close()
        serverSocket = null
        clientSocket = null
    }

    private fun hasPermission(permission: String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val MY_UUID = ""
    }
}