package com.techuntried.bluetoothshare.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.techuntried.bluetoothshare.databinding.FragmentHomeBinding
import com.techuntried.bluetoothshare.util.ConnectionType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentHome : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
    }


    private fun setOnClickListeners() {
        binding.sendButton.setOnClickListener {
            val action =
                FragmentHomeDirections.actionFragmentHomeToFragmentConnection(
                    ConnectionType.Sender
                )
            findNavController().navigate(action)
        }
        binding.receiveButton.setOnClickListener {
            val action =
                FragmentHomeDirections.actionFragmentHomeToFragmentConnection(
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