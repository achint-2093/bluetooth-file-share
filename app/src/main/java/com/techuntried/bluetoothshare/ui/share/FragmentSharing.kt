package com.techuntried.bluetoothshare.ui.share

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.techuntried.bluetoothshare.databinding.FragmentSharingBinding

class FragmentSharing : Fragment() {

    private var _binding: FragmentSharingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSharingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}