package com.techuntried.bluetooth.ui.home

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import com.techuntried.bluetooth.databinding.FragmentHomeBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.techuntried.bluetooth.R
import com.techuntried.bluetoothshare.domain.model.BluetoothDeviceItem
import com.techuntried.bluetooth.ui.BluetoothViewModel
import com.techuntried.bluetoothshare.ui.home.BluetoothDeviceAdapter
import com.techuntried.bluetoothshare.ui.home.OnDeviceClicked
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentHome : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BluetoothViewModel by activityViewModels()
    private lateinit var pairedDeviceAdapter: BluetoothDeviceAdapter
    private lateinit var scanDeviceAdapter: BluetoothDeviceAdapter

    private val bluetoothManager by lazy {
        getSystemService(requireContext(), BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled == true

    val enableBluetoothLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { /* Not needed */ }

    val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val canEnableBluetooth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            perms[Manifest.permission.BLUETOOTH_CONNECT] == true
        } else true

        if (canEnableBluetooth && !isBluetoothEnabled) {
            enableBluetoothLauncher.launch(
                Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            )
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                )
            )
        }
        setOnClickListeners()
        setPairedDeviceAdapter()
        setScanDeviceAdapter()
        observers()
    }

    private fun observers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    scanDeviceAdapter.submitList(state.scannedDevices)
                    pairedDeviceAdapter.submitList(state.pairedDevices)
                    when {
                        state.isConnecting -> {
                            Toast.makeText(context, "Loading", Toast.LENGTH_SHORT).show()
                        }

                        state.isConnected -> {
                            findNavController().navigate(R.id.fragmentChat)
                        }

                        else -> {

                        }
                    }
                }

            }
        }
    }

    private fun setPairedDeviceAdapter() {
        pairedDeviceAdapter = BluetoothDeviceAdapter(object : OnDeviceClicked {
            override fun onClick(deviceItem: BluetoothDeviceItem) {
                viewModel.connectToDevice(deviceItem)
            }

        })
        binding.pairDevicesRv.adapter = pairedDeviceAdapter
        binding.pairDevicesRv.layoutManager = LinearLayoutManager(context)
    }

    private fun setScanDeviceAdapter() {
        scanDeviceAdapter = BluetoothDeviceAdapter(object : OnDeviceClicked {
            override fun onClick(deviceItem: BluetoothDeviceItem) {
                viewModel.connectToDevice(deviceItem)
            }

        })
        binding.scanDevicesRv.adapter = scanDeviceAdapter
        binding.scanDevicesRv.layoutManager = LinearLayoutManager(context)
    }

    private fun setOnClickListeners() {
        binding.scanDevicesButton.setOnClickListener {
            viewModel.startScan()
        }
        binding.startServer.setOnClickListener {
            viewModel.waitForIncomingConnections()
        }
        binding.File.setOnClickListener {
            findNavController().navigate(R.id.fragmentFileTransfer)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}