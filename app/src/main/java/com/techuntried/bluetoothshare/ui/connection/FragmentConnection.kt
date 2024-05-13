package com.techuntried.bluetoothshare.ui.connection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.techuntried.bluetooth.ui.BluetoothViewModel
import com.techuntried.bluetoothshare.databinding.FragmentConnectionBinding
import com.techuntried.bluetoothshare.domain.model.BluetoothDeviceItem
import com.techuntried.bluetoothshare.ui.home.BluetoothDeviceAdapter
import com.techuntried.bluetoothshare.ui.home.OnDeviceClicked
import com.techuntried.bluetoothshare.util.ConnectionType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentConnection : Fragment() {

    private var _binding: FragmentConnectionBinding? = null
    private val binding get() = _binding!!
    private val arguments by navArgs<FragmentConnectionArgs>()
    private val viewModel: BluetoothViewModel by viewModels()
    private lateinit var adapter: BluetoothDeviceAdapter
    private var connectionType: ConnectionType? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConnectionBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectionType = arguments.connectionType

        if (connectionType == ConnectionType.Sender) {
            viewModel.startScan()
        } else if (connectionType == ConnectionType.Receiver) {
            viewModel.waitForIncomingConnections()
        }

        setOnClickListeners()
        setRecyclerViewAdapter()
        observers()
    }

    private fun setOnClickListeners() {
    }

    private fun observers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    if (state.scannedDevices.isNotEmpty()) {
                        if (connectionType == ConnectionType.Sender)
                            adapter.submitList(state.scannedDevices)
                    }
//                    state.connectionState?.let {
//                        binding.infoText.text = it.toString()
//                    }

                    if (state.errorMessage!=null){
                        Toast.makeText(context, state.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setRecyclerViewAdapter() {
        adapter = BluetoothDeviceAdapter(object : OnDeviceClicked {
            override fun onClick(deviceItem: BluetoothDeviceItem) {

            }
        })
        binding.devicesRecyclerView.adapter = adapter
        binding.devicesRecyclerView.layoutManager = LinearLayoutManager(context)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}