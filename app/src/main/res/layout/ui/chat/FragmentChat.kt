package com.techuntried.bluetooth.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.techuntried.bluetooth.R
import com.techuntried.bluetooth.databinding.FragmentChatBinding
import com.techuntried.bluetooth.ui.BluetoothViewModel
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentChat : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MessagesListAdapter
    private val viewModel: BluetoothViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        setRecyclerViewAdapter()
        observers()
    }

    private fun setRecyclerViewAdapter() {
        adapter = MessagesListAdapter()
        binding.messagesRecyclerView.adapter = adapter
        binding.messagesRecyclerView.layoutManager = LinearLayoutManager(context)

    }

    private fun observers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect {state->
                    adapter.submitList(state.messages)
                    when {
                        state.isConnecting -> {
                            Toast.makeText(context, "Loading", Toast.LENGTH_SHORT).show()
                        }

                        state.isConnected -> {

                        }



                        else -> {
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.disconnectChat.setOnClickListener {
            viewModel.disconnectFromDevice()
        }
        binding.sendMessageButton.setOnClickListener {
            val message = binding.editText.text.toString()
            viewModel.sendMessage(message)
            binding.editText.text.clear()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}