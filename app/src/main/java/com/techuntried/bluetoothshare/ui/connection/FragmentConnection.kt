package com.techuntried.bluetoothshare.ui.connection

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import com.techuntried.bluetoothshare.databinding.FragmentConnectionBinding
import com.techuntried.bluetoothshare.ui.BluetoothViewModel
import com.techuntried.bluetoothshare.ui.home.BluetoothDeviceAdapter
import com.techuntried.bluetoothshare.util.ConnectionType
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.InputStream


@AndroidEntryPoint
class FragmentConnection : Fragment() {

    private var _binding: FragmentConnectionBinding? = null
    private val binding get() = _binding!!
    var endpoinid: String? = null

    private val arguments by navArgs<FragmentConnectionArgs>()
    private val viewModel: BluetoothViewModel by viewModels()
    private lateinit var adapter: BluetoothDeviceAdapter
    private var connectionType: ConnectionType? = null

    private var connectionsClient: ConnectionsClient? = null
    private val appId = "com.example.myapp"
    private var deviceFound = false
    private var deviceConnected = false
    private var isFilePicked = false
    var inputStream: InputStream? = null
    private var fileUri: Uri? = null

    private val createPdfLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

    }


    private var filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { selectedFileUri ->
                    val file = File(selectedFileUri.toString()).name
                    binding.fileName.text = file
                    fileUri = selectedFileUri
                    //inputStream = requireContext().contentResolver.openInputStream(selectedFileUri)
                    isFilePicked = true
                }
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConnectionBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createPdfLauncher.launch(
            arrayOf(
                android.Manifest.permission.NEARBY_WIFI_DEVICES,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
        connectionsClient = Nearby.getConnectionsClient(requireActivity())
        val payloadCallback = setPayloadCallback()
        val connectionLifecycleCallback = setConnectionLifeCycleCallback(payloadCallback)
        val endpointDiscoveryCallback = setEndpointDiscoveryCallback()
        if (arguments.connectionType == ConnectionType.Sender) {
            startDiscovery(endpointDiscoveryCallback)
        } else {
            startAdvertise(connectionLifecycleCallback)
        }

        setOnClickListeners(connectionLifecycleCallback, endpointDiscoveryCallback)
    }


    private fun setOnClickListeners(
        connectionLifecycleCallback: ConnectionLifecycleCallback,
        endpointDiscoveryCallback: EndpointDiscoveryCallback
    ) {
        binding.SendButton.setOnClickListener {
            if (deviceConnected) {
                if (inputStream != null) {
                    if (isFilePicked) {
                        val file = File(fileUri.toString())
                        val textMessage = "Hello, this is a simple text message"
                        val textData = textMessage.toByteArray(Charsets.UTF_8)
                        connectionsClient?.sendPayload(endpoinid!!, Payload.fromFile(file))
                    }
                } else {
                    Toast.makeText(context, "file not picked", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "no device connected", Toast.LENGTH_SHORT).show()
            }
        }
        binding.actionButton.setOnClickListener {
            val buttonText = binding.actionButton.text.toString()
            when (buttonText) {
                "Scan" -> {
                    startDiscovery(endpointDiscoveryCallback)
                    updateStatus("Scanning...", "Scanning")
                }

                "Connect" -> {
                    if (deviceFound) {
                        connectionsClient?.requestConnection(
                            "My App", endpoinid!!, connectionLifecycleCallback
                        )
                        updateStatus("Connecting...", "Connecting...")
                    }
                }

                "Disconnect" -> {
                    if (deviceFound) {
                        connectionsClient?.disconnectFromEndpoint(endpoinid!!)
                        updateStatus("Disconnected", "Disconnected")
                    }
                }
            }

        }
        binding.pickFileButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.setType("*/*")
            filePickerLauncher.launch(intent)
        }
    }

    fun updateStatus(text: String, button: String?) {
        binding.status.text = text
        if (button != null)
            binding.actionButton.text = button
    }

    private fun startDiscovery(endpointDiscoveryCallback: EndpointDiscoveryCallback) {
        updateStatus("Scanning...", "Scanning...")
        connectionsClient?.startDiscovery(
            appId,
            endpointDiscoveryCallback,
            DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build()
        )
    }

    private fun startAdvertise(connectionLifecycleCallback: ConnectionLifecycleCallback) {
        updateStatus("Waiting...", "Waiting...")
        connectionsClient?.startAdvertising(
            Build.MODEL.toString(),
            appId,
            connectionLifecycleCallback,
            AdvertisingOptions.Builder().setStrategy(Strategy.P2P_STAR).build()
        )
    }

    private fun setConnectionLifeCycleCallback(payloadCallback: PayloadCallback): ConnectionLifecycleCallback {
        val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
                connectionsClient?.acceptConnection(endpointId, payloadCallback)
                updateStatus("Connection Initiated", "Connecting...")
            }

            override fun onConnectionResult(endpointId: String, resolution: ConnectionResolution) {
                if (resolution.status.statusMessage == "SUCCESS") {
                    endpoinid = endpointId
                    deviceConnected = true
                    updateStatus("${resolution.status.statusMessage}", "Disconnect")
                }

            }

            override fun onDisconnected(endpointId: String) {
                disconnect()
            }
        }
        return connectionLifecycleCallback
    }

    private fun setPayloadCallback(): PayloadCallback {
        val payloadCallback = object : PayloadCallback() {
            override fun onPayloadReceived(endpointId: String, payload: Payload) {
                updateStatus("data received", "")
                when (payload.type) {
                    Payload.Type.FILE -> {
                        val receivedFile = payload.asFile()?.asJavaFile()
                        binding.fileName.text = receivedFile?.name

                        // Process the received file (e.g., save it locally)
                    }

                    Payload.Type.BYTES -> {
                        val receivedBytes = payload.asBytes()
                        val receivedMessage = receivedBytes?.toString(Charsets.UTF_8)
                        // Process the received message
                        binding.fileName.text = receivedMessage
                    }

                }
            }

            override fun onPayloadTransferUpdate(
                endpointId: String, update: PayloadTransferUpdate
            ) {
                updateStatus("update ${update.bytesTransferred}", null)
            }
        }
        return payloadCallback
    }

    private fun setEndpointDiscoveryCallback(): EndpointDiscoveryCallback {
        val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
            override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                updateStatus("Device Found ${info.endpointName}", "Connect")
                endpoinid = endpointId
                binding.status.text = info.endpointName
                deviceFound = true

            }

            override fun onEndpointLost(endpointId: String) {
                updateStatus("endpoint lost", "Scan")
                deviceFound = false
            }
        }
        return endpointDiscoveryCallback
    }


    override fun onDestroyView() {
        super.onDestroyView()
        endpoinid?.let {
            connectionsClient?.disconnectFromEndpoint(it)
        }
        _binding = null

    }

    private fun disconnect() {
        updateStatus("Disconnected", "Scan")
        deviceFound = false
        deviceConnected = false
        endpoinid = null
        connectionsClient?.stopDiscovery()
        connectionsClient?.stopAdvertising()
    }

}