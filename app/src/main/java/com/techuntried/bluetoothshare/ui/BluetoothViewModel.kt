package com.techuntried.bluetoothshare.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techuntried.bluetoothshare.domain.BluetoothController
import com.techuntried.bluetoothshare.domain.ConnectionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bluetoothController: BluetoothController
) : ViewModel() {

    private val _state = MutableStateFlow(ConnectionUiState())
    val state = combine(
        bluetoothController.scannedDevices,
        bluetoothController.pairedDevices,
        _state
    ) { scannedDevices, pairedDevices, state ->
        state.copy(scannedDevices = scannedDevices)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    private var deviceConnectionJob: Job? = null

    init {
//        bluetoothController.isConnected.onEach { isConnected ->
//            _state.update { it.copy(isConnected = isConnected) }
//        }.launchIn(viewModelScope)
//
//        bluetoothController.errors.onEach { error ->
//            _state.update {
//                it.copy(
//                    errorMessage = error
//                )
//            }
//        }.launchIn(viewModelScope)
    }

//    fun connectToDevice(device: BluetoothDeviceItem) {
//        _state.update { it.copy(isConnecting = true) }
//        deviceConnectionJob = bluetoothController
//            .connectToDevice(device)
//            .listen()
//    }

//    fun disconnectFromDevice() {
//        deviceConnectionJob?.cancel()
//        bluetoothController.closeConnection()
//        _state.update {
//            it.copy(
//                isConnecting = false,
//                isConnected = false
//            )
//        }
//    }

    fun waitForIncomingConnections() {
        _state.update { it.copy(connectionState = ConnectionState.Waiting) }
        deviceConnectionJob = bluetoothController
            .startBluetoothServer()
            .listen()
    }


    fun startScan() {
        bluetoothController.startDiscovery()
        _state.update { it.copy(connectionState = ConnectionState.Scanning) }
    }

    fun stopScan() {
        bluetoothController.stopDiscovery()
    }

    private fun Flow<ConnectionResult>.listen(): Job {
        return onEach { result ->
            when (result) {
                ConnectionResult.ConnectionEstablished -> {
                    _state.update {
                        it.copy(connectionState = ConnectionState.Connected)

                    }
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
        }
            .catch { throwable ->
                bluetoothController.closeConnection()
                _state.update {
                    it.copy(
                        connectionState = ConnectionState.Failed,
                        errorMessage = throwable.message.toString()
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothController.release()
    }
}