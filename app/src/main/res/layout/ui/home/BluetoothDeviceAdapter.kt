package com.techuntried.bluetooth.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.techuntried.bluetooth.databinding.BluetoothDeviceItemBinding
import com.techuntried.bluetoothshare.domain.model.BluetoothDeviceItem
import com.techuntried.bluetoothshare.ui.home.BluetoothDeviceAdapter
import com.techuntried.bluetoothshare.ui.home.BluetoothDeviceDiffUtil
import com.techuntried.bluetoothshare.ui.home.OnDeviceClicked


class BluetoothDeviceAdapter(private val onDeviceClicked: OnDeviceClicked) :
    ListAdapter<BluetoothDeviceItem, BluetoothDeviceAdapter.MyViewHolder>(BluetoothDeviceDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position),onDeviceClicked)
    }

    class MyViewHolder private constructor(private val binding: BluetoothDeviceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: BluetoothDeviceItem,
            onDeviceClicked: OnDeviceClicked
        ) {
            binding.item = item
            binding.root.setOnClickListener {
                onDeviceClicked.onClick(item)
            }
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = BluetoothDeviceItemBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(view)

            }
        }
    }

}

interface OnDeviceClicked {
    fun onClick(deviceItem: BluetoothDeviceItem)
}

class BluetoothDeviceDiffUtil : DiffUtil.ItemCallback<BluetoothDeviceItem>() {
    override fun areItemsTheSame(
        oldItem: BluetoothDeviceItem,
        newItem: BluetoothDeviceItem
    ): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(
        oldItem: BluetoothDeviceItem,
        newItem: BluetoothDeviceItem
    ): Boolean {
        return oldItem == newItem
    }

}

