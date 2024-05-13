package com.techuntried.bluetooth.ui.file

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.techuntried.bluetooth.databinding.FragmentFileTransferBinding
import com.techuntried.bluetoothshare.util.ConnectionType

class FragmentFileTransfer : Fragment() {

    private var _binding: FragmentFileTransferBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFileTransferBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.sendButton.setOnClickListener {
            val action =
                FragmentFileTransferDirections.actionFragmentFileTransferToFragmentConnection(
                    ConnectionType.Sender
                )
            findNavController().navigate(action)
        }
        binding.receiveButton.setOnClickListener {
            val action =
                FragmentFileTransferDirections.actionFragmentFileTransferToFragmentConnection(
                    ConnectionType.Receiver
                )
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}