package com.techuntried.bluetooth.ui.file

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techuntried.bluetoothshare.domain.ConnectionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class FileViewModel @Inject constructor(
    private val fileBluetoothController: FileBluetoothController
) : ViewModel() {

    private val _state = MutableStateFlow(FileUiState())
    val state = combine(
        fileBluetoothController.scannedDevices,
        _state
    ) { scannedDevices, state ->
        state.copy(
            devices = scannedDevices,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    private var deviceConnectionJob: Job? = null

//    init {
//        fileBluetoothController.isConnected.onEach { isConnected ->
//            _state.update {
//                it.copy(
//                    connectionState =
//                    if (isConnected) ConnectionState.Connected else ConnectionState.Connecting
//                )
//            }
//        }.launchIn(viewModelScope)
//
//        fileBluetoothController.errors.onEach { error ->
//            _state.update {
//                it.copy(
//                    errorMessage = error
//                )
//            }
//        }.launchIn(viewModelScope)
//    }

//    fun connectToDevice(device: BluetoothDeviceItem) {
//        _state.update { it.copy(connectionState = ConnectionState.Connecting) }
//        deviceConnectionJob = fileBluetoothController
//            .connectToDevice(device)
//            .listen()
//    }
//
//    fun disconnectFromDevice() {
//        deviceConnectionJob?.cancel()
//        fileBluetoothController.closeConnection()
//        _state.update {
//            it.copy(
//                connectionState = ConnectionState.Disconnect
//            )
//        }
//    }

    fun waitForIncomingConnections() {
        _state.update { it.copy(connectionState = ConnectionState.Waiting) }
        deviceConnectionJob = fileBluetoothController
            .waitForIncomingConnections()
            .listen()
    }

//    fun sendMessage(message: String) {
//        viewModelScope.launch {
//            val bluetoothMessage = fileBluetoothController.trySendMessage(message)
//            if(bluetoothMessage != null) {
//                _state.update { it.copy(
//                    messages = it.messages + bluetoothMessage
//                ) }
//            }
//        }
//    }

    fun startScan() {
        fileBluetoothController.scanDevices()
        _state.update { it.copy(connectionState = ConnectionState.Scanning) }
    }

    fun stopScan() {
        // fileBluetoothController.stopDiscovery()
    }

    private fun Flow<ConnectionResult>.listen(): Job {
        return onEach { result ->
            when (result) {
                ConnectionResult.ConnectionEstablished -> {
                    _state.update {
                        it.copy(
                            connectionState = ConnectionState.Connected,
                            errorMessage = null
                        )
                    }
                }

                is ConnectionResult.TransferSucceeded -> {
//                    _state.update { it.copy(
//                        messages = it.messages + result.message
//                    ) }
                }

                is ConnectionResult.Error -> {
                    _state.update {
                        it.copy(
                            connectionState = ConnectionState.Failed,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }.catch { throwable ->
            fileBluetoothController.closeConnection()
            _state.update {
                it.copy(
                    connectionState = ConnectionState.Failed
                )
            }
        }.launchIn(viewModelScope)
    }


}